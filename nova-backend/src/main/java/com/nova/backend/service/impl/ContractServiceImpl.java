package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.backend.common.ContractStatus;
import com.nova.backend.dto.*;
import com.nova.backend.entity.Child;
import com.nova.backend.entity.Contract;
import com.nova.backend.entity.ContractChangeLog;
import com.nova.backend.entity.ContractFeeItem;
import com.nova.backend.entity.ContractOtherFee;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.ChildMapper;
import com.nova.backend.mapper.ContractChangeLogMapper;
import com.nova.backend.mapper.ContractFeeItemMapper;
import com.nova.backend.mapper.ContractMapper;
import com.nova.backend.mapper.ContractOtherFeeMapper;
import com.nova.backend.service.ContractService;
import com.nova.backend.util.SnowflakeIdGenerator;
import com.nova.backend.vo.ContractVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 合同服务实现类
 *
 * @author Nova
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final ContractFeeItemMapper contractFeeItemMapper;
    private final ContractOtherFeeMapper contractOtherFeeMapper;
    private final ContractChangeLogMapper contractChangeLogMapper;
    private final ChildMapper childMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<ContractVO> pageList(ContractQueryDTO queryDTO) {
        Page<Contract> page = new Page<>(
                queryDTO.getPage() != null ? queryDTO.getPage() : 1,
                queryDTO.getSize() != null ? queryDTO.getSize() : 10
        );

        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(queryDTO.getContractNo()), Contract::getContractNo, queryDTO.getContractNo())
                .like(StringUtils.hasText(queryDTO.getContractName()), Contract::getContractName, queryDTO.getContractName())
                .eq(queryDTO.getChildId() != null, Contract::getChildId, queryDTO.getChildId())
                .eq(StringUtils.hasText(queryDTO.getCourseType()), Contract::getCourseType, queryDTO.getCourseType())
                .eq(queryDTO.getStatus() != null, Contract::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getStartDateBegin() != null, Contract::getStartDate, queryDTO.getStartDateBegin())
                .le(queryDTO.getStartDateEnd() != null, Contract::getStartDate, queryDTO.getStartDateEnd())
                .orderByDesc(Contract::getCreateTime);

        Page<Contract> result = contractMapper.selectPage(page, queryWrapper);

        Page<ContractVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public List<ContractVO> listAll() {
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contract::getStatus, ContractStatus.ACTIVE)
                .orderByDesc(Contract::getCreateTime);
        List<Contract> contracts = contractMapper.selectList(queryWrapper);
        return contracts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ContractVO getDetailById(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        ContractVO vo = convertToVO(contract);
        vo.setFeeItems(getFeeItemsByContractId(id));
        vo.setOtherFees(getOtherFeesByContractId(id));
        vo.setChangeLogs(getChangeLogsByContractId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(ContractDTO dto) {
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contract::getChildId, dto.getChildId())
                .eq(Contract::getStatus, ContractStatus.ACTIVE);
        Contract existingContract = contractMapper.selectOne(queryWrapper);
        if (existingContract != null) {
            throw new BusinessException("该幼儿已有生效中的合同，无法重复签订");
        }

        Contract contract = new Contract();
        BeanUtils.copyProperties(dto, contract);
        contract.setContractNo(generateContractNo());
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setCreateTime(LocalDateTime.now());
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.insert(contract);

        saveFeeItems(contract.getId(), dto.getFeeItems());
        saveOtherFees(contract.getId(), dto.getOtherFees());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeContract(ContractChangeDTO dto) {
        Contract originalContract = contractMapper.selectById(dto.getOriginalContractId());
        if (originalContract == null) {
            throw new BusinessException("原合同不存在");
        }
        if (!ContractStatus.ACTIVE.equals(originalContract.getStatus())) {
            throw new BusinessException("只有生效中的合同才能进行变更");
        }

        LocalDate nextMonthFirstDay = getNextMonthFirstDay();
        originalContract.setStatus(ContractStatus.CHANGED);
        originalContract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(originalContract);

        Contract newContract = new Contract();
        newContract.setContractNo(generateContractNo());
        newContract.setChildId(originalContract.getChildId());
        newContract.setContractName(dto.getContractName());
        newContract.setCourseType(dto.getCourseType());
        newContract.setStartDate(nextMonthFirstDay);
        newContract.setEndDate(dto.getEndDate());
        newContract.setAttachmentUrl(dto.getAttachmentUrl());
        newContract.setStatus(ContractStatus.ACTIVE);
        newContract.setCreateTime(LocalDateTime.now());
        newContract.setUpdateTime(LocalDateTime.now());
        contractMapper.insert(newContract);

        saveFeeItems(newContract.getId(), dto.getFeeItems());
        saveOtherFees(newContract.getId(), dto.getOtherFees());

        saveChangeLog(originalContract.getId(), "CHANGE", dto.getChangeReason(), originalContract, newContract);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean terminate(Long id, String reason) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        if (!ContractStatus.ACTIVE.equals(contract.getStatus())) {
            throw new BusinessException("只有生效中的合同才能终止");
        }

        String beforeContent = toJson(contract);
        contract.setStatus(ContractStatus.TERMINATED);
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);

        saveChangeLog(id, "TERMINATE", reason, beforeContent, null);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean renew(ContractRenewDTO dto) {
        Contract originalContract = contractMapper.selectById(dto.getOriginalContractId());
        if (originalContract == null) {
            throw new BusinessException("原合同不存在");
        }
        if (!ContractStatus.ACTIVE.equals(originalContract.getStatus()) && !ContractStatus.EXPIRED.equals(originalContract.getStatus())) {
            throw new BusinessException("只有生效中或已过期的合同才能续签");
        }

        if (dto.getStartDate().isBefore(originalContract.getEndDate()) 
                || dto.getStartDate().isEqual(originalContract.getEndDate())) {
            throw new BusinessException("续签合同的开始日期必须在原合同结束日期之后");
        }

        originalContract.setStatus(ContractStatus.EXPIRED);
        originalContract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(originalContract);

        Contract newContract = new Contract();
        newContract.setContractNo(generateContractNo());
        newContract.setChildId(originalContract.getChildId());
        newContract.setContractName(dto.getContractName());
        newContract.setCourseType(originalContract.getCourseType());
        newContract.setStartDate(dto.getStartDate());
        newContract.setEndDate(dto.getEndDate());
        newContract.setAttachmentUrl(dto.getAttachmentUrl());
        newContract.setStatus(ContractStatus.ACTIVE);
        newContract.setCreateTime(LocalDateTime.now());
        newContract.setUpdateTime(LocalDateTime.now());
        contractMapper.insert(newContract);

        saveFeeItems(newContract.getId(), dto.getFeeItems());
        saveOtherFees(newContract.getId(), dto.getOtherFees());

        saveChangeLog(originalContract.getId(), "RENEW", "合同续签", originalContract, newContract);

        return true;
    }

    @Override
    public ContractVO getActiveContractByChildId(Long childId) {
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contract::getChildId, childId)
                .eq(Contract::getStatus, ContractStatus.ACTIVE)
                .orderByDesc(Contract::getCreateTime)
                .last("LIMIT 1");
        Contract contract = contractMapper.selectOne(queryWrapper);
        if (contract == null) {
            return null;
        }
        ContractVO vo = convertToVO(contract);
        vo.setFeeItems(getFeeItemsByContractId(contract.getId()));
        vo.setOtherFees(getOtherFeesByContractId(contract.getId()));
        return vo;
    }

    @Override
    public BigDecimal getFeeByAge(Long contractId, Integer ageMonths) {
        LambdaQueryWrapper<ContractFeeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractFeeItem::getContractId, contractId);
        List<ContractFeeItem> feeItems = contractFeeItemMapper.selectList(queryWrapper);

        if (feeItems == null || feeItems.isEmpty()) {
            throw new BusinessException("合同未配置年龄段收费明细");
        }

        ContractFeeItem matchedItem = null;
        for (ContractFeeItem item : feeItems) {
            int[] range = parseAgeRange(item.getAgeRange());
            if (range != null && ageMonths >= range[0] && ageMonths <= range[1]) {
                matchedItem = item;
                break;
            }
        }

        if (matchedItem == null) {
            for (ContractFeeItem item : feeItems) {
                if (item.getAgeRange().contains("以上")) {
                    int[] range = parseAgeRange(item.getAgeRange());
                    if (range != null && ageMonths >= range[0]) {
                        matchedItem = item;
                        break;
                    }
                }
            }
        }

        if (matchedItem == null) {
            throw new BusinessException("未找到对应年龄段的收费标准");
        }

        return matchedItem.getEducationFee().add(matchedItem.getMealFee());
    }

    private String generateContractNo() {
        String dateStr = LocalDate.now().toString().replace("-", "");
        long sequence = SnowflakeIdGenerator.generateId() % 10000;
        return String.format("HT%s%04d", dateStr, sequence);
    }

    private LocalDate getNextMonthFirstDay() {
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        return nextMonth.atDay(1);
    }

    private ContractVO convertToVO(Contract contract) {
        ContractVO vo = new ContractVO();
        BeanUtils.copyProperties(contract, vo);
        vo.setId(contract.getId().toString());
        if (contract.getChildId() != null) {
            Child child = childMapper.selectById(contract.getChildId());
            if (child != null) {
                vo.setChildName(child.getName());
            }
        }
        return vo;
    }

    private List<ContractVO.ContractFeeItemVO> getFeeItemsByContractId(Long contractId) {
        LambdaQueryWrapper<ContractFeeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractFeeItem::getContractId, contractId);
        List<ContractFeeItem> items = contractFeeItemMapper.selectList(queryWrapper);
        return items.stream().map(item -> {
            ContractVO.ContractFeeItemVO vo = new ContractVO.ContractFeeItemVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private List<ContractVO.ContractOtherFeeVO> getOtherFeesByContractId(Long contractId) {
        LambdaQueryWrapper<ContractOtherFee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractOtherFee::getContractId, contractId);
        List<ContractOtherFee> otherFees = contractOtherFeeMapper.selectList(queryWrapper);
        return otherFees.stream().map(otherFee -> {
            ContractVO.ContractOtherFeeVO vo = new ContractVO.ContractOtherFeeVO();
            BeanUtils.copyProperties(otherFee, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private List<ContractVO.ContractChangeLogVO> getChangeLogsByContractId(Long contractId) {
        LambdaQueryWrapper<ContractChangeLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractChangeLog::getContractId, contractId)
                .orderByDesc(ContractChangeLog::getCreateTime);
        List<ContractChangeLog> changeLogs = contractChangeLogMapper.selectList(queryWrapper);
        return changeLogs.stream().map(log -> {
            ContractVO.ContractChangeLogVO vo = new ContractVO.ContractChangeLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private void saveFeeItems(Long contractId, List<ContractFeeItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (ContractFeeItemDTO item : items) {
            ContractFeeItem entity = new ContractFeeItem();
            BeanUtils.copyProperties(item, entity);
            entity.setId(null);
            entity.setContractId(contractId);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            contractFeeItemMapper.insert(entity);
        }
    }

    private void saveOtherFees(Long contractId, List<ContractOtherFeeDTO> otherFees) {
        if (otherFees == null || otherFees.isEmpty()) {
            return;
        }
        for (ContractOtherFeeDTO otherFee : otherFees) {
            ContractOtherFee entity = new ContractOtherFee();
            BeanUtils.copyProperties(otherFee, entity);
            entity.setId(null);
            entity.setContractId(contractId);
            entity.setStatus(otherFee.getStatus() != null ? otherFee.getStatus() : 1);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            contractOtherFeeMapper.insert(entity);
        }
    }

    private void saveChangeLog(Long contractId, String changeType, String changeReason, 
                               Contract beforeContract, Contract afterContract) {
        ContractChangeLog changeLog = new ContractChangeLog();
        changeLog.setContractId(contractId);
        changeLog.setChangeType(changeType);
        changeLog.setChangeReason(changeReason);
        changeLog.setBeforeContent(toJson(beforeContract));
        changeLog.setAfterContent(afterContract != null ? toJson(afterContract) : null);
        changeLog.setCreateTime(LocalDateTime.now());
        contractChangeLogMapper.insert(changeLog);
    }

    private void saveChangeLog(Long contractId, String changeType, String changeReason, 
                               String beforeContent, String afterContent) {
        ContractChangeLog changeLog = new ContractChangeLog();
        changeLog.setContractId(contractId);
        changeLog.setChangeType(changeType);
        changeLog.setChangeReason(changeReason);
        changeLog.setBeforeContent(beforeContent);
        changeLog.setAfterContent(afterContent);
        changeLog.setCreateTime(LocalDateTime.now());
        contractChangeLogMapper.insert(changeLog);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return null;
        }
    }

    private int[] parseAgeRange(String ageRange) {
        if (ageRange == null || ageRange.isEmpty()) {
            return null;
        }
        
        if (ageRange.contains("以上")) {
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(ageRange);
            if (matcher.find()) {
                int min = Integer.parseInt(matcher.group(1));
                return new int[]{min, Integer.MAX_VALUE};
            }
        }
        
        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(ageRange);
        if (matcher.find()) {
            int min = Integer.parseInt(matcher.group(1));
            int max = Integer.parseInt(matcher.group(2));
            return new int[]{min, max};
        }
        
        return null;
    }
}

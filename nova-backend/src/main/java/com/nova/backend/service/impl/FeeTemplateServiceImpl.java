package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.backend.dto.FeeTemplateDTO;
import com.nova.backend.dto.FeeTemplateItemDTO;
import com.nova.backend.dto.OtherFeeTemplateDTO;
import com.nova.backend.entity.Attendance;
import com.nova.backend.entity.FeeTemplate;
import com.nova.backend.entity.FeeTemplateItem;
import com.nova.backend.entity.OtherFeeTemplate;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.AttendanceMapper;
import com.nova.backend.mapper.FeeTemplateItemMapper;
import com.nova.backend.mapper.FeeTemplateMapper;
import com.nova.backend.mapper.OtherFeeTemplateMapper;
import com.nova.backend.service.FeeTemplateService;
import com.nova.backend.vo.FeeTemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收费标准模板服务实现类
 *
 * @author Nova
 */
@Service
@RequiredArgsConstructor
public class FeeTemplateServiceImpl extends ServiceImpl<FeeTemplateMapper, FeeTemplate> implements FeeTemplateService {

    private final FeeTemplateMapper feeTemplateMapper;
    private final FeeTemplateItemMapper feeTemplateItemMapper;
    private final OtherFeeTemplateMapper otherFeeTemplateMapper;

    @Override
    public Page<FeeTemplateVO> pageList(Integer pageNum, Integer pageSize, String templateName, String courseType, Integer status) {
        Page<FeeTemplate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FeeTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(templateName), FeeTemplate::getTemplateName, templateName)
                .eq(StringUtils.hasText(courseType), FeeTemplate::getCourseType, courseType)
                .eq(status != null, FeeTemplate::getStatus, status)
                .orderByDesc(FeeTemplate::getCreateTime);
        Page<FeeTemplate> result = feeTemplateMapper.selectPage(page, queryWrapper);

        Page<FeeTemplateVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public FeeTemplateVO getDetailById(Long id) {
        FeeTemplate template = feeTemplateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        FeeTemplateVO vo = convertToVO(template);

        LambdaQueryWrapper<FeeTemplateItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(FeeTemplateItem::getTemplateId, id);
        List<FeeTemplateItem> items = feeTemplateItemMapper.selectList(itemWrapper);
        vo.setItems(items.stream().map(item -> {
            FeeTemplateVO.FeeTemplateItemVO itemVO = new FeeTemplateVO.FeeTemplateItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList()));

        LambdaQueryWrapper<OtherFeeTemplate> otherFeeWrapper = new LambdaQueryWrapper<>();
        otherFeeWrapper.eq(OtherFeeTemplate::getTemplateId, id);
        List<OtherFeeTemplate> otherFees = otherFeeTemplateMapper.selectList(otherFeeWrapper);
        vo.setOtherFees(otherFees.stream().map(otherFee -> {
            FeeTemplateVO.OtherFeeTemplateVO otherFeeVO = new FeeTemplateVO.OtherFeeTemplateVO();
            BeanUtils.copyProperties(otherFee, otherFeeVO);
            return otherFeeVO;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(FeeTemplateDTO dto) {
        FeeTemplate template = new FeeTemplate();
        BeanUtils.copyProperties(dto, template);
        template.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        template.setVersion(1);
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        feeTemplateMapper.insert(template);

        saveTemplateItems(template.getId(), dto.getItems());
        saveOtherFees(template.getId(), dto.getOtherFees());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(FeeTemplateDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("模板ID不能为空");
        }

        FeeTemplate oldTemplate = feeTemplateMapper.selectById(dto.getId());
        if (oldTemplate == null) {
            throw new BusinessException("模板不存在");
        }

        FeeTemplate newTemplate = new FeeTemplate();
        BeanUtils.copyProperties(dto, newTemplate);
        newTemplate.setId(null);
        newTemplate.setVersion(oldTemplate.getVersion() + 1);
        newTemplate.setStatus(oldTemplate.getStatus());
        newTemplate.setCreateTime(LocalDateTime.now());
        newTemplate.setUpdateTime(LocalDateTime.now());
        feeTemplateMapper.insert(newTemplate);

        saveTemplateItems(newTemplate.getId(), dto.getItems());
        saveOtherFees(newTemplate.getId(), dto.getOtherFees());

        return true;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        FeeTemplate template = feeTemplateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        template.setStatus(status);
        template.setUpdateTime(LocalDateTime.now());
        return feeTemplateMapper.updateById(template) > 0;
    }

    @Override
    public List<FeeTemplateVO> listEnabled() {
        LambdaQueryWrapper<FeeTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FeeTemplate::getStatus, 1)
                .orderByDesc(FeeTemplate::getCreateTime);
        List<FeeTemplate> templates = feeTemplateMapper.selectList(queryWrapper);
        return templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private FeeTemplateVO convertToVO(FeeTemplate template) {
        FeeTemplateVO vo = new FeeTemplateVO();
        BeanUtils.copyProperties(template, vo);
        vo.setId(String.valueOf(template.getId()));
        return vo;
    }

    private void saveTemplateItems(Long templateId, List<FeeTemplateItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (FeeTemplateItemDTO item : items) {
            FeeTemplateItem entity = new FeeTemplateItem();
            BeanUtils.copyProperties(item, entity);
            entity.setId(null);
            entity.setTemplateId(templateId);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            feeTemplateItemMapper.insert(entity);
        }
    }

    private void saveOtherFees(Long templateId, List<OtherFeeTemplateDTO> otherFees) {
        if (otherFees == null || otherFees.isEmpty()) {
            return;
        }
        for (OtherFeeTemplateDTO otherFee : otherFees) {
            OtherFeeTemplate entity = new OtherFeeTemplate();
            BeanUtils.copyProperties(otherFee, entity);
            entity.setId(null);
            entity.setTemplateId(templateId);
            entity.setStatus(otherFee.getStatus() != null ? otherFee.getStatus() : 1);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            otherFeeTemplateMapper.insert(entity);
        }
    }
}

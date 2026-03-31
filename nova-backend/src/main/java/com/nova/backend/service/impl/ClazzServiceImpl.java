package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.backend.entity.Clazz;
import com.nova.backend.mapper.ClazzMapper;
import com.nova.backend.service.ClazzService;
import com.nova.backend.vo.ClazzVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 班级服务实现
 *
 * @author Nova
 */
@Service
@RequiredArgsConstructor
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {

    private final ClazzMapper clazzMapper;

    @Override
    public Object pageList(Integer page, Integer limit, String className, Integer status) {
        Page<ClazzVO> pageInfo = new Page<>(page, limit);
        return clazzMapper.selectClazzPage(pageInfo, className, status);
    }

    @Override
    public List<Clazz> getOptions() {
        LambdaQueryWrapper<Clazz> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Clazz::getStatus, 1);
        queryWrapper.orderByAsc(Clazz::getClassName);
        return this.list(queryWrapper);
    }

    @Override
    public ClazzVO getClazzDetail(Long id) {
        return clazzMapper.selectClazzById(id);
    }
}

package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.backend.entity.Child;
import com.nova.backend.mapper.ChildMapper;
import com.nova.backend.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 幼儿服务实现
 *
 * @author Nova
 */
@Service
@RequiredArgsConstructor
public class ChildServiceImpl extends ServiceImpl<ChildMapper, Child> implements ChildService {

    @Override
    public Object pageList(Integer page, Integer limit, String name, Long classId, Integer status) {
        Page<Child> pageInfo = new Page<>(page, limit);
        
        LambdaQueryWrapper<Child> queryWrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(Child::getName, name);
        }
        if (classId != null) {
            queryWrapper.eq(Child::getClassId, classId);
        }
        if (status != null) {
            queryWrapper.eq(Child::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Child::getCreateTime);
        
        return this.page(pageInfo, queryWrapper);
    }

    @Override
    public List<Child> getChildList(Integer status) {
        LambdaQueryWrapper<Child> queryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            queryWrapper.eq(Child::getStatus, status);
        }
        queryWrapper.orderByDesc(Child::getCreateTime);
        return this.list(queryWrapper);
    }
}
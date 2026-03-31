package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.backend.entity.Child;

import java.util.List;

/**
 * 幼儿服务接口
 *
 * @author Nova
 */
public interface ChildService extends IService<Child> {

    /**
     * 分页查询幼儿列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param name 幼儿姓名
     * @param classId 班级ID
     * @param status 状态
     * @return 分页结果
     */
    Object pageList(Integer page, Integer limit, String name, Long classId, Integer status);

    /**
     * 获取幼儿列表（不分页）
     *
     * @param status 状态
     * @return 幼儿列表
     */
    List<Child> getChildList(Integer status);
}
package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.backend.entity.Clazz;
import com.nova.backend.vo.ClazzVO;

import java.util.List;

/**
 * 班级服务接口
 *
 * @author Nova
 */
public interface ClazzService extends IService<Clazz> {

    /**
     * 分页查询班级列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param className 班级名称
     * @param status 状态
     * @return 分页结果
     */
    Object pageList(Integer page, Integer limit, String className, Integer status);

    /**
     * 获取班级选项列表
     *
     * @return 班级列表
     */
    List<Clazz> getOptions();

    /**
     * 获取班级详情（包含班主任名称）
     *
     * @param id 班级ID
     * @return 班级详情
     */
    ClazzVO getClazzDetail(Long id);
}

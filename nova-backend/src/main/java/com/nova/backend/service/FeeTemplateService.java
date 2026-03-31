package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.FeeTemplateDTO;
import com.nova.backend.vo.FeeTemplateVO;

import java.util.List;

/**
 * 收费标准模板服务接口
 *
 * @author Nova
 */
public interface FeeTemplateService {

    /**
     * 分页查询模板列表
     *
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @param templateName 模板名称（模糊查询）
     * @param courseType  课程类型
     * @param status      状态
     * @return 分页结果
     */
    Page<FeeTemplateVO> pageList(Integer pageNum, Integer pageSize, String templateName, String courseType, Integer status);

    /**
     * 获取模板详情（包含年龄段明细和其他费用）
     *
     * @param id 模板ID
     * @return 模板详情
     */
    FeeTemplateVO getDetailById(Long id);

    /**
     * 新增模板（同时保存年龄段明细和其他费用）
     *
     * @param dto 模板数据传输对象
     * @return 是否成功
     */
    boolean save(FeeTemplateDTO dto);

    /**
     * 更新模板（生成新版本）
     *
     * @param dto 模板数据传输对象
     * @return 是否成功
     */
    boolean updateById(FeeTemplateDTO dto);

    /**
     * 更新模板状态
     *
     * @param id     模板ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 获取启用状态模板列表
     *
     * @return 启用模板列表
     */
    List<FeeTemplateVO> listEnabled();
}

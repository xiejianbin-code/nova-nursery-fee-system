package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.AlertQueryDTO;
import com.nova.backend.vo.AlertVO;

/**
 * 预警服务接口
 *
 * @author Nova
 */
public interface AlertService {

    /**
     * 分页查询预警列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<AlertVO> pageList(AlertQueryDTO queryDTO);

    /**
     * 获取未处理预警数量
     *
     * @return 未处理预警数量
     */
    Long getUnhandleCount();

    /**
     * 处理预警
     *
     * @param id    预警ID
     * @param remark 处理备注
     * @return 是否成功
     */
    boolean handle(Long id, String remark);

    /**
     * 创建预警
     *
     * @param childId 幼儿ID
     * @param type    预警类型
     * @param content 预警内容
     * @return 是否成功
     */
    boolean createAlert(Long childId, String type, String content);
}

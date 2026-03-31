package com.nova.backend.task;

import com.nova.backend.service.BillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 账单生成定时任务
 *
 * @author Nova
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BillGenerateTask {

    private final BillService billService;

    /**
     * 每月1日0点自动生成上月账单
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateMonthlyBills() {
        log.info("开始执行月度账单自动生成任务...");
        try {
            int count = billService.generateMonthlyBills();
            log.info("月度账单自动生成任务完成，共生成{}条账单", count);
        } catch (Exception e) {
            log.error("月度账单自动生成任务执行失败: {}", e.getMessage(), e);
        }
    }

}

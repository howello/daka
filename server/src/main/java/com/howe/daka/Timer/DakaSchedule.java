package com.howe.daka.Timer;


import com.howe.daka.service.AutoChargeService;
import com.howe.daka.service.DakaMainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 11:19 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@Component
public class DakaSchedule {
    @Resource
    private DakaMainService dakaMainService;

    @Resource
    private AutoChargeService autoChargeService;

//    @Scheduled(cron = "*/1 * * * * *")
    public void test() {
        System.out.println("System.currentTimeMillis() = " + System.currentTimeMillis());
    }

    /**
     * 打卡定时，半小时一次
     */
    @Scheduled(cron = "${howe.cron.daka.value}")
    public void dakaSchedule() {
        dakaMainService.doDakaPre();
    }

    @Scheduled(cron = "${howe.cron.charge.value}")
    public void chargeScedule(){
        autoChargeService.charge();
    }

    @Scheduled(cron = "${howe.cron.disCharge.value}")
    public void disChargeScedule(){
        autoChargeService.discharge();
    }
}

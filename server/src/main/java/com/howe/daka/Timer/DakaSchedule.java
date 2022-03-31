package com.howe.daka.Timer;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 11:19 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@Component
public class DakaSchedule {

    @Scheduled(cron = "*/1 * * * * *")
    public void  test(){
        System.out.println("System.currentTimeMillis() = " + System.currentTimeMillis());
    }
}

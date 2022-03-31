package com.howe.daka.service.impl;

import com.howe.daka.service.DakaScheduleService;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 14:18 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
public class DakaScheduleServiceImpl implements DakaScheduleService {

    @Resource
    private RestTemplate restTemplate;

    /**
     * 是否为法定工作日
     *
     * @return
     */
    @Override
    public boolean isWorkDay() {



        return false;
    }
}

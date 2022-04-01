package com.howe.daka.service.impl;

import com.howe.daka.service.AutoChargeService;
import com.howe.daka.service.InteractiveCmdService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>@Author lu
 * <p>@Date 2022/4/1 9:43 星期五
 * <p>@Version 1.0
 * <p>@Description
 */
@Service
public class AutoChargeServiceImpl implements AutoChargeService {
    @Resource
    private InteractiveCmdService interactiveCmdService;

    /**
     * 充电
     */
    @Override
    public void charge() {
        interactiveCmdService.chargeStep();
    }

    /**
     * 放电
     */
    @Override
    public void discharge() {
        interactiveCmdService.disChargeStep();
    }
}

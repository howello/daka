package com.howe.daka.service;

import cn.hutool.core.date.DateTime;
import com.howe.daka.dto.ServerJiangResponseDTO;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 14:16 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
public interface DakaMainService {

    /**
     * 预打卡
     */
    void doDakaPre();


    /**
     * 是否为法定工作日
     * @return
     */
    boolean isWorkDay(DateTime nowTime);

    /**
     * 是否在打卡时间范围内
     * @return
     */
    boolean isInDakaRange(DateTime nowTime);

    /**
     * 打卡成功通知
     */
    ServerJiangResponseDTO notifyDakaSuccess();

    /**
     * 开始打卡通知
     */
    void notifyStartDaka();
}

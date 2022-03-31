package com.howe.daka.service;

import org.springframework.stereotype.Service;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 14:16 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@Service
public interface DakaScheduleService {
    /**
     * 是否为法定工作日
     * @return
     */
    boolean isWorkDay();
}

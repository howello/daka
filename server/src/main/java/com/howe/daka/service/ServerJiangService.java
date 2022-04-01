package com.howe.daka.service;

import com.howe.daka.dto.ServerJiangResponseDTO;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 17:54 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
public interface ServerJiangService {
    /**
     * 发送微信通知
     * @param title
     * @param content
     * @return
     */
    ServerJiangResponseDTO send(String title, String content);
}

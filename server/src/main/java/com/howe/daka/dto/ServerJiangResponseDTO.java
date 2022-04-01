package com.howe.daka.dto;

import lombok.Data;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 17:58 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@Data
public class ServerJiangResponseDTO {
    private String pushid;
    private String readkey;
    private String error;
    private Integer errno;
}

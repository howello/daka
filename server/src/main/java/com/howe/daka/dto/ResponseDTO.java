package com.howe.daka.dto;

import lombok.Data;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 18:00 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@Data
public class ResponseDTO {
    private Integer code;
    private String message;
    private ServerJiangResponseDTO data;
}

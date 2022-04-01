package com.howe.daka.controller;

import com.alibaba.fastjson.JSONObject;
import com.howe.daka.dto.ServerJiangResponseDTO;
import com.howe.daka.service.DakaMainService;
import com.howe.daka.service.InteractiveCmdService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 17:35 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@RestController
public class DakaController {
    @Resource
    private DakaMainService dakaMainService;

    @Resource
    private InteractiveCmdService interactiveCmdService;

    @GetMapping("/dakaSuccess")
    public String success() {
        interactiveCmdService.sendOpenScreenCmd();
        ServerJiangResponseDTO serverJiangResponseDTO = dakaMainService.notifyDakaSuccess();
        return JSONObject.toJSONString(serverJiangResponseDTO);
    }
}

package com.howe.daka.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.howe.daka.dto.ServerJiangRequestDTO;
import com.howe.daka.dto.ServerJiangResponseDTO;
import com.howe.daka.service.ServerJiangService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 18:02 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@Service
public class ServerJiangServiceImpl implements ServerJiangService {
    @Resource
    private RestTemplate restTemplate;

    @Value("${howe.serverJiang.url}")
    private String url;

    @Value("${howe.serverJiang.channel}")
    private int channel;

    @Override
    public ServerJiangResponseDTO send(String title, String content) {
        ServerJiangRequestDTO req = new ServerJiangRequestDTO();
        req.setTitle(title);
        req.setDesp(content);
        req.setChannel(channel);
        HttpEntity<String> entity = new HttpEntity<>(JSONObject.toJSONString(req));
        ServerJiangResponseDTO serverJiangResponseDTO = null;
        try {
            ResponseEntity<ServerJiangResponseDTO> response = restTemplate.postForEntity(url, entity, ServerJiangResponseDTO.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                serverJiangResponseDTO = response.getBody();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return serverJiangResponseDTO;
    }
}

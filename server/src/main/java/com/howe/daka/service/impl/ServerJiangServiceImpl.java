package com.howe.daka.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.howe.daka.dto.ServerJiangRequestDTO;
import com.howe.daka.dto.ServerJiangResponseDTO;
import com.howe.daka.service.ServerJiangService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URLEncoder;

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

    @Value("${howe.serverJiang.SendKey}")
    private String SendKey;

    @Value("${howe.serverJiang.SendKeyBak}")
    private String SendKeyBak;

    @Value(".send")
    private String suffix;

    @Value("${howe.serverJiang.channel}")
    private int channel;

    @SneakyThrows
    @Override
    public ServerJiangResponseDTO send(String title, String content) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.set("title", title);
        map.set("desp", content);
        map.set("channel", channel);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        return doHttpRequestSend(url + SendKey + suffix, entity, 0);
    }

    private ServerJiangResponseDTO doHttpRequestSend(String url, HttpEntity<MultiValueMap<String, Object>> entity, int times) {
        ServerJiangResponseDTO serverJiangResponseDTO = null;
        try {
            ResponseEntity<ServerJiangResponseDTO> response = restTemplate.postForEntity(url, entity, ServerJiangResponseDTO.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                serverJiangResponseDTO = response.getBody();
                return serverJiangResponseDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (times == 0 && e.getMessage().contains("401")) {
                doHttpRequestSend(this.url + SendKeyBak + suffix, entity, 1);
            }
        }
        return null;
    }
}

package com.howe.daka.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.howe.daka.dto.ServerJiangResponseDTO;
import com.howe.daka.service.DakaMainService;
import com.howe.daka.service.InteractiveCmdService;
import com.howe.daka.service.ServerJiangService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 14:18 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@Service
public class DakaMainServiceImpl implements DakaMainService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private InteractiveCmdService interactiveCmdService;

    private static String workDayCache;

    @Resource
    private ServerJiangService serverJiangService;

    @Value("${howe.workdayApi.url}")
    private String workdayApiUrl;

    @Value("${howe.TimeRange.AM.start}")
    private String AMRangeStart;

    @Value("${howe.TimeRange.AM.end}")
    private String AMRangeEnd;

    @Value("${howe.TimeRange.PM.start}")
    private String PMRangeStart;

    @Value("${howe.TimeRange.PM.end}")
    private String PMRangeEnd;

    /**
     * 预打卡
     */
    @Override
    public void doDakaPre() {
        DateTime now = DateTime.now();
        if (!isWorkDay(now)) {
            return;
        }

        if (!isInDakaRange(now)) {
            return;
        }

        int delay = RandomUtil.randomInt(1, 20);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(() -> {
            System.out.println("预打卡");
            interactiveCmdService.doDaka();
            notifyStartDaka();
        }, delay, TimeUnit.MINUTES);
    }

    /**
     * 是否为法定工作日
     *
     * @return
     */
    @Override
    public boolean isWorkDay(DateTime nowTime) {
        String now = DateUtil.format(nowTime, "yyyyMMdd");
        if (StringUtils.isNotBlank(workDayCache)) {
            JSONObject json = JSONObject.parseObject(workDayCache);
            if (now.equalsIgnoreCase(json.getString("date"))) {
                return json.getBoolean("isWorkDay");
            }
        }

        try {
            JSONObject response = restTemplate.getForObject(workdayApiUrl + now, JSONObject.class);
            if (response != null) {
                Integer code = response.getInteger("code");
                if (code == 0) {
                    Integer integer = ((JSONObject) response.getJSONObject("data").getJSONArray("list").get(0)).getInteger("workday");
                    boolean isWorkDay = integer == 1;
                    JSONObject json = new JSONObject();
                    json.put("date", now);
                    json.put("isWorkDay", isWorkDay);
                    workDayCache = json.toJSONString();
                    return isWorkDay;
                }
            }
        } catch (RestClientException e) {
            return !DateUtil.isWeekend(nowTime);
        }
        return false;
    }

    /**
     * 是否在打卡时间范围内
     *
     * @return
     */
    @Override
    public boolean isInDakaRange(DateTime nowTime) {
        boolean AMTime = nowTime.compareTo(DateUtil.parse(AMRangeStart, "HH:mm:ss")) > 0 &&
                nowTime.compareTo(DateUtil.parse(AMRangeEnd, "HH:mm:ss")) < 0;
        boolean PMTime = nowTime.compareTo(DateUtil.parse(PMRangeStart, "HH:mm:ss")) > 0 &&
                nowTime.compareTo(DateUtil.parse(PMRangeEnd, "HH:mm:ss")) < 0;
        return AMTime || PMTime;
    }

    /**
     * 检测是否成功
     */
    @Override
    public ServerJiangResponseDTO notifyDakaSuccess() {
        JSONObject json = new JSONObject();
        json.put("time", DateUtil.format(DateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        json.put("status", "success");
        json.put("msg", "打卡成功");
       return serverJiangService.send("打卡成功", json.toJSONString());
    }

    /**
     * 开始打卡通知
     */
    @Override
    public void notifyStartDaka() {
        JSONObject json = new JSONObject();
        json.put("time", DateUtil.format(DateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        json.put("status", "Start");
        json.put("msg", "开始打卡");
        serverJiangService.send("开始打卡", json.toJSONString());
    }
}

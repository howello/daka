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
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
        if (isRestartTime(now)) {
            restartWifi();
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
                    Integer integer = response.getJSONObject("data").getJSONArray("list").getJSONObject(0).getInteger("workday");
                    boolean isWorkDay = integer == 1;
                    JSONObject json = new JSONObject();
                    json.put("date", now);
                    json.put("isWorkDay", isWorkDay);
                    workDayCache = json.toJSONString();
                    return isWorkDay;
                }
            }
        } catch (Exception e) {
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
        boolean AMTime = nowTime.compareTo(getRange(AMRangeStart)) > 0 &&
                nowTime.compareTo(getRange(AMRangeEnd)) < 0;
        boolean PMTime = nowTime.compareTo(getRange(PMRangeStart)) > 0 &&
                nowTime.compareTo(getRange(PMRangeEnd)) < 0;
        return AMTime || PMTime;
    }

    private DateTime getRange(String time) {
        return DateUtil.parse(DateUtil.formatDate(DateTime.now()) + " " + time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 重启wifi
     */
    private void restartWifi() {
        AtomicBoolean status= new AtomicBoolean(false);
        AtomicBoolean success = new AtomicBoolean(false);
        interactiveCmdService.toggleWifiStatus(line->{
            if ("ON".equalsIgnoreCase(line)) {
                status.set(true);
            } else if ("OFF".equalsIgnoreCase(line)) {
                status.set(false);
            }
            if ("SUCCESS".equalsIgnoreCase(line)) {
                success.set(true);
            } else {
                success.set(false);
            }
        });
        if (success.get() && status.get()) {
            restartWifi();
        }
    }

   private boolean isRestartTime(DateTime nowTime){
       DateTime AMRangeStartDate = getRange(AMRangeStart);
       DateTime PMRangeStartDate = getRange(PMRangeStart);
       boolean AMTime = nowTime.compareTo(DateUtil.offsetMinute(AMRangeStartDate, -30)) > 0 &&
               nowTime.compareTo(AMRangeStartDate) < 0;
       boolean PMTime = nowTime.compareTo(DateUtil.offsetMinute(PMRangeStartDate, -30)) > 0 &&
               nowTime.compareTo(PMRangeStartDate) < 0;
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

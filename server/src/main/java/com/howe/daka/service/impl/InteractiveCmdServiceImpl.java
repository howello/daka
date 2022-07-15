package com.howe.daka.service.impl;

import com.howe.daka.service.InteractiveCmdService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 16:12 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
@Service
public class InteractiveCmdServiceImpl implements InteractiveCmdService {

    @Value("${howe.cmd.openScreenCmd}")
    private String openScreenCmd;

    @Value("${howe.cmd.openApp}")
    private String openAppCmd;

    @Value("${howe.cmd.shutApp}")
    private String shutAppCmd;

    @Value("${howe.cmd.charge1Step}")
    private String charge1Step;

    @Value("${howe.cmd.charge2Step}")
    private String charge2Step;

    @Value("${howe.cmd.disCharge1Step}")
    private String disCharge1Step;

    @Value("${howe.cmd.disCharge2Step}")
    private String disCharge2Step;

    @Value("${howe.cmd.disCharge3Step}")
    private String disCharge3Step;

    @Value("${howe.cmd.disCharge4Step}")
    private String disCharge4Step;

    @Value("${howe.cmd.startDakaService}")
    private String startDakaService;

    @Value("${howe.cmd.toggleWifiStatus}")
    private String toggleWifiStatus;

    /**
     * 打卡
     */
    @Override
    @Async
    public void doDaka() {
        try {
            sendOpenScreenCmd();
            Thread.sleep(1000);
            openApp();
            Thread.sleep(3 * 1000);
            startDakaService();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点亮屏幕
     */
    @Override
    public void sendOpenScreenCmd() {
        try {
            Runtime.getRuntime().exec(openScreenCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开app
     */
    @Override
    public void openApp() {
        try {
            Runtime.getRuntime().exec(openAppCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭app
     */
    @Override
    public void shutApp() {
        try {
            Runtime.getRuntime().exec(shutAppCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复充电步骤
     */
    @Override
    public void chargeStep() {
        try {
            Runtime.getRuntime().exec(charge1Step);
            Runtime.getRuntime().exec(charge2Step);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断电步骤
     */
    @Override
    public void disChargeStep() {
        try {
            Runtime.getRuntime().exec(disCharge1Step);
            Runtime.getRuntime().exec(disCharge2Step);
            Runtime.getRuntime().exec(disCharge3Step);
            Runtime.getRuntime().exec(disCharge4Step);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动打卡服务
     */
    @Override
    public void startDakaService() {
        try {
            Runtime.getRuntime().exec(startDakaService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换wifi热点状态
     *
     * @param consumer
     */
    @SneakyThrows
    @Override
    public void toggleWifiStatus(Consumer<String> consumer) {
        excutePowerShellCmd(consumer,toggleWifiStatus);
    }

    /**
     * 执行powershell命令，带返回
     * @param consumer
     * @param cmdLine
     * @throws IOException
     */
    private void excutePowerShellCmd(Consumer<String> consumer, String cmdLine) throws IOException {
        Process powerShellProcess = Runtime.getRuntime().exec(cmdLine);
        powerShellProcess.getOutputStream().close();
        String line;
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                consumer.accept(line.trim());
            }
        }
        stdout.close();
    }
}

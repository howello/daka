package com.howe.daka.service;


import java.util.function.Consumer;

/**
 * <p>@Author lu
 * <p>@Date 2022/3/31 16:11 星期四
 * <p>@Version 1.0
 * <p>@Description
 */
public interface InteractiveCmdService {

    /**
     * 打卡
     */
    void  doDaka();

    /**
     * 点亮屏幕
     */
    void sendOpenScreenCmd();

    /**
     * 打开app
     */
    void openApp();

    /**
     * 关闭app
     */
    void shutApp();

    /**
     * 恢复充电步骤
     */
    void chargeStep();

    /**
     * 断电步骤
     */
    void disChargeStep();

    /**
     * 启动打卡服务
     */
    void startDakaService();

    /**
     * 切换wifi热点状态
     * @param consumer
     */
    void toggleWifiStatus(Consumer<String> consumer);

    void startGnirehtetService();

    void stopGnirehtetService();

    void queryGnirehtetService(Consumer<String> consumer);
}

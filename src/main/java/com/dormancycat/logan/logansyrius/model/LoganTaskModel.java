package com.dormancycat.logan.logansyrius.model;

import lombok.Data;

/**
 * 功能描述:  <p></p>
 *
 * @version 1.0 2019-10-07
 * @since logan-web 1.0
 */
@Data
public class LoganTaskModel {

    private long taskId;

    private String amount;

    private String appId;

    private String unionId;

    private String environment;

    private int platform;

    private String buildVersion;

    private String appVersion;

    private String deviceId;
    /**
     * 日志所属天
     */
    private long logDate;
    /**
     * 文件名
     */
    private String logFileName;
    /**
     * 日志上报时间
     */
    private long addTime;

}

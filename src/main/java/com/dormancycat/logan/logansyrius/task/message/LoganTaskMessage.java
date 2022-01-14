package com.dormancycat.logan.logansyrius.task.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoganTaskMessage {
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

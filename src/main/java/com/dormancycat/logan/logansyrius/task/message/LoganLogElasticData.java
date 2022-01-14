package com.dormancycat.logan.logansyrius.task.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoganLogElasticData {

    private String amount;

    private String appId;

    private String unionId;

    private String platform;

    private String buildVersion;

    private String appVersion;

    private String deviceId;

    /**
     * 日志所属天
     */
    @Field(type = FieldType.Date)
    private Date logDate;
    /**
     * 文件名
     */
    private String logFileName;
    /**
     * 日志上报时间
     */
    @Field(type = FieldType.Date)
    private Date addTime;

    private String content;//content
    private String logType;//logType

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZ")
    private Date logTime;//logTime

    private String theadName;//theadName
    private String threadId;//threadid
    private String isMainThread;//是否是主线程
}

package com.dormancycat.logan.logansyrius.task.listener;

import com.alibaba.fastjson.JSON;
import com.dormancycat.logan.logansyrius.enums.PlatformEnum;
import com.dormancycat.logan.logansyrius.model.LoganLogItem;
import com.dormancycat.logan.logansyrius.task.message.LoganLogElasticData;
import com.dormancycat.logan.logansyrius.util.FileUtil;
import com.dormancycat.logan.logansyrius.task.message.LoganTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Service
public class ListenerConfiguration {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Value("${elasticsearch.save.batchSize:50}")
    private Integer batchSize;


    @KafkaListener(
            id = "task",
            topics = "${spring.kafka.logan.file.parsing.topic}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(LoganTaskMessage message, Acknowledgment ack) {
        log.info("listener message {}",message);
        File file = FileUtil.getFile(message.getLogFileName());
        if (file == null || !file.exists()) {
            log.info("file is null or file not exists {}",message.getLogFileName());
            return;
        }
        try {
            String indexName = getIndexName(message);

            ArrayList<LoganLogElasticData> dataList = new ArrayList<>();

            try (InputStream in = new FileInputStream(file);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    LoganLogElasticData elasticData = transferToEsData(message, str);
                    dataList.add(elasticData);
                    if (dataList.size() >= batchSize) {
                        elasticsearchTemplate.save(dataList, IndexCoordinates.of(indexName));
                        dataList.clear();
                    }
                }
            } catch (Exception e) {
                log.error("read log file error ", e);
            } finally {
                if (null != dataList && !dataList.isEmpty()) {
                    elasticsearchTemplate.save(dataList, IndexCoordinates.of(indexName));
                    dataList.clear();
                }
            }

            file.deleteOnExit();

        } catch (Exception e) {
            log.error("message listener error ", e);
        } finally {
            log.info("ack task :{}", message);
            ack.acknowledge();
        }
    }

    private LoganLogElasticData transferToEsData(LoganTaskMessage message, String str) {
        LoganLogItem loganLogItem = JSON.parseObject(str, LoganLogItem.class);


        LoganLogElasticData elasticData = LoganLogElasticData.builder()
                .amount(message.getAmount())
                .appId(message.getAppId())
                .unionId(message.getUnionId())
                .platform(PlatformEnum.valueOfPlatform(message.getPlatform()).getDesc())
                .buildVersion(message.getBuildVersion())
                .appVersion(message.getAppVersion())
                .deviceId(message.getDeviceId())
                .logDate(new Date(message.getLogDate()))
                .addTime(new Date(message.getAddTime()))
                .content(loganLogItem.getC())
                .logType(loganLogItem.getF())
                .logTime(new Date(Long.parseLong(loganLogItem.getL())))
//                .logTime(DateTimeUtil.formatDateTime(new Date(Long.parseLong(loganLogItem.getL()))))
                .theadName(loganLogItem.getN())
                .threadId(loganLogItem.getI())
                .isMainThread(loganLogItem.getM())
                .build();
        return elasticData;
    }

    private String getIndexName(LoganTaskMessage message) {
        String environment = message.getEnvironment();
        if (StringUtils.isBlank(environment)) {
            environment = message.getBuildVersion();
        }
        String appId = message.getAppId();
        String indexName = appId + "_" + environment;
        indexName = indexName.toLowerCase();
        IndexOperations indexOps = elasticsearchTemplate.indexOps(IndexCoordinates.of(indexName));
        if (!indexOps.exists()) {
            createIndex(indexOps);
        }
        return indexName;
    }

    private synchronized void createIndex(IndexOperations indexOps) {
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(LoganLogElasticData.class);
        }
    }

}

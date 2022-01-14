package com.dormancycat.logan.logansyrius.task.impl;

import com.dormancycat.logan.logansyrius.model.LoganTaskModel;
import com.dormancycat.logan.logansyrius.task.LoganTaskModelService;
import com.dormancycat.logan.logansyrius.task.message.LoganTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class LoganTaskModelServiceImpl implements LoganTaskModelService {


    @Value("${spring.kafka.logan.file.parsing.topic}")
    private String topic;

    @Resource
    private KafkaTemplate<String, LoganTaskMessage> loganTaskKafkaTemplate;

    @Override
    public void submitTask(LoganTaskModel model) {
        try {
            LoganTaskMessage loganTaskMessage = new LoganTaskMessage();
            BeanUtils.copyProperties(model,loganTaskMessage);
            loganTaskKafkaTemplate
                    .send(topic, loganTaskMessage)
                    .completable()
                    .get(10, TimeUnit.SECONDS);
            log.info("send message to kafka success.");
        } catch (ExecutionException e) {
            log.error("send message error ",e);
            throw new IllegalStateException("MQ.Server.Error", e);
        } catch (InterruptedException e) {
            log.error("send message error ",e);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Mq.Service.Interrupted", e);
        } catch (TimeoutException e) {
            log.error("send message error ",e);
            throw new IllegalStateException("Mq.Service.TimeOut", e);
        }
    }
}

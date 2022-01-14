package com.dormancycat.logan.logansyrius.task.listener;

import com.dormancycat.logan.logansyrius.task.message.LoganTaskMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafakaConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean
    public ConsumerFactory<String, LoganTaskMessage> kafkaConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), new JsonDeserializer<>(LoganTaskMessage.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LoganTaskMessage> kafkaListenerContainerFactory(ConsumerFactory<String, LoganTaskMessage> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, LoganTaskMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, LoganTaskMessage> loganTaskKafkaTemplate(ProducerFactory<String, LoganTaskMessage> pf) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaTemplate<>(pf, props);
    }

}

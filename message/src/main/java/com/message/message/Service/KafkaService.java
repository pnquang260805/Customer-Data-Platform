package com.message.message.Service;

import com.message.message.Serializer.JsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

@Service
@Slf4j
public class KafkaService {
    public boolean topicExisted(String topic, String bootstrapServer){
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        try (AdminClient admin = AdminClient.create(props)){
            ListTopicsResult topics = admin.listTopics();
            log.info("Checking topics in Kafka at {}", bootstrapServer);
            Set<String> names = topics.names().get();
            log.info("Available topics: {}", names);
            return names.contains(topic);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createTopic(String topicName, int numPartitions, short replicationFactor, String bootstrap){
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);

        try(AdminClient admin = AdminClient.create(props)){
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
            admin.createTopics(Collections.singleton(newTopic)).all().get();
            log.info("Topic {} has been created", topicName);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public <T> void sendMessage(String topic, String key, T data, String bootstrap){
        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        try(KafkaProducer<String, T> producer = new KafkaProducer<>(prop)){
            ProducerRecord<String, T> record = new ProducerRecord<>(topic, key, data);
            producer.send(record);
            log.info("Message has been sent");
        }
    }
}

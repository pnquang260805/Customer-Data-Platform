package com.message.message.Config;

import com.message.message.Service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class AppInit implements ApplicationRunner {
    @Autowired
    private KafkaService kafkaService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("AppInit started, checking topics...");
        String bootstrap = "localhost:9094";

        List<String> topics = Arrays.asList("order", "product", "customer", "category");
        for(String topic : topics){
            if(!kafkaService.topicExisted(topic, bootstrap)){
                kafkaService.createTopic(topic, 3, (short)1, bootstrap);
            }
        }
    }
}
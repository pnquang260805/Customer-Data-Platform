package com.message.message.Service;

import com.message.message.DTO.Customer;
import com.message.message.DTO.MockDatabase;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class CommonService {
    Random random = new Random();
    Faker faker = new Faker();
    @Autowired
    KafkaService kafkaService;
    private String bootstrap = "localhost:9094";

    @Scheduled(fixedDelay = 10000)
    public void createCustomers(){
        Set<String> customersId = new HashSet<>();
        for(int i = 0; i < random.nextInt(50); i++) {
            String id = UUID.randomUUID().toString();
            customersId.add(id);
            MockDatabase.customerId.add(id);
        }
        for(String id : customersId){
            Customer customer = Customer.builder()
                    .customerId(id)
                    .name(faker.name().fullName())
                    .address(genAddress())
                    .dob(new SimpleDateFormat("yyyy-MM-dd").format(faker.date().birthday(15, 90)))
                    .sex(faker.gender().binaryTypes())
                    .build();
            kafkaService.sendMessage("customer", "customer",customer, bootstrap);
        }
    }

    private String genAddress(){
        return faker.address().buildingNumber() + " " + faker.address().streetAddress() + " " + faker.address().cityName() + " " + faker.address().country();
    }
}

package com.message.message.Service;

import com.message.message.DTO.CustomerRequest;
import com.message.message.Entity.Customer;
import com.message.message.Mapper.CustomerMapper;
import com.message.message.Repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final KafkaService kafkaService;

    private static final String TOPIC = "customer";
    @Value("${kafka.bootstrap}")
    private String BOOTSTRAP;

    public Customer createCustomer(CustomerRequest request){
        Customer customer = customerRepository.save(customerMapper.toCustomer(request));
        log.info("Start send message");
        log.info("Bootstrap server: " + BOOTSTRAP + "======================================");
        kafkaService.sendMessage(TOPIC, "create_customer", customer, BOOTSTRAP);
        log.info("Sent==================================================================");
        return customer;
    }
}

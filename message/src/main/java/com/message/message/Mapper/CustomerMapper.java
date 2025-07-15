package com.message.message.Mapper;

import com.message.message.DTO.CustomerRequest;
import com.message.message.Entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerMapper {
    public Customer toCustomer(CustomerRequest request){
        if(request == null) {
            log.warn("Customer request is null");
            return null;
        }
        return Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .dob(request.getDob())
                .sex(request.getSex())
                .country(request.getCountry())
                .build();
    }
}

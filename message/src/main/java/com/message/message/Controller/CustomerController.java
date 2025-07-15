package com.message.message.Controller;

import com.message.message.DTO.CustomerRequest;
import com.message.message.Entity.Customer;
import com.message.message.Service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRequest request){
        Customer customer = customerService.createCustomer(request);
        return ResponseEntity.ok().body(customer);
    }
}

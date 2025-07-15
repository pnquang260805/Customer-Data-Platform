package com.message.message.Entity;

import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Customer {
    @JsonProperty("customerId")
    @Id
    private String customerId;
    private String name;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String dob;
    private String sex;
    private String country;
    private String cartId;
}

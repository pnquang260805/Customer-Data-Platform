package com.message.message.DTO;

import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("dob")
    private String dob;

    @JsonProperty("sex")
    private String sex;
}

package com.message.message.DTO;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public class Order {
    String id;
    String customerId;
    Set<String> productsId;
    BigDecimal totalAmount;
}

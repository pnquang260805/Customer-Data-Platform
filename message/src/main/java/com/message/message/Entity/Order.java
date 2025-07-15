package com.message.message.Entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Document
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    String orderId;
    String customerId;
    Set<String> productsId;
    BigDecimal totalAmount;
}

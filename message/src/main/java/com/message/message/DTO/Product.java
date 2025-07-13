package com.message.message.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    String id;
    String name;
    BigDecimal price;
    Long quantityInStock;
    Integer star;
}

package com.message.message.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    String name;
    String email;
    String addressLine1;
    String addressLine2;
    String dob;
    String country;
    String sex; // male, man, men, Male, mAle, ....
}

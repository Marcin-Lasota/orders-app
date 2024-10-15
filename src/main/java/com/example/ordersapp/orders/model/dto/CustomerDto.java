package com.example.ordersapp.orders.model.dto;

import com.example.ordersapp.orders.model.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import org.hibernate.validator.constraints.Length;

/**
 * DTO for {@link Customer}
 */
public record CustomerDto(
    Long id,
    @Length(min = 2, max = 50) String firstName,
    @Length(min = 2, max = 50) String lastName,
    @Email String email,
    @Length(min = 2, max = 50) String address,
    @Length(min = 2, max = 50) String city,
    @Pattern(regexp = "^\\d{2}-\\d{3}$") String postalCode,
    String country,
    @Pattern(regexp = "^\\+?\\d{10,15}$") String phone
) implements Serializable {

}

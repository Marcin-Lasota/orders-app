package com.example.ordersapp.orders.model;

import com.example.ordersapp.shared.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "customers")
public class Customer extends BaseEntity {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String address;
    @NotNull
    private String city;
    @NotNull
    private String postalCode;
    @NotNull
    private String country;
    @NotNull
    private String phone;
}

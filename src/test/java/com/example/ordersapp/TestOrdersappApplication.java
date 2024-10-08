package com.example.ordersapp;

import org.springframework.boot.SpringApplication;

public class TestOrdersappApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrdersappApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

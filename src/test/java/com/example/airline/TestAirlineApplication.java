package com.example.airline;

import org.springframework.boot.SpringApplication;

public class TestAirlineApplication {

    public static void main(String[] args) {
        SpringApplication.from(AirlineApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

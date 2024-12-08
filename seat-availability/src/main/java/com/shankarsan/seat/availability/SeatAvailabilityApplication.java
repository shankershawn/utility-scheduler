package com.shankarsan.seat.availability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.shankarsan")
public class SeatAvailabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatAvailabilityApplication.class, args);
    }

}

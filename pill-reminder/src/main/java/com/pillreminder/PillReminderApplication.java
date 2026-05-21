package com.pillreminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PillReminderApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                PillReminderApplication.class,
                args
        );
    }
}
package com.newsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NewsDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsDashboardApplication.class, args);
        System.out.println("News Dashboard Backend Started Successfully! 🚀");
    }

}

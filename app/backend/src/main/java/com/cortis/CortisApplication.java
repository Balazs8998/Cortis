package com.cortis;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CortisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CortisApplication.class, args);

    }

}

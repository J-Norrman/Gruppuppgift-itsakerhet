package com.joel.gruppuppgiftitsakerhet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication()
public class GruppuppgiftItsakerhetApplication {

    public static void main(String[] args) {
        SpringApplication.run(GruppuppgiftItsakerhetApplication.class, args);
    }

}

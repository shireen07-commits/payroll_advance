package com.payrolladvance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PayrollAdvanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayrollAdvanceApplication.class, args);
    }
}
package org.example.contractservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ContractServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractServiceApplication.class, args);
    }

}

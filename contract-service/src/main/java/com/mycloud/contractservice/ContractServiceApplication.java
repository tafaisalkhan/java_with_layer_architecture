package com.mycloud.contractservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.mycloud.contractservice", "com.mycloud.productservice"})
@EntityScan(basePackages = {"com.mycloud.contractservice.adapter.out.persistence", "com.mycloud.productservice.adapter.out.persistence"})
@EnableJpaRepositories(basePackages = {"com.mycloud.contractservice.adapter.out.persistence.repository", "com.mycloud.productservice.adapter.out.persistence.repository"})
public class ContractServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContractServiceApplication.class, args);
    }
}

package com.mycloud.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.mycloud.userservice",
    "com.mycloud.authorizationservice",
    "com.mycloud.customerservice"
})
@EntityScan("com.mycloud.authorizationservice.persistence")
@EnableJpaRepositories("com.mycloud.authorizationservice.persistence")
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

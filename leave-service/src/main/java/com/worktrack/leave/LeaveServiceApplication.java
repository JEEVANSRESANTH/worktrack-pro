package com.worktrack.leave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableDiscoveryClient // Announces this service online to Eureka
@EnableJpaAuditing     // Logs background timestamps seamlessly
public class LeaveServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaveServiceApplication.class, args);
    }
}
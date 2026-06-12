package com.worktrack.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Centralized Configuration Server for WorkTrack Pro.
 * Extracts active properties parameters from an external storage node
 * and securely distributes them dynamically to down-stream services on initialization.
 */
@SpringBootApplication
@EnableConfigServer     // Mounts the Spring Cloud configuration storage engine infrastructure
@EnableDiscoveryClient  // Advertises this server node's address space to the active Eureka cluster
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
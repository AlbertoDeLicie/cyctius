package com.cyctius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories(basePackages = "com.cyctius.repository")
public class CyctiusServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CyctiusServerApplication.class);
    }
}
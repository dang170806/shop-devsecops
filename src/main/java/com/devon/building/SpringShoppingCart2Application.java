package com.devon.building;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringShoppingCart2Application {
    public static void main(String[] args) {
        SpringApplication.run(SpringShoppingCart2Application.class, args);
    }

}

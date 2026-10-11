package com.smallstore.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.smallstore.dashboard", "com.tambolaapp.tambola"})
@EntityScan(basePackages = {"com.smallstore.dashboard", "com.tambolaapp.tambola"})
@EnableJpaRepositories(basePackages = {"com.smallstore.dashboard", "com.tambolaapp.tambola"})
public class SmallstoredashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmallstoredashboardApplication.class, args);
	}

}

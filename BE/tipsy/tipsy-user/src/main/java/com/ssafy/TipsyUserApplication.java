package com.ssafy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.ssafy"})
public class TipsyUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(TipsyUserApplication.class, args);
	}

}

package com.ssafy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.ssafy.domainnosql.dao.room"})
@SpringBootApplication(scanBasePackages = {"com.ssafy"})
public class TipsyRoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(TipsyRoomApplication.class, args);
	}

}

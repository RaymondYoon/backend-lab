package com.example.backproject1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Backproject1Application {

	public static void main(String[] args) {
		SpringApplication.run(Backproject1Application.class, args);
	}

}

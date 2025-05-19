package com.yilmaz.goalCast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class GoalCastApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoalCastApplication.class, args);
	}

}

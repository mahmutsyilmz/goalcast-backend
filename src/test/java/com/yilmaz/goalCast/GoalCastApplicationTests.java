package com.yilmaz.goalCast;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GoalCastApplicationTests {

	@BeforeAll
	static void setup() {
		System.setProperty("spring.profiles.active", "test");
	}

	@Test
	void contextLoads() {
	}
}
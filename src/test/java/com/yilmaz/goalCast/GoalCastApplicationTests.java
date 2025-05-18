package com.yilmaz.goalCast;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles; // BUNU KALDIRIN VEYA YORUM SATIRI YAPIN
import org.springframework.test.context.TestPropertySource; // BU IMPORTU EKLEYİN

@SpringBootTest
// @ActiveProfiles("test") // BUNU KALDIRIN VEYA YORUM SATIRI YAPIN
@TestPropertySource(properties = { // BU ANOTASYONU EKLEYİN
		"spring.datasource.url=jdbc:h2:mem:goalcast_test_ci;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driverClassName=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.show-sql=false",
		"spring.h2.console.enabled=false"
		// "spring.sql.init.mode=never" // Gerekirse bu satırı da ekleyebilirsiniz
})
class GoalCastApplicationTests {

	@Test
	void contextLoads() {
		// Bu test şimdi yukarıdaki property'lerle çalışmalı
	}
}
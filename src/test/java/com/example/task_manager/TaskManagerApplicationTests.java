package com.example.task_manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class TaskManagerApplicationTests {

	@Test
	void contextLoads() {
		// Проверяет, что контекст Spring успешно загружается
	}

}

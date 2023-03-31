package com.beside.ten011;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    static {
        System.setProperty("jasypt.encryptor.password", System.getenv("jasypt.encryptor.password"));
    }

	@Test
	void contextLoads() {
	}

}

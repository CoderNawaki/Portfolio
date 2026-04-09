package com.codernawaki.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"PORTFOLIO_ADMIN_USERNAME=test-admin",
		"PORTFOLIO_ADMIN_PASSWORD=test-password"
})
class PortfolioApplicationTests {

	@Test
	void contextLoads() {
	}

}

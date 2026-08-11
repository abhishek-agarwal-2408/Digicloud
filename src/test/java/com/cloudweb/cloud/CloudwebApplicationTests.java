package com.cloudweb.cloud;

import com.cloudweb.cloud.authentication.signup.SignUpServiceImpl;
import com.cloudweb.cloud.authentication.user.User;
import com.cloudweb.cloud.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CloudwebApplicationTests {
	@Autowired
	private SignUpServiceImpl signUpService;

	@Test
	void testSignUpService() {
		User newUser = new User();
		newUser.setUsername("john_doe");
		newUser.setEmail("john@example2.com");
		newUser.setPassword("password123");

		// Call the signUp method of the service
		try {
			signUpService.createUser(newUser);
		} catch (UserAlreadyExistsException e) {
			e.printStackTrace();
		}
	}
}

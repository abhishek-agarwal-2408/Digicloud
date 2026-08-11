package com.cloudweb.cloud.authentication.signup;

import com.cloudweb.cloud.authentication.user.Customer;
import com.cloudweb.cloud.authentication.user.User;

public interface SignUpService {
    User createUser(User user);

    User updateUser(Customer customer, String userId);
    User verify(String email, String otp);
}


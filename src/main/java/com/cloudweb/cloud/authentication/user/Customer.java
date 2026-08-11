package com.cloudweb.cloud.authentication.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String dob;
    private String password;
    private String confirmPassword;
}

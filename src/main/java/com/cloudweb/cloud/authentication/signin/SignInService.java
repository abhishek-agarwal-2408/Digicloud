package com.cloudweb.cloud.authentication.signin;

import com.cloudweb.cloud.authentication.user.User;
import com.cloudweb.cloud.authentication.user.UserRepository;
import com.cloudweb.cloud.exception.AccountNotActiveException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignInService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String signInUser(String email, String password, HttpSession session) {
        if(email == null || email.isEmpty() || email.isBlank()){
            throw new AccountNotActiveException("Please provide email.");
        }
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new AccountNotActiveException("Email not found.");
        }else if(!user.isVerified()){
            throw new AccountNotActiveException("Your account is not activated yet. Please activate your account.");
        }
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Authentication successful, store user details in the session
            session.setAttribute("userId", user.getUserId());
            return session.getId(); // Return the session ID
        }
        return null; // Authentication failed
    }
}

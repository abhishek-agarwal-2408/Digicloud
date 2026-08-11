package com.cloudweb.cloud.authentication.signup;

import com.cloudweb.cloud.authentication.user.*;
import com.cloudweb.cloud.email.EmailService;
import com.cloudweb.cloud.exception.InvalidOTPException;
import com.cloudweb.cloud.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SignUpServiceImpl implements SignUpService {

    @Value("${mail.fromName}")
    private String fromName;

    @Value("${otp.expiry.time.minutes}")
    private int otpExpiryTime;

    private final UserRepository userRepository;

    private UserVerificationRepository userVerificationRepository;


    private EmailService emailService;

    @Autowired
    public SignUpServiceImpl(UserRepository userRepository, UserVerificationRepository userVerificationRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.userVerificationRepository = userVerificationRepository;
        this.emailService = emailService;
    }

    public User createUser(User user) {
        // Save the user to the database
        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            throw new UserAlreadyExistsException("Please provide email.");
        }else if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists.");
        }else {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.setActive("N");
            userRepository.save(user);
        }

        // Generate and save OTP
        String otp = generateOTP();
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(otpExpiryTime);
        UserVerification userVerification = new UserVerification();
        userVerification.setUser(user);
        userVerification.setToken(otp);
        userVerification.setExpiryDateTime(expiryDateTime);
        userVerificationRepository.save(userVerification);
        // Send OTP via email
        String emailContent = "<div>Hi " + user.getFirstName() + " " + user.getLastName() + "</div>\n\n";
        emailContent += "<div>Welcome to " + fromName + ".</div>\n\n";
        emailContent += "Your OTP for verification is: " + otp + ". It will expire in "+ otpExpiryTime +" minutes.";
        emailService.sendEmail(user.getEmail(), "Email Verification OTP", emailContent);
        return user;
    }

    @Override
    public User updateUser(Customer customer, String userId) {
        User user = userRepository.findByUserId(userId);
        if(customer.getFirstName() != null && !(customer.getFirstName().isBlank() || customer.getFirstName().isEmpty())){
            user.setFirstName(customer.getFirstName());
        }
        if(customer.getLastName() != null && !(customer.getLastName().isBlank() || customer.getLastName().isEmpty())){
            user.setLastName(customer.getLastName());
        }
        if(customer.getDob() != null && !(customer.getDob().isBlank() || customer.getDob().isEmpty())){
            user.setDob(customer.getDob());
        }
        if(customer.getPhoneNumber() != null && !(customer.getPhoneNumber().isBlank() || customer.getPhoneNumber().isEmpty())){
            user.setPhoneNumber(customer.getPhoneNumber());
        }
        if(customer.getPassword() != null && !(customer.getPassword().isBlank() || customer.getPassword().isEmpty())){
            String hashedPassword = BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
        }
        userRepository.save(user);
        return user;
    }

    // Helper method to generate a 6-digit OTP
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public User verify(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new InvalidOTPException("User with the provided email not found.");
        }
        UserVerification userVerification = userVerificationRepository.findByUserId(user.getUserId());
        if (userVerification == null || !userVerification.getToken().equals(otp)) {
            throw new InvalidOTPException("Invalid OTP provided.");
        }
        user.setActive("Y");
        userRepository.save(user);
        return user;
    }
}


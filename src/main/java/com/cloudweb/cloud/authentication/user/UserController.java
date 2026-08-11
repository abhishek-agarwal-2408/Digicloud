package com.cloudweb.cloud.authentication.user;

import com.cloudweb.cloud.authentication.signin.SessionIdRequest;
import com.cloudweb.cloud.authentication.signin.SignInRequest;
import com.cloudweb.cloud.authentication.signin.SignInService;
import com.cloudweb.cloud.authentication.signup.SignUpService;
import com.cloudweb.cloud.authentication.signup.VerificationRequest;
import com.cloudweb.cloud.database.FileStorageService;
import com.cloudweb.cloud.exception.AccountNotActiveException;
import com.cloudweb.cloud.exception.UserAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/control")
public class UserController {

    @Autowired
    private SignUpService signUpService;
    @Autowired
    private SignInService signInService;
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody User user) {
        try {
            signUpService.createUser(user);
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok("Customer registered successfully!");
    }

    @PostMapping("/updateCustomer")
    public ResponseEntity<String> updateCutomerDetails(@RequestBody Customer customer, HttpServletRequest request) {
        if(!(customer.getConfirmPassword().equals(customer.getPassword()))){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Passwords not match");
        }
        HttpSession session = request.getSession(false);
        if(session == null){
            return ResponseEntity.ok("Session expired.");
        }
        String userId = (String) session.getAttribute("userId");
        try {
            signUpService.updateUser(customer, userId);
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok("Customer updated successfully!");
    }

    @PostMapping("/updateCustomerContact")
    public ResponseEntity<String> updateCustomerContact(@RequestBody Customer customer, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null){
            return ResponseEntity.ok("Session expired.");
        }
        String userId = (String) session.getAttribute("userId");
        try {
            signUpService.updateUser(customer, userId);
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok("Customer contact updated successfully!");
    }

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if(session == null){
                return "redirect:/control/login";
            }
            String userId = (String) session.getAttribute("userId");
            for (MultipartFile file : files) {
                fileStorageService.storeFile(file, userId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/control/dashboard";
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<String> updateProfile(@RequestParam("profile") MultipartFile profile, HttpServletRequest request) {
        JSONObject response = new JSONObject();
        try {
            HttpSession session = request.getSession(false);
            String userId = (String) session.getAttribute("userId");
            String message = fileStorageService.updateProfile(profile, userId);
            if(message == "success"){
                response.put("message","Profile updated.");
            }else{
                response.put("message","Failed to update profile.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.put("message","Failed to update profile.");
        }
        return ResponseEntity.ok(response.toString());
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOTP(@RequestBody VerificationRequest verificationRequest) {
        try {
            String email = verificationRequest.getEmail();
            String otp = verificationRequest.getOtp();
            signUpService.verify(email, otp);
        } catch (RuntimeException e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok("Customer registered successfully!");
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        try{
            String sessionId = signInService.signInUser(signInRequest.getEmail(), signInRequest.getPassword(), session);
            JSONObject response = new JSONObject();
            if (sessionId != null){
                response.put("message","success");
                response.put("sessionId",sessionId);
            }else {
                response.put("message","Invalid credentials");
            }
            return ResponseEntity.ok(response.toString());
        }catch (AccountNotActiveException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    public String signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            // Invalidate the session (sign out the user)
            session.invalidate();
        }
        return "redirect:/control/login";
    }

    @PostMapping("/signInStatus")
    public ResponseEntity<String> signInStatus(@RequestBody SessionIdRequest sessionIdRequest, HttpServletRequest request) {
        String sessionId = sessionIdRequest.getSessionId();
        HttpSession session = request.getSession(false);
        JSONObject response = new JSONObject();
        if (session != null && session.getId().equals(sessionId)) {
            response.put("message","Customer is signed in");
        } else {
            response.put("message","Customer is not signed in or invalid session ID");
        }
        return ResponseEntity.ok(response.toString());
    }
}

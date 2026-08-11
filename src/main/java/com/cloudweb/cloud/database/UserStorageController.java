package com.cloudweb.cloud.database;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class UserStorageController {

    @Autowired
    UserStorageRepository userStorageRepository;

    @PostMapping("/user/documents")
    public Set<UserStorage> getUserDocuments(@RequestBody String sessionId, HttpServletRequest request) {
        // Fetch the documents or images based on the userId
        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute("userId");
        Set<UserStorage> userDocuments = userStorageRepository.findByUserId(userId);
        return userDocuments;
    }
}

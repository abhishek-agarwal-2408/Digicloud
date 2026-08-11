package com.cloudweb.cloud.authentication.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("http://localhost:8080")
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    boolean existsByEmail(String email);

    User findByUserId(String userId);
    boolean existsByUserId(String userId);
}


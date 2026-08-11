package com.cloudweb.cloud.authentication.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationRepository extends JpaRepository<UserVerification, String> {
    UserVerification findByToken(String token);
    UserVerification findByUserId(String userId);
}

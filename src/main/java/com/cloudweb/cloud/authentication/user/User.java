package com.cloudweb.cloud.authentication.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(generator = "customUserIdGenerator")
    @GenericGenerator(name = "customUserIdGenerator", strategy = "com.cloudweb.cloud.authentication.user.UserId")
    private String userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String dob;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String active;

    private String profileImageName;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserVerification userVerifications;

    public boolean isVerified() {
        boolean isVerified = this.getActive().equals("Y");
        return isVerified;
    }
}


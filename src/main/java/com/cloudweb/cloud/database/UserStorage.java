package com.cloudweb.cloud.database;

import com.cloudweb.cloud.authentication.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Component
public class UserStorage {
    @Id
    private String storageId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private String fileName;

    private String fileType;

    private long size;

    private String location;

    private String fileCategory; // "document" or "image"

    private LocalDateTime createdDateTime;

    private String deleted;

}

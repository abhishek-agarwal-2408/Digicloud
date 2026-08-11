package com.cloudweb.cloud.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserStorageRepository extends JpaRepository<UserStorage, String> {

    @Query("SELECT us FROM UserStorage us WHERE us.user.userId = :userId AND us.deleted NOT IN ('Y')")
    Set<UserStorage> findByUserId(String userId);

    @Query("SELECT us FROM UserStorage us WHERE us.storageId = :storageId AND us.deleted NOT IN ('Y')")
    UserStorage findByStorageId(String storageId);

    @Query("SELECT us FROM UserStorage us WHERE us.user.userId = :userId AND us.fileCategory = :fileCategory AND us.deleted NOT IN ('Y')")
    Set<UserStorage> findByUserIdAndCategory(String userId, String fileCategory);

    @Query("SELECT COALESCE(SUM(us.size), 0) FROM UserStorage us WHERE us.user.userId = :userId AND us.deleted NOT IN ('Y')")
    long getTotalSizeByUserId(String userId);

    @Query("SELECT COUNT(*) FROM UserStorage us WHERE us.user.userId = :userId AND us.deleted NOT IN ('Y')")
    long getTotalDocsByUserId(String userId);

    @Query("SELECT COUNT(*) FROM UserStorage us WHERE us.user.userId = :userId AND us.fileCategory = :fileCategory AND us.deleted NOT IN ('Y')")
    long getNoOfDocsByUserIdAndCategory(String userId, String fileCategory);
}

package com.cloudweb.cloud.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StorageId {
    private static final Map<String, String> FILE_TYPE_PREFIXES = new HashMap<>();
    private static final String SQL_QUERY = "SELECT COUNT(*) FROM USER_STORAGE WHERE FILE_CATEGORY = '_FILE_TYPE_'";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String generateId(String fileType) {
        System.out.println(fileType);
        String prefix = fileType.toUpperCase();
        long counter = jdbcTemplate.queryForObject(SQL_QUERY.replace("_FILE_TYPE_", fileType), Long.class) + 1;
        System.out.println(counter);
        return prefix + String.format("%05d", counter);
    }
}

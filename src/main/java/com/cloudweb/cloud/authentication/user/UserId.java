package com.cloudweb.cloud.authentication.user;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class UserId  implements IdentifierGenerator {

    private static final String PREFIX = "CLOUD";
    private static final String SQL_QUERY = "SELECT COUNT(*) FROM user";

    private final JdbcTemplate jdbcTemplate;
    private long counter;

    @Autowired
    public UserId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public synchronized Serializable generate(SharedSessionContractImplementor session, Object object)
            throws HibernateException {
        counter = jdbcTemplate.queryForObject(SQL_QUERY, Long.class) + 1;
        return PREFIX + String.format("%07d", counter++);
    }
}

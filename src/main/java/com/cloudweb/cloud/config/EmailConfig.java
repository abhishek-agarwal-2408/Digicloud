package com.cloudweb.cloud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

@Configuration
@EnableAsync
@PropertySource("classpath:email.properties")
public class EmailConfig {
    // Add your email configuration properties here
    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${mail.transport.protocol}")
    private String mailProtocol;
    @Value("${mail.smtp.auth}")
    private String mailAuth;
    @Value("${mail.smtp.starttls.enable}")
    private String starttlsEnable;
    @Value("${mail.debug}")
    private String mailDebug;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailProtocol);
        props.put("mail.smtp.auth", mailAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.debug", mailDebug); // Set to false for production

        return mailSender;
    }
}
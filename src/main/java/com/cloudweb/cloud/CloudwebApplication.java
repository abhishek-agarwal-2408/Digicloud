package com.cloudweb.cloud;

import com.cloudweb.cloud.filters.RootRedirectionFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan("com.cloudweb.cloud.") // Replace with the package where UserStorage is located
public class CloudwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudwebApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<RootRedirectionFilter> rootRedirectionFilter() {
		FilterRegistrationBean<RootRedirectionFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RootRedirectionFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}

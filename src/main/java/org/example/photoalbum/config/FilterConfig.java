package org.example.photoalbum.config;

import org.example.photoalbum.security.FirebaseAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!dev") // create a dev profile in order to bypass filter for development purposes
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FirebaseAuthFilter> firebaseAuthFilter() {
        FilterRegistrationBean<FirebaseAuthFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new FirebaseAuthFilter());
        reg.addUrlPatterns("/api/*"); // everything under /api requires auth
        reg.setOrder(1);
        return reg;
    }
}
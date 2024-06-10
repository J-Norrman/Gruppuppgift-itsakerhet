package com.joel.gruppuppgiftitsakerhet.util;

import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.joel.gruppuppgiftitsakerhet.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Create admin user
            AppUser admin = new AppUser(null, "admin@example.com", passwordEncoder.encode("admin123"), "ADMIN");
            userRepository.save(admin);
            // Create regular user
            AppUser user = new AppUser(null, "user@example.com", passwordEncoder.encode("user123"), "USER");
            userRepository.save(user);
        };
    }
}

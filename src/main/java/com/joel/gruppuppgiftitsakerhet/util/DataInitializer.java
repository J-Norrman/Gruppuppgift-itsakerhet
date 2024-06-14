package com.joel.gruppuppgiftitsakerhet.util;

import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.joel.gruppuppgiftitsakerhet.repository.UserRepository;

@Configuration

//DataInitializer skapar och sparar två användare när applikationen startar.
//Detta är främst för utvecklingssyfte, så att vi har en användare och en admin att logga in som.
//Denna metod bör inte användas i en produktionsmiljö.

public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initDatabase() {
        userRepository.save(new AppUser(null, "user@example.com", passwordEncoder.encode("password"), "USER","firstname","lastname",30));
        userRepository.save(new AppUser(null, "admin@example.com", passwordEncoder.encode("password"), "ADMIN","firstname","lastname",30));
    }
}

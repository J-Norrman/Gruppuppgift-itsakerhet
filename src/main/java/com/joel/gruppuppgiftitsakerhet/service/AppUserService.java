package com.joel.gruppuppgiftitsakerhet.service;

import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.joel.gruppuppgiftitsakerhet.repository.UserRepository;

import java.util.List;

@Service
public class AppUserService {
    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    public AppUser getUserByEmail(String email) {
        return userRepository.findByemail(email);
    }

    public AppUser getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}

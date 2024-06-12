package com.joel.gruppuppgiftitsakerhet.service;

import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import com.joel.gruppuppgiftitsakerhet.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.joel.gruppuppgiftitsakerhet.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDTO convertToDto(AppUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setAge(user.getAge());

        return userDTO;
    }

    public AppUser convertToEntity(UserDTO userDTO) {
        AppUser user = new AppUser();
        if (userDTO.getId() != null) {
            user.setId(userDTO.getId());
        }
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAge(userDTO.getAge());
        user.setPassword(userDTO.getPassword());
        return user;
    }
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

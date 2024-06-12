package com.joel.gruppuppgiftitsakerhet.web;
import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import com.joel.gruppuppgiftitsakerhet.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.joel.gruppuppgiftitsakerhet.service.AppUserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class AppController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<AppUser> users = appUserService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        model.addAttribute("users", userDTOs);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            model.addAttribute("loggedInUser", userDetails.getUsername());
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                model.addAttribute("userRole", user.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.joining(", ")));
            }
        }
        return "users";
    }



    @GetMapping("/register")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("user") UserDTO userDTO) {
        AppUser user = convertToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        AppUser user = appUserService.getUserById(id);
        UserDTO userDTO = convertToDto(user);
        model.addAttribute("user", userDTO);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") UserDTO userDTO) {
        AppUser user = convertToEntity(userDTO);
        user.setId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return "redirect:/users";
    }
    private UserDTO convertToDto(AppUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setAge(user.getAge());

        return userDTO;
    }

    private AppUser convertToEntity(UserDTO userDTO) {
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
//    InMemoryUserDetailsManager manager;
//    PasswordEncoder encoder;
//    QRCode qrCode;
//    UserRepository userRepository;
//    AppUser appUser;
//
//
//    public ThymeLeafController(InMemoryUserDetailsManager manager, PasswordEncoder encoder, QRCode qrCode, UserRepository userRepository) {
//        this.manager = manager;
//        this.encoder = encoder;
//        this.qrCode = qrCode;
//        this.userRepository = userRepository;
//    }
//
//    @GetMapping("/register")
//    public String register(Model model){
//        model.addAttribute("user", new UserDTO());
//        return "register";
//    }
//
//
//    @PostMapping("/register")
//    public String submitForm(@ModelAttribute("user") UserDTO userDTO, Model model) {
//        AppUser appUser = new AppUser();
//        appUser.setEmail(userDTO.getEmail());
//        appUser.setPassword(encoder.encode(userDTO.getPassword()));
//        appUser.setSecret(Base32.random());
//        model.addAttribute("qrcode", qrCode.dataUrl(appUser));
//        userRepository.save(appUser);
//        return "qrcode";
//    }
//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }

}

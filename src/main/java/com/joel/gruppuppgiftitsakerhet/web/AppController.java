package com.joel.gruppuppgiftitsakerhet.web;
import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.joel.gruppuppgiftitsakerhet.service.AppUserService;

import java.util.List;
@Controller
@RequestMapping("/")
public class AppController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<AppUser> users = appUserService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("user") AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        AppUser user = appUserService.getUserById(id);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") AppUser user) {
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

}

package com.joel.gruppuppgiftitsakerhet.web;
import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public String getAllUsers(Model model) {
        List<AppUser> users = appUserService.getAllUsers();
        model.addAttribute("users", users);
        return "user-list";
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

    @PostMapping
    public String createUser(@ModelAttribute("user") AppUser user) {
        appUserService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        AppUser user = appUserService.getUserById(id);
        model.addAttribute("user", user);
        return "user-form";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") AppUser user) {
        user.setId(id);
        appUserService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return "redirect:/users";
    }
}

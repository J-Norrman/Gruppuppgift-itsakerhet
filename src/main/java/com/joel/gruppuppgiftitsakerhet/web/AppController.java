package com.joel.gruppuppgiftitsakerhet.web;
import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import com.joel.gruppuppgiftitsakerhet.service.AppUserService;
import com.joel.gruppuppgiftitsakerhet.util.MaskingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class AppController {

    @Autowired
    private AppUserService appUserService;


    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        logger.debug("Hämtar alla användare");
        List<AppUser> users = appUserService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/login")
    public String login() {
        logger.debug("Visar loginformulär");
        return "login";
    }

    @GetMapping("/register")
    public String showCreateForm(Model model) {
        logger.debug("Visar registreringsformulär");
        model.addAttribute("user", new AppUser());
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("user") AppUser user) {
        logger.debug("Registrerar användare med e-post: " + MaskingUtils.anonymize(user.getEmail()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserService.saveUser(user);
        logger.info("Användare registrerad: " + MaskingUtils.anonymize(user.getEmail()));
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Visar redigeringsformulär för användare med ID: " + id);
        AppUser user = appUserService.getUserById(id);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") AppUser user) {
        logger.debug("Uppdaterar användare med ID: " + id);
        user.setId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserService.saveUser(user);
        logger.info("Användare uppdaterad med ID: " + id);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        logger.debug("Försöker ta bort användare med ID: " + id);
        try {
            appUserService.deleteUser(id);
            logger.info("Användare borttagen med ID: " + id);
        } catch (Exception e) {
            logger.warn("Användare med ID: " + id + " kunde inte hittas", e);
        }
        return "redirect:/users";
    }

}

package com.joel.gruppuppgiftitsakerhet.web;
import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import com.joel.gruppuppgiftitsakerhet.model.UserDTO;
import com.joel.gruppuppgiftitsakerhet.service.UserService;
import com.joel.gruppuppgiftitsakerhet.util.MaskingUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/")
public class AppController {

    @Autowired
    private UserService userService;


    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String root() {
        return "redirect:/login";
    }

//    @GetMapping("/login")
//    public String login() {
//        logger.debug("Visar loginformulär");
//        return "login";
//    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        logger.debug("Användare är utloggad");
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }


    @GetMapping("/users")
    public String getAllUsers(Model model) {
        logger.debug("Hämtar alla användare");
        List<AppUser> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(userService::convertToDto)
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
        logger.debug("Visar registreringsformulär");
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("user") UserDTO userDTO) {
        logger.debug("Registrerar användare med e-post: " + MaskingUtils.anonymize(userDTO.getEmail()));
        AppUser user = userService.convertToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        logger.info("Användare registrerad: " + MaskingUtils.anonymize(user.getEmail()));
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Visar redigeringsformulär för användare med ID: " + id);
        AppUser user = userService.getUserById(id);
        UserDTO userDTO = userService.convertToDto(user);
        model.addAttribute("user", userDTO);
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") @Valid UserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDTO);
            return "edit"; // return to the edit form if validation fails
        }
        AppUser user = userService.convertToEntity(userDTO);
        logger.debug("Uppdaterar användare med ID: " + id);
        user.setId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        logger.info("Användare uppdaterad med ID: " + id);
        return "redirect:/users";
    }
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        logger.debug("Försöker ta bort användare med ID: " + id);
        AppUser userToDelete = userService.getUserById(id);
        if (userToDelete == null) {
            logger.warn("Användare med ID: " + id + " kunde inte hittas");
            model.addAttribute("error", "User not found");
            return "redirect:/users";
        }
        if ("ADMIN".equals(userToDelete.getRole())) {
            logger.warn("Försöker ta bort en administratör: " + id);
            model.addAttribute("error", "Cannot delete an admin user");
            return "redirect:/users";
        }
        try {
            userService.deleteUser(id);
            logger.info("Användare borttagen med ID: " + id);
        } catch (Exception e) {
            logger.warn("Användare med ID: " + id + " kunde inte tas bort", e);
        }
        return "redirect:/users";
    }
}

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AppController hanterar webbrelaterade förfrågningar och funktionalitet för användarhantering.

 * Denna controller inkluderar hantering av inloggning, utloggning, visning av användarlistor,
 * registrering av nya användare, redigering av befintliga användare och borttagning av användare.

 * Logger används för att logga information om operationer och deras resultat.
 */


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

    @GetMapping("/login")
    public String login() {
        logger.debug("Visar loginformulär");
        return "login";
    }

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
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") @Valid UserDTO userDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDTO);
            return "edit";
        }
        try {
            AppUser user = userService.convertToEntity(userDTO);
            logger.debug("Uppdaterar användare med ID: " + id);
            user.setId(id);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
            logger.info("Användare uppdaterad med ID: " + id);
            redirectAttributes.addFlashAttribute("successMessage", "Användare uppdaterad med ID: " + id);
            return "redirect:/users";
        } catch (Exception e) {
            logger.error("Fel vid uppdatering av användare med ID: " + id, e);
            model.addAttribute("errorMessage", "Kunde inte uppdatera användare med ID: " + id);
            model.addAttribute("user", userDTO);
            return "edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Försöker ta bort användare med ID: " + id);
        AppUser userToDelete = userService.getUserById(id);
        if (userToDelete == null) {
            logger.warn("Användare med ID: " + id + " kunde inte hittas");
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/users";
        }
        if ("ADMIN".equals(userToDelete.getRole())) {
            logger.warn("Försöker ta bort en administratör: " + id);
            redirectAttributes.addFlashAttribute("error", "Cannot delete an admin user");
            return "redirect:/users";
        }
        try {
            userService.deleteUser(id);
            logger.info("Användare borttagen med ID: " + id);
        } catch (Exception e) {
            logger.warn("Användare med ID: " + id + " kunde inte tas bort", e);
            redirectAttributes.addFlashAttribute("error", "User could not be deleted.");
        }
        return "redirect:/users";
    }
}

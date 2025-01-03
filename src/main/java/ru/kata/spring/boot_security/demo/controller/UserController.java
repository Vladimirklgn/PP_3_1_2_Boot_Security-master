package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entitys.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;


@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user-page")
    public String userPage(Model model, @AuthenticationPrincipal UserDetails loggedUser) {
        User user = userService.findByUsername(loggedUser.getUsername());
        model.addAttribute("user", user);
        return "user-page";
    }

    @GetMapping("/edit")
    public String editUserPage(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User loggedUser) {
        User user = userService.findByUsername(loggedUser.getUsername());
        model.addAttribute("user", user);
        return "edit-user";
    }

    @PatchMapping("/edit")
    public String patchUser(@ModelAttribute("user") User user,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User loggedUser) {

        User currentUser = userService.findByUsername(loggedUser.getUsername());

        if (user.getName() != null && !user.getName().isEmpty()) {
            currentUser.setName(user.getName());
        }

        if (user.getSurname() != null && !user.getSurname().isEmpty()) {
            currentUser.setSurname(user.getSurname());
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            currentUser.setEmail(user.getEmail());
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty() &&
                !passwordEncoder.matches(user.getPassword(), currentUser.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userService.update(currentUser.getId(), currentUser);

        return "redirect:/user/user-page";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.update(user.getId(), user);
        return "redirect:/user";
    }
}
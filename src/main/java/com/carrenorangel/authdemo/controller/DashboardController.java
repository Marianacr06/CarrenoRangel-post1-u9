package com.carrenorangel.authdemo.controller;

import com.carrenorangel.authdemo.entity.AppUser;
import com.carrenorangel.authdemo.repository.AppUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping
public class DashboardController {

    private final AppUserRepository appUserRepository;

    public DashboardController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @GetMapping({"/admin/dashboard", "/admin"})
    public String adminDashboard(Principal principal, Model model) {
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("roleLabel", "ADMIN");

        List<AppUser> users = appUserRepository.findAll();
        model.addAttribute("users", users);

        return "dashboard";
    }

    @GetMapping({"/user/dashboard", "/dashboard"})
    public String userDashboard(Principal principal, Model model) {
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("roleLabel", "USER");
        return "dashboard";
    }
}

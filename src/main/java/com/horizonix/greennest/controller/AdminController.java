package com.horizonix.greennest.controller;

import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // 1. Dashboard: Show list of Pending Owners
    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        // You must have this method in UserService: return userRepository.findByRoleAndIsVerifiedFalse(Role.OWNER);
        List<User> pendingOwners = userService.getPendingOwners();
        model.addAttribute("pendingOwners", pendingOwners);
        return "admin/dashboard";
    }

    // 2. Action: Approve Owner
    @PostMapping("/approve/{id}")
    public String approveOwner(@PathVariable Long id) {
        userService.approveOwner(id);
        return "redirect:/admin/dashboard?success";
    }
}
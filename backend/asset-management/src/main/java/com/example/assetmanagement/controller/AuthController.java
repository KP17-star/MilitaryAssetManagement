package com.example.assetmanagement.controller;

import com.example.assetmanagement.entity.User;
import com.example.assetmanagement.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5175")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Invalid username or password"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Login successful");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());

        if (user.getBase() != null) {
            response.put("baseId", user.getBase().getId());
            response.put("baseName", user.getBase().getName());
        } else {
            response.put("baseId", null);
            response.put("baseName", null);
        }

        return response;
    }
}

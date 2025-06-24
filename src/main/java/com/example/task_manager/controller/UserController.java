package com.example.task_manager.controller;

import com.example.task_manager.model.User;
import com.example.task_manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        // Проверка, что пользователь с таким именем не существует
        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        
        User registeredUser = userService.register(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
    
    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = userService.findByUsername(username);
        
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
} 
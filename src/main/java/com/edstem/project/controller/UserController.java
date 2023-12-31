package com.edstem.project.controller;

import com.edstem.project.contract.request.LoginRequest;
import com.edstem.project.contract.request.SignUpRequest;
import com.edstem.project.contract.response.AuthResponse;
import com.edstem.project.contract.response.UserResponse;
import com.edstem.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public UserResponse SignUp(@Valid @RequestBody SignUpRequest request) {
        return userService.signUp(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) throws Exception{
        return userService.login(request);
    }
}

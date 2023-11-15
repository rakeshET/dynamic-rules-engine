package com.edstem.project.service;

import com.edstem.project.contract.request.LoginRequest;
import com.edstem.project.contract.request.SignUpRequest;
import com.edstem.project.contract.response.AuthResponse;
import com.edstem.project.contract.response.SignUpResponse;
import com.edstem.project.exception.InvalidLoginException;
import com.edstem.project.model.Role;
import com.edstem.project.model.User;
import com.edstem.project.repository.UserRepository;
import com.edstem.project.security.JwtService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SignUpResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException("Invalid Signup");
        }
        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .build();
        user = userRepository.save(user);
        return modelMapper.map(user, SignUpResponse.class);
    }

    public AuthResponse login(LoginRequest request) throws Exception{
        String email = request.getEmail();
        String password = request.getPassword();
        if (!userRepository.existsByEmail(email)) {
            throw new EntityNotFoundException("Invalid login");
        }
        User user = userRepository.findByEmail(request.getEmail());
        if (passwordEncoder.matches(password, user.getPassword())) {
            AuthResponse jwtToken = jwtService.generateToken(user.getName());
            return jwtToken;
        }
        throw new InvalidLoginException();
    }
}

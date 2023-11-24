package com.edstem.project.service;

import com.edstem.project.contract.request.LoginRequest;
import com.edstem.project.contract.request.SignUpRequest;
import com.edstem.project.contract.response.AuthResponse;
import com.edstem.project.contract.response.UserResponse;
import com.edstem.project.exception.InvalidLoginException;
import com.edstem.project.model.Role;
import com.edstem.project.model.User;
import com.edstem.project.repository.RuleRepository;
import com.edstem.project.repository.UserRepository;
import com.edstem.project.security.JwtService;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private UserService userService;
    private JwtService jwtService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        userRepository = Mockito.mock(UserRepository.class);
        modelMapper = new ModelMapper();
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtService = Mockito.mock(JwtService.class);
        userService = new UserService(userRepository, modelMapper, passwordEncoder,jwtService);
    }

    @Test
    public void signUp_withValidRequest_shouldCreateNewUser() {
        SignUpRequest request = new SignUpRequest();
        request.setName("John Doe");
        request.setEmail("johndoe@example.com");
        request.setPassword("password123");
        request.setRole("ADMIN");

        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.setName("John Doe");
        savedUser.setEmail("johndoe@example.com");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(Role.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);


        UserResponse actualResponse = userService.signUp(request);
        verify(userRepository, times(1)).save(any(User.class));

        // Assert user response contains expected data
        assertEquals(request.getName(), actualResponse.getName());
        assertEquals(request.getEmail(), actualResponse.getEmail());
        assertEquals("hashedPassword", actualResponse.getPassword());
    }

    @Test
    public void signUp_withExistingEmail_shouldThrowException() {
        SignUpRequest request = new SignUpRequest();
        request.setName("John Doe");
        request.setEmail("johndoe@example.com");
        request.setPassword("password123");
        request.setRole("USER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.signUp(request));
    }

    @Test
    public void login_withValidCredentials_shouldReturnAuthToken() throws Exception {
        // Create a LoginRequest with valid credentials
        LoginRequest request = new LoginRequest();
        request.setEmail("johndoe@example.com");
        request.setPassword("plainTextPassword");

        // Create a User with the expected details
        User savedUser = new User();
        savedUser.setName("John Doe");
        savedUser.setEmail("johndoe@example.com");
        savedUser.setPassword("hashedPassword"); // Assuming this is the hashed password stored in the database
        savedUser.setRole(Role.USER);

        // Mock the UserRepository behavior
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(savedUser);

        // Mock the PasswordEncoder behavior
        when(passwordEncoder.matches(request.getPassword(), savedUser.getPassword())).thenReturn(true);

        // Mock the JwtService behavior
        AuthResponse expectedToken = new AuthResponse();
        expectedToken.setName("John Doe");
        expectedToken.setToken("validToken");
        when(jwtService.generateToken(savedUser)).thenReturn(expectedToken);

        // Call the login method
        AuthResponse actualToken = userService.login(request);

        // Verify that the actual token matches the expected token
        assertEquals(expectedToken, actualToken);

        // Optionally, you can also verify other interactions, such as method invocations
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).matches(request.getPassword(), savedUser.getPassword());
        verify(jwtService, times(1)).generateToken(savedUser);
    }


    @Test
    public void login_withInvalidCredentials_shouldThrowException(){
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("invalidPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.login(request));
    }

    @Test
    public void login_withInvalidPassword_shouldThrowException(){
        LoginRequest request = new LoginRequest();
        request.setEmail("johndoe@example.com");
        request.setPassword("invalidPassword");

        User user = new User();
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.USER);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.login(request));
    }

}


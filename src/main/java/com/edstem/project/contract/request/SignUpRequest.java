package com.edstem.project.contract.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;
    @NotBlank(message = "role cannot be empty")
    private String Role;
}
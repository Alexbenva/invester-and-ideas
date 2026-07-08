package com.venturebridge.dto;

import com.venturebridge.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters and contain letters and numbers")
    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotNull
    private Role role;

    private String phone;

    private String linkedin;
}
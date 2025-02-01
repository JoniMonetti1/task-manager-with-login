package com.example.taskManagerWithLogin.models.dto;

import com.example.taskManagerWithLogin.validations.ExistsByUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    @NotBlank(message = "Username is required")
    @ExistsByUsername
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Name is required")
    private String name;
}

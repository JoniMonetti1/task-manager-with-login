package com.example.taskManagerWithLogin.models.dto;

import com.example.taskManagerWithLogin.models.ROLE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private ROLE role;
}

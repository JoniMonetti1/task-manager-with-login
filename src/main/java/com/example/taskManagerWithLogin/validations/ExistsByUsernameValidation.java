package com.example.taskManagerWithLogin.validations;

import com.example.taskManagerWithLogin.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExistsByUsernameValidation implements ConstraintValidator<ExistsByUsername, String> {

    @Autowired
    UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (username == null || username.trim().isEmpty()) {
            return true;
        }

        try {
            return !userService.existsByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}

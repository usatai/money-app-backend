package com.example.money.validation;

import com.example.money.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserNameUniqueValidator implements ConstraintValidator<UserNameUnique,String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isValid(String user_name, ConstraintValidatorContext constraintValidatorContext) {
        if (user_name == null || user_name.isEmpty()) {
            return true;
        }

        Boolean userExists = userRepository.existsByUser(user_name);
        if (userExists) {
            return false;
        }

        return true;
    }
}

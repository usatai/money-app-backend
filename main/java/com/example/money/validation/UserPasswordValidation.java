package com.example.money.validation;

import com.example.money.controller.LoginForm;
import com.example.money.security.PasswordUtil;
import com.example.money.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserPasswordValidation implements ConstraintValidator<UserPassword,LoginForm> {

    @Autowired
    UserService userService;

    @Override
    public boolean isValid(LoginForm loginForm, ConstraintValidatorContext constraintValidatorContext){
        if (loginForm.loginUser_name() == null || loginForm.loginUser_password() == null ||
        loginForm.loginUser_name().isBlank() || loginForm.loginUser_password().isBlank()){
            return true;
        }

        String dbUserPassword = userService.getUserPassword(loginForm.loginUser_name());

        if(PasswordUtil.matchPassword(loginForm.loginUser_password(),dbUserPassword)) {
            return true;
        }

        constraintValidatorContext.disableDefaultConstraintViolation();

        constraintValidatorContext.buildConstraintViolationWithTemplate("パスワードが正しくありません")
                .addPropertyNode("loginUser_password")
                .addConstraintViolation();

        return false;
    }
}

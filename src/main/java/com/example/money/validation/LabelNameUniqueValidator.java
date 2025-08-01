package com.example.money.validation;

import com.example.money.controller.LabelForm;
import com.example.money.repository.LabelRepository;
import com.example.money.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LabelNameUniqueValidator implements ConstraintValidator<LabelNameUnique,LabelForm> {

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @Override
    public boolean isValid(LabelForm labelForm, ConstraintValidatorContext constraintValidatorContext){
        if (labelForm.label_name() == null){
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return true;
        }
    
        String username = authentication.getName(); // トークン内のユーザー名
        Integer userIdInt = userService.getUserIdByUsername(username)
            .orElseThrow(() -> new RuntimeException("ユーザーIDが見つかりません: " + username));

        Long labelCount = labelRepository.existsByLabel(labelForm.label_name(),userIdInt);
        if (labelCount == 1){
            return false;
        }

        return  true;

    }

}

package com.example.money.validation;

import com.example.money.controller.LabelForm;
import com.example.money.repository.LabelRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LabelNameUniqueValidator implements ConstraintValidator<LabelNameUnique,LabelForm> {

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    HttpServletRequest request;

    @Override
    public boolean isValid(LabelForm labelForm, ConstraintValidatorContext constraintValidatorContext){
        if (labelForm.label_name() == null){
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        Integer userIdInt = (Integer) session.getAttribute("userIdInt");
        if (userIdInt == null) {
            return false;
        }

        Boolean labelExists = labelRepository.existsByLabel(labelForm.label_name(),userIdInt);
        if (labelExists != null && labelExists){
            return false;
        }

        return  true;

    }

}

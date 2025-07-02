package com.example.money.controller;

import com.example.money.service.LabelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class LabelController {

    @Autowired
    LabelService labelService;

    @PostMapping("/input")
    public ResponseEntity<?> input(@RequestBody @Validated LabelForm labelForm, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors",errorMessage));
        }

        Integer userIdInt = (Integer) session.getAttribute("userIdInt");
        YearMonth yearMonth = (YearMonth) session.getAttribute("currentDate");

        labelService.input(labelForm,userIdInt,yearMonth);

        return ResponseEntity.ok(Map.of("message","登録完了"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteMoneyLabel(@RequestBody @Validated DeleteForm deleteForm, BindingResult bindingResult,RedirectAttributes redirectAttributes, HttpSession session){
        if(bindingResult.hasErrors()){
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(Map.of("errors",errorMessage));
        }

        Integer userIdInt = (Integer)session.getAttribute("userIdInt");
        YearMonth yearMonth = (YearMonth) session.getAttribute("currentDate");
        String yearMonthSt = yearMonth.toString();

        labelService.deleteLabel(userIdInt,deleteForm,yearMonthSt);

        return ResponseEntity.ok(Map.of("message","削除しました。"));
    }

}

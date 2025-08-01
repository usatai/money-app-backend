package com.example.money.service;

import com.example.money.controller.LoginForm;
import com.example.money.controller.UserForm;
import com.example.money.model.User;
import com.example.money.repository.MoneyRepository;
import com.example.money.repository.UserRepository;
import com.example.money.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MoneyRepository moneyRepository;

    public List<User> searchAll(){
        return userRepository.findAll();
    }

    public Optional<User> searchId(Integer id){
        return userRepository.findById(id);
    }

    public void save(UserForm userForm){
        userRepository.save(CreateUser(userForm));
    }

    private User CreateUser(UserForm userForm) {
        Date now = new Date();
        String hashedPassword = PasswordUtil.hashPassword(userForm.user_password());

        User user = new User();
        user.setUser_name(userForm.user_name());
        user.setUser_email(userForm.user_email());
        user.setUser_password(hashedPassword);
        user.setCreate_date(now);

        return user;
    }

    public Optional<Integer> getUserid(UserForm userForm){
         return userRepository.user_id(userForm.user_name());
    }

    public Optional<Integer> getLoginUserid(LoginForm loginForm){
        return userRepository.user_id(loginForm.loginUser_name());
    }

    public String getUserPassword(String user_name) {
        return userRepository.user_password(user_name);
    }

    public Optional<Integer> getUserIdByUsername(String user_name) {
        return userRepository.user_id(user_name);
    }

}

package com.example.money.service;

import com.example.money.controller.GestLoginUserForm;
import com.example.money.controller.LoginForm;
import com.example.money.controller.UserForm;
import com.example.money.model.User;
import com.example.money.repository.GestUserRepository;
import com.example.money.repository.UserRepository;
import com.example.money.security.PasswordUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final GestUserRepository gestUserRepository;

    public UserService(UserRepository userRepository,
                       GestUserRepository gestUserRepository) {
        this.userRepository = userRepository;
        this.gestUserRepository = gestUserRepository;
    }

    public List<User> searchAll(){
        return userRepository.findAll();
    }

    public Optional<User> searchId(Integer id){
        return userRepository.findById(id);
    }

    @Transactional
    public void save(UserForm userForm){
        userRepository.save(createUser(userForm));
    }

    @Transactional
    public User gestUserSave(GestLoginUserForm gestLoginUserForm) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, 60); // ★ 1時間
        Date expires = cal.getTime();

        User gestUser = new User();
        gestUser.setUser_name(gestLoginUserForm.gestLoginUserName());
        gestUser.setCreated_date(now);
        gestUser.setExpires_at(expires);
        
        return userRepository.save(gestUser);
    }

    private User createUser(UserForm userForm) {
        Date now = new Date();
        String hashedPassword = PasswordUtil.hashPassword(userForm.user_password());

        User user = new User();
        user.setUser_name(userForm.user_name());
        user.setUser_email(userForm.user_email());
        user.setUser_password(hashedPassword);
        user.setCreated_date(now);
        user.setExpires_at(null);

        return user;
    }

    // ゲストユーザー定期削除
    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Tokyo")
    @Transactional
    public void purgeExpiredGuests(){
        int removed = userRepository.deleteExpired(new Date());
        log.info("Expired gest users removed: {}", removed);
    }

    public Optional<Integer> getUserid(UserForm userForm){
         return userRepository.user_id(userForm.user_name());
    }

    public Optional<Integer> getLoginUserid(LoginForm loginForm){
        return userRepository.user_id(loginForm.loginUser_name());
    }

    public Optional<Integer> getGestUserid(GestLoginUserForm gestLoginUserForm) {
        return gestUserRepository.gest_user_id(gestLoginUserForm.gestLoginUserName());
    }

    public String getUserPassword(String user_name) {
        return userRepository.user_password(user_name);
    }

    public Optional<Integer> getUserIdByUsername(String user_name) {
        return userRepository.user_id(user_name);
    }

}

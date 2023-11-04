package com.example.toby_spring.chapter6.user.service;

import com.example.toby_spring.chapter6.user.domain.User;

public interface UserService {

    void add(User user);
    void upgradeLevels();
}

package com.example.toby_spring.chapter5.user.dao;

import com.example.toby_spring.chapter5.user.domain.User;

import java.util.List;

public interface UserDao {

    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();

    void update(User user);
}

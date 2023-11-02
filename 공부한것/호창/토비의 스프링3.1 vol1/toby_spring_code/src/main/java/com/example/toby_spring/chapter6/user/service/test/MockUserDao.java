package com.example.toby_spring.chapter6.user.service.test;

import com.example.toby_spring.chapter6.user.dao.UserDao;
import com.example.toby_spring.chapter6.user.domain.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MockUserDao implements UserDao {
    private List<User> users;

    private List<User> updated = new ArrayList<>();

    public MockUserDao(List<User> users){
        this.users = users;
    }


    @Override
    public List<User> getAll() {
        return this.users;
    }

    @Override
    public void update(User user) {
        updated.add(user);
    }

    @Override
    public void add(User user) { throw new UnsupportedOperationException(); }
    @Override
    public User get(String id) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAll() {throw new UnsupportedOperationException();}
    @Override
    public int getCount() {throw new UnsupportedOperationException();}
}

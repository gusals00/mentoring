package com.example.toby_spring.chapter6.user.service.test;

import com.example.toby_spring.chapter6.user.domain.User;
import com.example.toby_spring.chapter6.user.service.UserServiceImpl;

public class TestUserService extends UserServiceImpl {

    private String id;
    public TestUserService(String id) {
        this.id = id;
    }

    @Override
    protected void upgradeLevel(User user) {
        if (user.getId().equals(id)) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }
}

package com.example.toby_spring.chapter5.user.service.test;

import com.example.toby_spring.chapter5.user.domain.User;
import com.example.toby_spring.chapter5.user.service.UserService;

import javax.sql.DataSource;

public class TestUserService extends UserService {

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

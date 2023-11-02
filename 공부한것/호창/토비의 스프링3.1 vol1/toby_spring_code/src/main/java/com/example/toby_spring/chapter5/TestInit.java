package com.example.toby_spring.chapter5;

import org.junit.runner.JUnitCore;

public class TestInit {
    public static void main(String[] args) {
        JUnitCore.main("com.example.toby_spring.chapter5.user.dao.UserDaoTest",
                "com.example.toby_spring.chapter5.user.service.test.UserServiceTest",
                "com.example.toby_spring.chapter5.user.service.test.UserTest");
    }
}

package com.example.toby_spring.chapter3;

import org.junit.runner.JUnitCore;

public class TestInit {
    public static void main(String[] args) {
        JUnitCore.main("com.example.toby_spring.chapter3.user.dao.UserDaoTest",
                "com.example.toby_spring.chapter3.JUnitTest",
                "com.example.toby_spring.chapter3.calculator.CalcSumTest");
    }
}

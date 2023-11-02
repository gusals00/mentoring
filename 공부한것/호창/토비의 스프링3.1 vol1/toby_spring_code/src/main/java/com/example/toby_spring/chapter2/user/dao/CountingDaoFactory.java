package com.example.toby_spring.chapter2.user.dao;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {

//    @Bean
//    public UserDao userDao() {
//        UserDao userDao = new UserDao(connectionMaker());
//        return userDao;
//    }
//
//    @Bean
//    public ConnectionMaker connectionMaker() {
//        return new CountingConnectionMaker(realConnectionMaker());
//    }
//
//    @Bean
//    public ConnectionMaker realConnectionMaker() {
//        return new DConnectionMaker();
//    }
}

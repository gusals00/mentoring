package com.example.toby_spring.chapter6.jdk;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HelloUppercase implements Hello{
    Hello hello;
    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}

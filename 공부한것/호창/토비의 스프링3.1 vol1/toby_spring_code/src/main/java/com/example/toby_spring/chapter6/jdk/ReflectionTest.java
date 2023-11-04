package com.example.toby_spring.chapter6.jdk;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReflectionTest {

    @Test
    public void invokeMethod()throws Exception {
        String name = "Spring";

        assertThat(name.length(), is(6));

        Method lengthMethod = String.class.getMethod("length");
        assertThat((Integer)lengthMethod.invoke(name),is(6));

        assertThat(name.charAt(0),is('S'));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character)charAtMethod.invoke(name,0),is('S'));
    }

    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();
        assertThat(hello.sayHello("Toby"),is("Hello Toby"));
        assertThat(hello.sayHi("Toby"),is("Hi Toby"));
        assertThat(hello.sayThankYou("Toby"),is("Thank You Toby"));
    }

    @Test
    public void helloProxy() {
        HelloUppercase proxiedHello = new HelloUppercase(new HelloTarget());
        assertThat(proxiedHello.sayHello("Toby"),is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"),is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"),is("THANK YOU TOBY"));
    }
}

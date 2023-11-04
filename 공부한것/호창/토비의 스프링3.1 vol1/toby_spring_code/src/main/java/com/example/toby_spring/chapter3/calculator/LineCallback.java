package com.example.toby_spring.chapter3.calculator;

import java.io.IOException;

public interface LineCallback<T> {

    T doSomethingWithReader(String line,T value)throws IOException;
}

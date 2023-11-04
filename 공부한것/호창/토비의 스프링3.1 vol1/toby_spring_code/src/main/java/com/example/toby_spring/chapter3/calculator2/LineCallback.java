package com.example.toby_spring.chapter3.calculator2;

import java.io.IOException;

public interface LineCallback {

    Integer doSomethingWithReader(String line,Integer value)throws IOException;
}

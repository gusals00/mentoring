package com.example.toby_spring.chapter3.calculator3;

public class MultiplyCalculator extends AbstractCalculator {
    @Override
    Integer calculateSomething(String line, Integer sum) {
        return Integer.valueOf(line) * sum;
    }

    @Override
    protected Integer initialValue() {
        return 1;
    }
}

package com.example.toby_spring.chapter3.calculator3;

import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration
public class CalcSumTest {

    private AbstractCalculator calculator;
    String numFilePath;

    @Test
    public void sumOfNumbers() throws IOException {
        this.calculator = new AddCalculator();
        this.numFilePath = getClass().getResource("numbers.txt").getPath();
        assertThat(calculator.calculate(numFilePath), is(10));
    }

    @Test
    public void multiplyOfNumbers()throws IOException {
        this.calculator = new MultiplyCalculator();
        this.numFilePath = getClass().getResource("numbers.txt").getPath();
        assertThat(calculator.calculate(numFilePath), is(24));
    }

}
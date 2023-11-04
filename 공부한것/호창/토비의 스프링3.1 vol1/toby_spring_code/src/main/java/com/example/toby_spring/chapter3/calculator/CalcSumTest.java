package com.example.toby_spring.chapter3.calculator;

import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.hamcrest.Matchers.is;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration
public class CalcSumTest {

    private Calculator calculator;
    String numFilePath;

    @Before
    public void setUp() {
        this.calculator = new Calculator();
        this.numFilePath = getClass().getResource("numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum(numFilePath), is(10));
    }

    @Test
    public void multiplyOfNumbers()throws IOException {
        assertThat( calculator.calcMultiply(numFilePath), is(24));
    }

    @Test
    public void concatenateStrings() throws IOException {
        assertThat(calculator.concatenate(numFilePath),is("1234"));
    }


}

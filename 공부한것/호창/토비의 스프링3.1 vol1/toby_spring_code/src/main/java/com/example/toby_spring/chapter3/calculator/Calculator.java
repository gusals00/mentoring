package com.example.toby_spring.chapter3.calculator;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public int calcSum(String filePath) throws IOException {
        return fileReadTemplate(filePath, 0, new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithReader(String line, Integer value) throws IOException {
                return Integer.valueOf(line)+value;
            }
        });
    }

    public int calcMultiply(String filePath) throws IOException {
        return fileReadTemplate(filePath, 1, new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithReader(String line, Integer value) throws IOException {
                return Integer.valueOf(line)*value;
            }
        });
    }

    public String concatenate(String filePath) throws IOException {
        return fileReadTemplate(filePath, "", new LineCallback<String>() {
            @Override
            public String doSomethingWithReader(String line, String value) throws IOException {
                return value+line;
            }
        });
    }

    public <T> T fileReadTemplate(String filePath, T initial, LineCallback<T> callback) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));

            T sum = initial;
            String line = null;
            while ((line = br.readLine()) != null) {
                sum = callback.doSomethingWithReader(line,sum);
            }
            return sum;


        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
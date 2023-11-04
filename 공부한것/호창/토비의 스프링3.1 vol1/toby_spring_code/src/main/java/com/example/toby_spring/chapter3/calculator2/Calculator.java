package com.example.toby_spring.chapter3.calculator2;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public int calcSum(String filePath) throws IOException {
        return fileReadTemplate(filePath, 0, (line, value) -> Integer.valueOf(line)+value);
    }

    public int calcMultiply(String filePath) throws IOException {
        return fileReadTemplate(filePath, 1, (line, value) -> Integer.valueOf(line)*value);
    }


    public Integer fileReadTemplate(String filePath, Integer initial, LineCallback callback) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));

            Integer sum = initial;
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
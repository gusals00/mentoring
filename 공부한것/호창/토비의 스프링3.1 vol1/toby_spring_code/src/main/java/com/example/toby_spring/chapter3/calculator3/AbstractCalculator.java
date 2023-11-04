package com.example.toby_spring.chapter3.calculator3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

abstract class AbstractCalculator {

    public Integer calculate(String filePath) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));

            Integer sum = initialValue();
            String line = null;
            while ((line = br.readLine()) != null) {
                sum = calculateSomething(line,sum);
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

    protected Integer initialValue(){
        return 0;
    }

    abstract Integer calculateSomething(String line, Integer sum);
}

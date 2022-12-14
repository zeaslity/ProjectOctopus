package io.wdd.agent;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

public class SimpleTest {

    public static void main(String[] args) {


        String test = "test.sh";

        int lastIndexOf = test.lastIndexOf(".");

        String substring = test.substring(0, lastIndexOf);
        System.out.println("substring = " + substring);

        Arrays.stream(test.split("\\.")).map(
                st -> {
                    System.out.println("st = " + st);
                    return 1;
                }
        ).collect(Collectors.toList());

    }
}

package org.example;

import java.util.List;
import java.util.stream.Collectors;


public class GrepService {

    public static List<String> grep(String searchString, List<String> lines) {
        return lines.stream()
                .filter(line -> line.contains(searchString))
                .collect(Collectors.toList());
    }

    public static void printResults(List<String> matchingLines) {
        for (String line : matchingLines) {
            System.out.println(line);
        }
    }
}

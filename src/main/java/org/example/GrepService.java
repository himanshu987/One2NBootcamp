package org.example;

import java.util.List;
import java.util.stream.Collectors;

public class GrepService {

    public static List<String> grep(String searchString, List<String> lines, boolean caseInsensitive) {
        if (caseInsensitive) {
            return grepCaseInsensitive(searchString, lines);
        } else {
            return grepCaseSensitive(searchString, lines);
        }
    }

    public static List<String> grep(String searchString, List<String> lines) {
        return grep(searchString, lines, false);
    }

    private static List<String> grepCaseSensitive(String searchString, List<String> lines) {
        return lines.stream()
                .filter(line -> line.contains(searchString))
                .collect(Collectors.toList());
    }

    private static List<String> grepCaseInsensitive(String searchString, List<String> lines) {
        String searchLower = searchString.toLowerCase();
        return lines.stream()
                .filter(line -> line.toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
    }

    @Deprecated
    public static void printResults(List<String> matchingLines) {
        OutputWriter.writeToStdout(matchingLines);
    }
}

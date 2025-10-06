package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: ./mygrep \"search_string\" filename");
            System.exit(1);
        }

        String searchString = args[0];
        String filename = args[1];

        try {
            List<String> lines = readFile(filename);
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }

    public static List<String> readFile(String filename) throws IOException {
        Path path = Path.of(filename);
        return Files.readAllLines(path);
    }
}
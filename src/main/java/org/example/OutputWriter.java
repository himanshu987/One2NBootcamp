package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class OutputWriter {

    public static void writeOutput(List<String> matchingLines, String outputFile)
            throws GrepException, IOException {
        if (outputFile == null) {
            writeToStdout(matchingLines);
        } else {
            writeToFile(matchingLines, outputFile);
        }
    }

    public static void writeToStdout(List<String> lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public static void writeToFile(List<String> lines, String filename)
            throws GrepException, IOException {
        Path path = Path.of(filename);

        if (Files.exists(path)) {
            throw new GrepException("./mygrep: " + filename + ": File already exists");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Failed to write to file: " + filename, e);
        }
    }
}
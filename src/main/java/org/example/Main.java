package org.example;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1) {
            handleStdinMode(args[0]);
        } else if (args.length == 2) {
            handleFileMode(args[0], args[1]);
        } else {
            System.err.println("Usage: ./mygrep \"search_string\" [filename]");
            System.err.println("If filename is omitted, reads from STDIN");
            System.exit(1);
        }
    }

    private static void handleStdinMode(String searchString) {
        try {
            List<String> lines = InputReader.readFromStdin();
            List<String> matchingLines = GrepService.grep(searchString, lines);
            GrepService.printResults(matchingLines);
        } catch (IOException e) {
            System.err.println("./mygrep: error reading from stdin: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void handleFileMode(String searchString, String filename) {
        try {
            List<String> lines = readFile(filename);
            List<String> matchingLines = GrepService.grep(searchString, lines);
            GrepService.printResults(matchingLines);
        } catch (GrepException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("./mygrep: " + filename + ": " + e.getMessage());
            System.exit(1);
        }
    }

    public static List<String> readFile(String filename) throws IOException, GrepException {
        Path path = Path.of(filename);

        if (!Files.exists(path)) {
            throw new GrepException("./mygrep: " + filename + ": open: No such file or directory");
        }

        if (Files.isDirectory(path)) {
            throw new GrepException("./mygrep: " + filename + ": read: Is a directory");
        }

        if (!Files.isReadable(path)) {
            throw new GrepException("./mygrep: " + filename + ": Permission denied");
        }

        try {
            return Files.readAllLines(path);
        } catch (AccessDeniedException e) {
            throw new GrepException("./mygrep: " + filename + ": Permission denied");
        } catch (NoSuchFileException e) {
            throw new GrepException("./mygrep: " + filename + ": open: No such file or directory");
        }
    }
}
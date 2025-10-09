package org.example;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            GrepOptions options = ArgumentParser.parse(args);
            executeGrep(options);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (GrepException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("./mygrep: " + e.getMessage());
            System.exit(1);
        }
    }

    static void executeGrep(GrepOptions options) throws IOException, GrepException {
        List<String> lines = readInput(options);

        List<String> matchingLines = GrepService.grep(options.getSearchString(), lines);

        OutputWriter.writeOutput(matchingLines, options.getOutputFile());
    }

    private static List<String> readInput(GrepOptions options) throws IOException, GrepException {
        if (options.isStdinMode()) {
            return InputReader.readFromStdin();
        } else {
            return readFile(options.getInputFile());
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
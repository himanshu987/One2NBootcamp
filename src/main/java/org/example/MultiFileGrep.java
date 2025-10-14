package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MultiFileGrep {

    public static List<FileGrepResult> grepMultipleFiles(
            String searchString,
            List<Path> files,
            boolean caseInsensitive) throws IOException {

        List<FileGrepResult> results = new ArrayList<>();

        for (Path file : files) {
            try {
                List<String> lines = Main.readFileFromPath(file);
                List<String> matchingLines = GrepService.grep(searchString, lines, caseInsensitive);

                // Only include files with matches (as per grep behavior)
                if (!matchingLines.isEmpty()) {
                    results.add(new FileGrepResult(file.toString(), matchingLines));
                }
            } catch (GrepException e) {
                // Skip files that can't be read (similar to grep behavior)
                // Could log this if logging were enabled
            }
        }

        return results;
    }

    public static String formatLineWithFilename(String filename, String line) {
        return filename + ":" + line;
    }

    public static List<String> formatResultWithFilenames(FileGrepResult result) {
        List<String> formattedLines = new ArrayList<>();
        String filename = result.getFilename();

        for (String line : result.getMatchingLines()) {
            formattedLines.add(formatLineWithFilename(filename, line));
        }

        return formattedLines;
    }

    public static List<String> formatAllResults(List<FileGrepResult> results) {
        List<String> allFormattedLines = new ArrayList<>();

        for (FileGrepResult result : results) {
            allFormattedLines.addAll(formatResultWithFilenames(result));
        }

        return allFormattedLines;
    }
}

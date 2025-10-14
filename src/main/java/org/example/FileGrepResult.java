package org.example;

import java.util.List;

public class FileGrepResult {
    private final String filename;
    private final List<String> matchingLines;

    public FileGrepResult(String filename, List<String> matchingLines) {
        this.filename = filename;
        this.matchingLines = matchingLines;
    }

    public String getFilename() {
        return filename;
    }

    public List<String> getMatchingLines() {
        return matchingLines;
    }

    public boolean hasMatches() {
        return !matchingLines.isEmpty();
    }

    public int getMatchCount() {
        return matchingLines.size();
    }
}

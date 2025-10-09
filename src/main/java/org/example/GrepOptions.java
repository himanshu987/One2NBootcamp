package org.example;

public class GrepOptions {
    private final String searchString;
    private final String inputFile;
    private final String outputFile;
    private final boolean caseInsensitive;

    public GrepOptions(String searchString, String inputFile, String outputFile, boolean caseInsensitive) {
        this.searchString = searchString;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.caseInsensitive = caseInsensitive;
    }

    public GrepOptions(String searchString, String inputFile, String outputFile) {
        this(searchString, inputFile, outputFile, false);
    }

    public String getSearchString() {
        return searchString;
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public boolean isStdinMode() {
        return inputFile == null;
    }

    public boolean hasOutputFile() {
        return outputFile != null;
    }
}

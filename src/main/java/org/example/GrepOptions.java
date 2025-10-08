package org.example;

public class GrepOptions {
    private final String searchString;
    private final String inputFile;
    private final String outputFile;

    public GrepOptions(String searchString, String inputFile, String outputFile) {
        this.searchString = searchString;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
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

    public boolean isStdinMode() {
        return inputFile == null;
    }

    public boolean hasOutputFile() {
        return outputFile != null;
    }
}

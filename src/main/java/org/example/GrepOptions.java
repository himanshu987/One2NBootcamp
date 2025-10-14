package org.example;

public class GrepOptions {
    private final String searchString;
    private final String inputPath;
    private final String outputFile;
    private final boolean caseInsensitive;
    private final boolean recursive;

    public GrepOptions(String searchString, String inputPath, String outputFile,
                       boolean caseInsensitive, boolean recursive) {
        this.searchString = searchString;
        this.inputPath = inputPath;
        this.outputFile = outputFile;
        this.caseInsensitive = caseInsensitive;
        this.recursive = recursive;
    }

    public GrepOptions(String searchString, String inputPath, String outputFile, boolean caseInsensitive) {
        this(searchString, inputPath, outputFile, caseInsensitive, false);
    }

    public GrepOptions(String searchString, String inputPath, String outputFile) {
        this(searchString, inputPath, outputFile, false, false);
    }

    public String getSearchString() {
        return searchString;
    }

    public String getInputPath() {
        return inputPath;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public boolean isStdinMode() {
        return inputPath == null;
    }

    public boolean hasOutputFile() {
        return outputFile != null;
    }

    public boolean isDirectoryMode() {
        return inputPath != null && DirectoryReader.isDirectory(inputPath);
    }
}

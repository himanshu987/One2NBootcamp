package org.example;

public class ArgumentParser {

    public static GrepOptions parse(String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: ./mygrep \"search_string\" [filename] [-o output_file]");
        }

        String searchString = args[0];
        String inputFile = null;
        String outputFile = null;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];

            if ("-o".equals(arg)) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException("./mygrep: option requires an argument -- 'o'");
                }
                outputFile = args[i + 1];
                i += 2;
            } else {
                if (inputFile != null) {
                    throw new IllegalArgumentException("./mygrep: too many input files specified");
                }
                inputFile = arg;
                i++;
            }
        }

        return new GrepOptions(searchString, inputFile, outputFile);
    }
}

package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArgumentParser {

    public static GrepOptions parse(String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: ./mygrep [-i] \"search_string\" [filename] [-o output_file]");
        }

        List<String> argList = new ArrayList<>();
        Collections.addAll(argList, args);

        boolean caseInsensitive = false;
        String searchString = null;
        String inputFile = null;
        String outputFile = null;

        int i = 0;

        while (i < argList.size()) {
            String arg = argList.get(i);

            if ("-i".equals(arg)) {
                caseInsensitive = true;
                i++;
            } else if ("-o".equals(arg)) {
                if (i + 1 >= argList.size()) {
                    throw new IllegalArgumentException("./mygrep: option requires an argument -- 'o'");
                }
                outputFile = argList.get(i + 1);
                i += 2;
            } else if (searchString == null) {
                searchString = arg;
                i++;
            } else if (inputFile == null) {
                inputFile = arg;
                i++;
            } else {
                throw new IllegalArgumentException("./mygrep: unexpected argument: " + arg);
            }
        }

        if (searchString == null) {
            throw new IllegalArgumentException("./mygrep: search string is required");
        }

        return new GrepOptions(searchString, inputFile, outputFile, caseInsensitive);
    }
}

package org.example;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryReader {

    public static List<Path> listFilesRecursively(String directoryPath) throws IOException {
        Path path = Path.of(directoryPath);

        if (!Files.exists(path)) {
            throw new IOException("Directory does not exist: " + directoryPath);
        }

        if (!Files.isDirectory(path)) {
            throw new IOException("Not a directory: " + directoryPath);
        }

        if (!Files.isReadable(path)) {
            throw new IOException("Directory is not readable: " + directoryPath);
        }

        try (Stream<Path> paths = Files.walk(path)) {
            List<Path> files = paths
                    .filter(Files::isRegularFile)
                    .filter(Files::isReadable)
                    .sorted() // Sort for consistent output
                    .collect(Collectors.toList());

            return files;
        }
    }

    public static boolean isDirectory(String path) {
        return Files.isDirectory(Path.of(path));
    }

    public static List<Path> listFiles(String directoryPath) throws IOException {
        Path path = Path.of(directoryPath);

        if (!Files.isDirectory(path)) {
            throw new IOException("Not a directory: " + directoryPath);
        }

        try (Stream<Path> paths = Files.list(path)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(Files::isReadable)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }
}

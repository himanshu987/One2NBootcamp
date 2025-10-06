package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileReadingTest {

    @Test
    void testReadEmptyFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        List<String> lines = Main.readFile(file.toString());

        assertTrue(lines.isEmpty(), "Empty file should return empty list");
    }

    @Test
    void testReadSingleLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.writeString(file, "Hello World");

        List<String> lines = Main.readFile(file.toString());

        assertEquals(1, lines.size());
        assertEquals("Hello World", lines.get(0));
    }

    @Test
    void testReadMultipleLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("multiple.txt");
        Files.writeString(file, "Line 1\nLine 2\nLine 3");

        List<String> lines = Main.readFile(file.toString());

        assertEquals(3, lines.size());
        assertEquals("Line 1", lines.get(0));
        assertEquals("Line 2", lines.get(1));
        assertEquals("Line 3", lines.get(2));
    }

    @Test
    void testReadNonExistentFile() {
        assertThrows(IOException.class, () -> {
            Main.readFile("non_existent_file.txt");
        });
    }
}
package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutputWriterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testWriteToStdoutEmpty() {
        OutputWriter.writeToStdout(Collections.emptyList());

        assertEquals("", outContent.toString());
    }

    @Test
    void testWriteToStdoutSingleLine() {
        List<String> lines = Arrays.asList("lorem ipsum");
        OutputWriter.writeToStdout(lines);

        assertEquals("lorem ipsum\n", outContent.toString());
    }

    @Test
    void testWriteToStdoutMultipleLines() {
        List<String> lines = Arrays.asList("lorem ipsum", "a dummy text usually contains lorem ipsum");
        OutputWriter.writeToStdout(lines);

        assertEquals("lorem ipsum\na dummy text usually contains lorem ipsum\n",
                outContent.toString());
    }

    @Test
    void testWriteToFileCreatesNewFile(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("out.txt");
        List<String> lines = Arrays.asList("lorem ipsum", "another line");

        OutputWriter.writeToFile(lines, outputFile.toString());

        assertTrue(Files.exists(outputFile), "Output file should be created");

        List<String> writtenLines = Files.readAllLines(outputFile);
        assertEquals(2, writtenLines.size());
        assertEquals("lorem ipsum", writtenLines.get(0));
        assertEquals("another line", writtenLines.get(1));
    }

    @Test
    void testWriteToFileWithEmptyContent(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("empty.txt");

        OutputWriter.writeToFile(Collections.emptyList(), outputFile.toString());

        assertTrue(Files.exists(outputFile));
        List<String> writtenLines = Files.readAllLines(outputFile);
        assertTrue(writtenLines.isEmpty());
    }

    @Test
    void testWriteToFileAlreadyExists(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("existing.txt");
        Files.writeString(outputFile, "existing content");

        List<String> lines = List.of("new content");

        GrepException exception = assertThrows(GrepException.class, () -> {
            OutputWriter.writeToFile(lines, outputFile.toString());
        });

        assertTrue(exception.getMessage().contains("File already exists"));
        assertTrue(exception.getMessage().contains("existing.txt"));

        String content = Files.readString(outputFile);
        assertEquals("existing content", content);
    }

    @Test
    void testWriteToFileSingleLine(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("single.txt");
        List<String> lines = List.of("single line");

        OutputWriter.writeToFile(lines, outputFile.toString());

        List<String> writtenLines = Files.readAllLines(outputFile);
        assertEquals(1, writtenLines.size());
        assertEquals("single line", writtenLines.getFirst());
    }

    @Test
    void testWriteToFileSpecialCharacters(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("special.txt");
        List<String> lines = Arrays.asList("@#$%^&*()", "tab\there", "newline test");

        OutputWriter.writeToFile(lines, outputFile.toString());

        List<String> writtenLines = Files.readAllLines(outputFile);
        assertEquals(3, writtenLines.size());
        assertEquals("@#$%^&*()", writtenLines.get(0));
        assertEquals("tab\there", writtenLines.get(1));
    }

    @Test
    void testWriteToFileLongContent(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("long.txt");

        List<String> lines = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            lines.add("Line " + i);
        }

        OutputWriter.writeToFile(lines, outputFile.toString());

        List<String> writtenLines = Files.readAllLines(outputFile);
        assertEquals(1000, writtenLines.size());
        assertEquals("Line 0", writtenLines.get(0));
        assertEquals("Line 999", writtenLines.get(999));
    }

    @Test
    void testWriteOutputToStdout() throws Exception {
        List<String> lines = Arrays.asList("line1", "line2");

        OutputWriter.writeOutput(lines, null);

        assertEquals("line1\nline2\n", outContent.toString());
    }

    @Test
    void testWriteOutputToFile(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("output.txt");
        List<String> lines = Arrays.asList("line1", "line2");

        OutputWriter.writeOutput(lines, outputFile.toString());

        assertTrue(Files.exists(outputFile));
        List<String> writtenLines = Files.readAllLines(outputFile);
        assertEquals(2, writtenLines.size());
    }

    @Test
    void testWriteOutputFileAlreadyExists(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("existing.txt");
        Files.writeString(outputFile, "content");

        List<String> lines = Arrays.asList("new");

        assertThrows(GrepException.class, () -> {
            OutputWriter.writeOutput(lines, outputFile.toString());
        });
    }
}
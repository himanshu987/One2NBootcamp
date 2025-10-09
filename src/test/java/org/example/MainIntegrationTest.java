package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainIntegrationTest {
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
    void testFileInputFileOutput(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("loreipsum.txt");
        Files.writeString(inputFile,
                "lorem ipsum\n" +
                        "dolor sit amet\n" +
                        "a dummy text usually contains lorem ipsum\n" +
                        "consectetur adipiscing elit\n"
        );

        Path outputFile = tempDir.resolve("out.txt");

        GrepOptions options = new GrepOptions("lorem", inputFile.toString(), outputFile.toString());
        Main.executeGrep(options);

        assertTrue(Files.exists(outputFile), "Output file should be created");

        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals(2, outputLines.size());
        assertEquals("lorem ipsum", outputLines.get(0));
        assertEquals("a dummy text usually contains lorem ipsum", outputLines.get(1));

        assertEquals("", outContent.toString(), "Should not print to STDOUT when using -o flag");
    }

    @Test
    void testFileOutputWithNoMatches(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "line1\nline2\nline3\n");

        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options = new GrepOptions("nomatch", inputFile.toString(), outputFile.toString());
        Main.executeGrep(options);

        assertTrue(Files.exists(outputFile));
        List<String> outputLines = Files.readAllLines(outputFile);
        assertTrue(outputLines.isEmpty(), "Output file should be empty when no matches");
    }

    @Test
    void testFileOutputAlreadyExists(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "test content\n");

        Path outputFile = tempDir.resolve("existing.txt");
        Files.writeString(outputFile, "old content");

        GrepOptions options = new GrepOptions("test", inputFile.toString(), outputFile.toString());

        GrepException exception = assertThrows(GrepException.class, () -> {
            Main.executeGrep(options);
        });

        assertTrue(exception.getMessage().contains("File already exists"));

        String originalContent = Files.readString(outputFile);
        assertEquals("old content", originalContent);
    }

    @Test
    void testFileInputStdoutOutput(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "test line\nanother line\n");

        GrepOptions options = new GrepOptions("test", inputFile.toString(), null);
        Main.executeGrep(options);

        assertEquals("test line\n", outContent.toString());
    }

    @Test
    void testStdinInputFileOutput(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options = new GrepOptions("search", null, outputFile.toString());

        assertNull(options.getInputFile());
        assertTrue(options.hasOutputFile());
        assertTrue(options.isStdinMode());
    }

    @Test
    void testOutputFileWithSpecialCharacters(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "email@test.com\nuser@domain.com\nplain text\n");

        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options = new GrepOptions("@", inputFile.toString(), outputFile.toString());
        Main.executeGrep(options);

        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals(2, outputLines.size());
        assertTrue(outputLines.contains("email@test.com"));
        assertTrue(outputLines.contains("user@domain.com"));
    }

    @Test
    void testOutputFileWithLargeContent(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("large.txt");
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            if (i % 10 == 0) {
                content.append("match_").append(i).append("\n");
            } else {
                content.append("other_").append(i).append("\n");
            }
        }
        Files.writeString(inputFile, content.toString());

        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options = new GrepOptions("match_", inputFile.toString(), outputFile.toString());
        Main.executeGrep(options);

        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals(100, outputLines.size());
    }

    @Test
    void testMultipleGrepOperationsToSeparateFiles(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile,
                "Java is great\n" +
                        "Python is great\n" +
                        "Both are languages\n"
        );

        Path outputFile1 = tempDir.resolve("java_output.txt");
        GrepOptions options1 = new GrepOptions("Java", inputFile.toString(), outputFile1.toString());
        Main.executeGrep(options1);

        Path outputFile2 = tempDir.resolve("great_output.txt");
        GrepOptions options2 = new GrepOptions("great", inputFile.toString(), outputFile2.toString());
        Main.executeGrep(options2);

        List<String> output1 = Files.readAllLines(outputFile1);
        assertEquals(1, output1.size());
        assertEquals("Java is great", output1.get(0));

        List<String> output2 = Files.readAllLines(outputFile2);
        assertEquals(2, output2.size());
        assertTrue(output2.contains("Java is great"));
        assertTrue(output2.contains("Python is great"));
    }

    @Test
    void testCaseSensitiveSearchWithFileOutput(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "Hello\nhello\nHELLO\nHeLLo\n");

        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options = new GrepOptions("Hello", inputFile.toString(), outputFile.toString());
        Main.executeGrep(options);

        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals(1, outputLines.size());
        assertEquals("Hello", outputLines.get(0));
    }

    @Test
    void testPartialMatchWithFileOutput(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "foo\nfood\nfoobar\nbar\n");

        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options = new GrepOptions("foo", inputFile.toString(), outputFile.toString());
        Main.executeGrep(options);

        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals(3, outputLines.size());
        assertTrue(outputLines.contains("foo"));
        assertTrue(outputLines.contains("food"));
        assertTrue(outputLines.contains("foobar"));
    }

    @Test
    void testEmptySearchStringWithFileOutput(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "line1\nline2\n");

        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options = new GrepOptions("", inputFile.toString(), outputFile.toString());
        Main.executeGrep(options);

        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals(2, outputLines.size());
    }

    @Test
    void testInputFileErrors(@TempDir Path tempDir) throws Exception {
        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options1 = new GrepOptions("search", "nonexistent.txt", outputFile.toString());
        GrepException exception1 = assertThrows(GrepException.class, () -> {
            Main.executeGrep(options1);
        });
        assertTrue(exception1.getMessage().contains("No such file or directory"));

        Path dir = tempDir.resolve("dir");
        Files.createDirectory(dir);
        GrepOptions options2 = new GrepOptions("search", dir.toString(), outputFile.toString());
        GrepException exception2 = assertThrows(GrepException.class, () -> {
            Main.executeGrep(options2);
        });
        assertTrue(exception2.getMessage().contains("Is a directory"));
    }
}
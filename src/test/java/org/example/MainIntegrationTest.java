package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

    @Test
    void testStory4StdinExample() throws Exception {
        String input = "bar\nbarbazfoo\nFoobar\nfood\n";
        InputStream stdin = new ByteArrayInputStream(input.getBytes());

        List<String> lines = InputReader.readFromInputStream(stdin);

        GrepOptions options = new GrepOptions("foo", null, null, true);
        List<String> matches = GrepService.grep(
                options.getSearchString(),
                lines,
                options.isCaseInsensitive()
        );

        OutputWriter.writeToStdout(matches);

        String output = outContent.toString();
        String[] outputLines = output.split("\n");

        assertEquals(3, outputLines.length);
        assertEquals("barbazfoo", outputLines[0]);
        assertEquals("Foobar", outputLines[1]);
        assertEquals("food", outputLines[2]);
    }

    @Test
    void testCaseInsensitiveFileInput(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("test.txt");
        Files.writeString(inputFile, "Hello\nhello\nHELLO\nworld\n");

        GrepOptions options = new GrepOptions("hello", inputFile.toString(), null, true);
        Main.executeGrep(options);

        String output = outContent.toString();
        String[] lines = output.split("\n");

        assertEquals(3, lines.length);
        assertTrue(output.contains("Hello"));
        assertTrue(output.contains("hello"));
        assertTrue(output.contains("HELLO"));
        assertFalse(output.contains("world"));
    }

    @Test
    void testCaseInsensitiveWithFileOutput(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("filename.txt");
        Files.writeString(inputFile, "FOO\nfoo\nFoo\nbar\n");

        Path outputFile = tempDir.resolve("outfile.txt");

        GrepOptions options = new GrepOptions("foo", inputFile.toString(), outputFile.toString(), true);
        Main.executeGrep(options);

        assertTrue(Files.exists(outputFile));
        List<String> outputLines = Files.readAllLines(outputFile);

        assertEquals(3, outputLines.size());
        assertTrue(outputLines.contains("FOO"));
        assertTrue(outputLines.contains("foo"));
        assertTrue(outputLines.contains("Foo"));
    }

    @Test
    void testCombinedFlagsStory4Requirement(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("filename.txt");
        Files.writeString(inputFile,
                "This is FOO\n" +
                        "This is foo\n" +
                        "This is Foo\n" +
                        "This is bar\n"
        );

        Path outputFile = tempDir.resolve("outfile.txt");

        String[] args = {"-i", "foo", inputFile.toString(), "-o", outputFile.toString()};
        GrepOptions options = ArgumentParser.parse(args);

        assertTrue(options.isCaseInsensitive());
        assertEquals("foo", options.getSearchString());
        assertEquals(inputFile.toString(), options.getInputFile());
        assertEquals(outputFile.toString(), options.getOutputFile());

        Main.executeGrep(options);

        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals(3, outputLines.size());
    }

    @Test
    void testCaseSensitiveVsCaseInsensitiveComparison(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "Hello\nhello\nHELLO\nworld\n");

        GrepOptions caseSensitiveOpts = new GrepOptions("hello", inputFile.toString(), null, false);
        Main.executeGrep(caseSensitiveOpts);
        String caseSensitiveOutput = outContent.toString();
        outContent.reset();

        GrepOptions caseInsensitiveOpts = new GrepOptions("hello", inputFile.toString(), null, true);
        Main.executeGrep(caseInsensitiveOpts);
        String caseInsensitiveOutput = outContent.toString();

        assertEquals(1, caseSensitiveOutput.split("\n").length);
        assertTrue(caseSensitiveOutput.contains("hello"));
        assertFalse(caseSensitiveOutput.contains("Hello"));

        assertEquals(3, caseInsensitiveOutput.split("\n").length);
        assertTrue(caseInsensitiveOutput.contains("Hello"));
        assertTrue(caseInsensitiveOutput.contains("hello"));
        assertTrue(caseInsensitiveOutput.contains("HELLO"));
    }

    @Test
    void testCaseInsensitivePreservesCase(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "FooBar\nfoobar\nFOOBAR\n");

        Path outputFile = tempDir.resolve("output.txt");

        GrepOptions options = new GrepOptions("foobar", inputFile.toString(), outputFile.toString(), true);
        Main.executeGrep(options);

        List<String> lines = Files.readAllLines(outputFile);

        assertEquals("FooBar", lines.get(0));
        assertEquals("foobar", lines.get(1));
        assertEquals("FOOBAR", lines.get(2));
    }

    @Test
    void testCaseInsensitiveNoMatches(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "bar\nbaz\nqux\n");

        GrepOptions options = new GrepOptions("foo", inputFile.toString(), null, true);
        Main.executeGrep(options);

        assertEquals("", outContent.toString());
    }

    @Test
    void testCaseInsensitivePartialMatch(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile,
                "The word FOOBAR is here\n" +
                        "Looking for foobar\n" +
                        "No match\n"
        );

        GrepOptions options = new GrepOptions("foobar", inputFile.toString(), null, true);
        Main.executeGrep(options);

        String output = outContent.toString();
        assertTrue(output.contains("FOOBAR"));
        assertTrue(output.contains("foobar"));
        assertFalse(output.contains("No match"));
    }

    @Test
    void testBackwardCompatibilityWithoutIFlag(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "Hello\nhello\nHELLO\n");

        String[] args = {"Hello", inputFile.toString()};
        GrepOptions options = ArgumentParser.parse(args);

        assertFalse(options.isCaseInsensitive());

        Main.executeGrep(options);

        String output = outContent.toString();
        assertEquals("Hello\n", output);
    }

    @Test
    void testMultipleFlagsInDifferentOrders(@TempDir Path tempDir) throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "FOO\nfoo\nbar\n");

        Path outputFile = tempDir.resolve("output.txt");

        String[] args1 = {"-i", "foo", inputFile.toString(), "-o", outputFile.toString()};
        GrepOptions opts1 = ArgumentParser.parse(args1);
        assertTrue(opts1.isCaseInsensitive());
        assertEquals(outputFile.toString(), opts1.getOutputFile());
    }
}
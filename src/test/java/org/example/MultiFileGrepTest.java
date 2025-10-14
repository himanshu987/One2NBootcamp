package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiFileGrepTest {

    @Test
    void testFormatLineWithFilename() {
        String formatted = MultiFileGrep.formatLineWithFilename("test.txt", "this is a test");

        assertEquals("test.txt:this is a test", formatted);
    }

    @Test
    void testFormatResultWithFilenames() {
        List<String> matches = Arrays.asList("line 1", "line 2");
        FileGrepResult result = new FileGrepResult("file.txt", matches);

        List<String> formatted = MultiFileGrep.formatResultWithFilenames(result);

        assertEquals(2, formatted.size());
        assertEquals("file.txt:line 1", formatted.get(0));
        assertEquals("file.txt:line 2", formatted.get(1));
    }

    @Test
    void testFormatAllResults() {
        FileGrepResult result1 = new FileGrepResult("file1.txt", Arrays.asList("match1", "match2"));
        FileGrepResult result2 = new FileGrepResult("file2.txt", Arrays.asList("match3"));

        List<String> formatted = MultiFileGrep.formatAllResults(Arrays.asList(result1, result2));

        assertEquals(3, formatted.size());
        assertEquals("file1.txt:match1", formatted.get(0));
        assertEquals("file1.txt:match2", formatted.get(1));
        assertEquals("file2.txt:match3", formatted.get(2));
    }

    @Test
    void testGrepMultipleFilesSingleFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "this is a test\nanother line\ntest again\n");

        List<FileGrepResult> results = MultiFileGrep.grepMultipleFiles(
                "test",
                Arrays.asList(file),
                false
        );

        assertEquals(1, results.size());
        assertEquals(2, results.get(0).getMatchCount());
    }

    @Test
    void testGrepMultipleFilesMultipleFiles(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "test line 1\nno match\ntest line 2\n");

        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "another test\n");

        List<FileGrepResult> results = MultiFileGrep.grepMultipleFiles(
                "test",
                Arrays.asList(file1, file2),
                false
        );

        assertEquals(2, results.size());
        assertEquals(2, results.get(0).getMatchCount());
        assertEquals(1, results.get(1).getMatchCount());
    }

    @Test
    void testGrepMultipleFilesNoMatches(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "no matches here\n");

        List<FileGrepResult> results = MultiFileGrep.grepMultipleFiles(
                "test",
                Arrays.asList(file),
                false
        );

        assertTrue(results.isEmpty(), "Files with no matches should not be included");
    }

    @Test
    void testGrepMultipleFilesSomeWithMatches(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "has test\n");

        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file2, "no match\n");

        Path file3 = tempDir.resolve("file3.txt");
        Files.writeString(file3, "also has test\n");

        List<FileGrepResult> results = MultiFileGrep.grepMultipleFiles(
                "test",
                Arrays.asList(file1, file2, file3),
                false
        );

        assertEquals(2, results.size(), "Only files with matches should be included");
        assertTrue(results.get(0).getFilename().endsWith("file1.txt"));
        assertTrue(results.get(1).getFilename().endsWith("file3.txt"));
    }

    @Test
    void testGrepMultipleFilesCaseInsensitive(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "TEST\ntest\nTest\n");

        List<FileGrepResult> results = MultiFileGrep.grepMultipleFiles(
                "test",
                Arrays.asList(file),
                true
        );

        assertEquals(1, results.size());
        assertEquals(3, results.get(0).getMatchCount());
    }

    @Test
    void testFileGrepResult() {
        List<String> matches = Arrays.asList("line1", "line2");
        FileGrepResult result = new FileGrepResult("test.txt", matches);

        assertEquals("test.txt", result.getFilename());
        assertEquals(2, result.getMatchCount());
        assertTrue(result.hasMatches());
        assertEquals(matches, result.getMatchingLines());
    }

    @Test
    void testFileGrepResultNoMatches() {
        FileGrepResult result = new FileGrepResult("test.txt", List.of());

        assertFalse(result.hasMatches());
        assertEquals(0, result.getMatchCount());
    }
}
package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrepServiceTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream printStream = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(printStream);
    }

    @Test
    void testGrepNoMatches() {
        List<String> lines = Arrays.asList(
                "Hello World",
                "Java Programming",
                "Testing Code"
        );

        List<String> result = GrepService.grep("Python", lines);

        assertTrue(result.isEmpty(), "Should return empty list when no matches");
    }

    @Test
    void testGrepSingleMatch() {
        List<String> lines = Arrays.asList(
                "Hello World",
                "Java Programming",
                "Testing Code"
        );

        List<String> result = GrepService.grep("Java", lines);

        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0));
    }

    @Test
    void testGrepMultipleMatches() {
        List<String> lines = Arrays.asList(
                "I found the search_string in the file.",
                "This line has nothing special",
                "Another line also contains the search_string"
        );

        List<String> result = GrepService.grep("search_string", lines);

        assertEquals(2, result.size());
        assertEquals("I found the search_string in the file.", result.get(0));
        assertEquals("Another line also contains the search_string", result.get(1));
    }

    @Test
    void testGrepCaseSensitive() {
        List<String> lines = Arrays.asList(
                "Hello World",
                "hello world",
                "HELLO WORLD"
        );

        List<String> result = GrepService.grep("Hello", lines);

        assertEquals(1, result.size());
        assertEquals("Hello World", result.get(0));
    }

    @Test
    void testGrepPartialMatch() {
        List<String> lines = Arrays.asList(
                "bar",
                "barbazfoo",
                "Foobar",
                "food"
        );

        List<String> result = GrepService.grep("foo", lines);

        assertEquals(2, result.size());
        assertEquals("barbazfoo", result.get(0));
        assertEquals("food", result.get(1));
    }

    @Test
    void testPrintResultsEmpty() {
        GrepService.printResults(Collections.emptyList());

        assertEquals("", outputStream.toString());
    }

    @Test
    void testPrintResultsSingleLine() {
        List<String> lines = Arrays.asList("barbazfoo");
        GrepService.printResults(lines);

        assertEquals("barbazfoo\n", outputStream.toString());
    }

    @Test
    void testPrintResultsMultipleLines() {
        List<String> lines = Arrays.asList("barbazfoo", "food");
        GrepService.printResults(lines);

        assertEquals("barbazfoo\nfood\n", outputStream.toString());
    }

    @Test
    void testGrepEmptySearchString() {
        List<String> lines = Arrays.asList("Line 1", "Line 2");

        List<String> result = GrepService.grep("", lines);

        assertEquals(2, result.size(), "Empty search string should match all lines");
    }

    @Test
    void testGrepEmptyLines() {
        List<String> result = GrepService.grep("test", Collections.emptyList());

        assertTrue(result.isEmpty());
    }
}
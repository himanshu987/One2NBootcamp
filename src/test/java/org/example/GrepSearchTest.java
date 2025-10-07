package org.example;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrepSearchTest {

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
}
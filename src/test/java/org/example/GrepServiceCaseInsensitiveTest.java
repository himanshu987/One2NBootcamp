package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrepServiceCaseInsensitiveTest {

    @Test
    void testCaseInsensitiveBasic() {
        List<String> lines = Arrays.asList(
                "Hello World",
                "hello world",
                "HELLO WORLD",
                "HeLLo WoRLd"
        );

        List<String> result = GrepService.grep("hello", lines, true);

        assertEquals(4, result.size(), "All lines should match with case-insensitive search");
    }

    @Test
    void testCaseInsensitiveUppercaseSearch() {
        List<String> lines = Arrays.asList(
                "Hello World",
                "hello world",
                "HELLO WORLD"
        );

        List<String> result = GrepService.grep("HELLO", lines, true);

        assertEquals(3, result.size(), "Should match regardless of search string case");
    }

    @Test
    void testCaseInsensitiveMixedCase() {
        List<String> lines = Arrays.asList(
                "FooBar",
                "foobar",
                "FOOBAR",
                "FoObAr"
        );

        List<String> result = GrepService.grep("FoObAr", lines, true);

        assertEquals(4, result.size());
    }

    @Test
    void testCaseInsensitivePartialMatch() {
        List<String> lines = Arrays.asList(
                "The FOO is here",
                "Looking for foo",
                "FOOBAR contains it",
                "No match here"
        );

        List<String> result = GrepService.grep("foo", lines, true);

        assertEquals(3, result.size());
        assertFalse(result.contains("No match here"));
    }

    @Test
    void testCaseInsensitivePreservesOriginalCase() {
        List<String> lines = Arrays.asList("Hello World", "HELLO WORLD");

        List<String> result = GrepService.grep("hello", lines, true);

        assertEquals("Hello World", result.get(0), "Should preserve original case");
        assertEquals("HELLO WORLD", result.get(1), "Should preserve original case");
    }

    @Test
    void testCaseInsensitiveNoMatches() {
        List<String> lines = Arrays.asList("bar", "baz", "qux");

        List<String> result = GrepService.grep("foo", lines, true);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCaseInsensitiveEmptyLines() {
        List<String> result = GrepService.grep("test", Collections.emptyList(), true);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCaseInsensitiveEmptySearchString() {
        List<String> lines = Arrays.asList("Line 1", "Line 2");

        List<String> result = GrepService.grep("", lines, true);

        assertEquals(2, result.size(), "Empty search string should match all lines");
    }

    @Test
    void testCaseSensitiveVsCaseInsensitive() {
        List<String> lines = Arrays.asList(
                "Foo",
                "foo",
                "FOO",
                "bar"
        );

        List<String> caseSensitive = GrepService.grep("foo", lines, false);
        assertEquals(1, caseSensitive.size());
        assertEquals("foo", caseSensitive.getFirst());

        List<String> caseInsensitive = GrepService.grep("foo", lines, true);
        assertEquals(3, caseInsensitive.size());
        assertTrue(caseInsensitive.contains("Foo"));
        assertTrue(caseInsensitive.contains("foo"));
        assertTrue(caseInsensitive.contains("FOO"));
    }

    @Test
    void testBackwardCompatibilityDefaultCaseSensitive() {
        List<String> lines = Arrays.asList("Hello", "hello", "HELLO");

        List<String> result = GrepService.grep("Hello", lines);

        assertEquals(1, result.size());
        assertEquals("Hello", result.getFirst());
    }

    @Test
    void testCaseInsensitiveSpecialCharacters() {
        List<String> lines = Arrays.asList(
                "Email@Test.Com",
                "email@test.com",
                "EMAIL@TEST.COM"
        );

        List<String> result = GrepService.grep("email@test", lines, true);

        assertEquals(3, result.size());
    }

    @Test
    void testCaseInsensitiveWithNumbers() {
        List<String> lines = Arrays.asList("Test123", "test123", "TEST123");

        List<String> result = GrepService.grep("test123", lines, true);

        assertEquals(3, result.size());
    }

    @Test
    void testCaseInsensitiveUnicode() {
        List<String> lines = Arrays.asList("Café", "café", "CAFÉ");

        List<String> result = GrepService.grep("café", lines, true);

        assertFalse(result.isEmpty(), "Should match at least some lines");
    }

    @Test
    void testCaseInsensitiveLongLines() {
        String longLine = "This is a very long line that contains the word FOOBAR somewhere in the middle of it";
        List<String> lines = Arrays.asList(longLine, "short line");

        List<String> result = GrepService.grep("foobar", lines, true);

        assertEquals(1, result.size());
        assertEquals(longLine, result.getFirst());
    }

    @Test
    void testCaseInsensitiveMultipleOccurrencesInLine() {
        List<String> lines = Arrays.asList(
                "FOO and foo and Foo",
                "no match",
                "single foo"
        );

        List<String> result = GrepService.grep("foo", lines, true);

        assertEquals(2, result.size());
        assertTrue(result.contains("FOO and foo and Foo"));
        assertTrue(result.contains("single foo"));
    }
}
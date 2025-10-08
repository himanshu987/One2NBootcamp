package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InputReaderTest {

    @Test
    void testReadEmptyInput() throws IOException {
        String input = "";
        InputStream stdin = new ByteArrayInputStream(input.getBytes());

        List<String> lines = InputReader.readFromInputStream(stdin);

        assertTrue(lines.isEmpty(), "Empty input should produce empty list");
    }

    @Test
    void testReadSingleLine() throws IOException {
        String input = "hellofoo\n";
        InputStream stdin = new ByteArrayInputStream(input.getBytes());

        List<String> lines = InputReader.readFromInputStream(stdin);

        assertEquals(1, lines.size());
        assertEquals("hellofoo", lines.get(0));
    }

    @Test
    void testReadMultipleLines() throws IOException {
        String input = "bar\nhellofoo\nFoobar\nfood\n";
        InputStream stdin = new ByteArrayInputStream(input.getBytes());

        List<String> lines = InputReader.readFromInputStream(stdin);

        assertEquals(4, lines.size());
        assertEquals("bar", lines.get(0));
        assertEquals("hellofoo", lines.get(1));
        assertEquals("Foobar", lines.get(2));
        assertEquals("food", lines.get(3));
    }

    @Test
    void testReadLinesWithoutTrailingNewline() throws IOException {
        String input = "line1\nline2\nline3";
        InputStream stdin = new ByteArrayInputStream(input.getBytes());

        List<String> lines = InputReader.readFromInputStream(stdin);

        assertEquals(3, lines.size());
        assertEquals("line3", lines.get(2));
    }

    @Test
    void testReadLinesWithEmptyLines() throws IOException {
        String input = "line1\n\nline3\n";
        InputStream stdin = new ByteArrayInputStream(input.getBytes());

        List<String> lines = InputReader.readFromInputStream(stdin);

        assertEquals(3, lines.size());
        assertEquals("line1", lines.get(0));
        assertEquals("", lines.get(1));
        assertEquals("line3", lines.get(2));
    }

    @Test
    void testReadSpecialCharacters() throws IOException {
        String input = "@#$%\n!@#$%^&*()\ntab\there\n";
        InputStream stdin = new ByteArrayInputStream(input.getBytes());

        List<String> lines = InputReader.readFromInputStream(stdin);

        assertEquals(3, lines.size());
        assertEquals("@#$%", lines.get(0));
        assertEquals("!@#$%^&*()", lines.get(1));
        assertEquals("tab\there", lines.get(2));
    }

    @Test
    void testReadLongInput() throws IOException {
        StringBuilder inputBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            inputBuilder.append("Line ").append(i).append("\n");
        }
        InputStream stdin = new ByteArrayInputStream(inputBuilder.toString().getBytes());

        List<String> lines = InputReader.readFromInputStream(stdin);

        assertEquals(1000, lines.size());
        assertEquals("Line 0", lines.get(0));
        assertEquals("Line 999", lines.get(999));
    }
}
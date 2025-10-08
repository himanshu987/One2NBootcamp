package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTest {

    @Test
    void testParseStdinMode() {
        String[] args = {"search"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("search", options.getSearchString());
        assertTrue(options.isStdinMode());
        assertNull(options.getInputFile());
        assertFalse(options.hasOutputFile());
        assertNull(options.getOutputFile());
    }

    @Test
    void testParseFileMode() {
        String[] args = {"search", "file.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("search", options.getSearchString());
        assertFalse(options.isStdinMode());
        assertEquals("file.txt", options.getInputFile());
        assertFalse(options.hasOutputFile());
        assertNull(options.getOutputFile());
    }

    @Test
    void testParseFileModeWithOutput() {
        String[] args = {"lorem", "loreipsum.txt", "-o", "out.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("lorem", options.getSearchString());
        assertFalse(options.isStdinMode());
        assertEquals("loreipsum.txt", options.getInputFile());
        assertTrue(options.hasOutputFile());
        assertEquals("out.txt", options.getOutputFile());
    }

    @Test
    void testParseStdinModeWithOutput() {
        String[] args = {"search", "-o", "output.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("search", options.getSearchString());
        assertTrue(options.isStdinMode());
        assertNull(options.getInputFile());
        assertTrue(options.hasOutputFile());
        assertEquals("output.txt", options.getOutputFile());
    }

    @Test
    void testParseOutputFlagAtEnd() {
        String[] args = {"search", "input.txt", "-o", "output.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("search", options.getSearchString());
        assertEquals("input.txt", options.getInputFile());
        assertEquals("output.txt", options.getOutputFile());
    }

    @Test
    void testParseNoArguments() {
        String[] args = {};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentParser.parse(args)
        );

        assertTrue(exception.getMessage().contains("Usage"));
    }

    @Test
    void testParseMissingOutputFilename() {
        String[] args = {"search", "file.txt", "-o"};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentParser.parse(args)
        );

        assertTrue(exception.getMessage().contains("option requires an argument"));
    }

    @Test
    void testParseTooManyInputFiles() {
        String[] args = {"search", "file1.txt", "file2.txt", "-o", "out.txt"};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentParser.parse(args)
        );

        assertTrue(exception.getMessage().contains("too many input files"));
    }

    @Test
    void testParseSearchStringWithSpaces() {
        String[] args = {"search string with spaces", "file.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("search string with spaces", options.getSearchString());
        assertEquals("file.txt", options.getInputFile());
    }

    @Test
    void testParseSpecialCharactersInSearchString() {
        String[] args = {"@#$%^&*()", "file.txt", "-o", "out.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("@#$%^&*()", options.getSearchString());
    }

    @Test
    void testParseFilenameWithPath() {
        String[] args = {"search", "/path/to/file.txt", "-o", "/path/to/output.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("/path/to/file.txt", options.getInputFile());
        assertEquals("/path/to/output.txt", options.getOutputFile());
    }

    @Test
    void testGrepOptionsImmutability() {
        String[] args = {"search", "file.txt", "-o", "out.txt"};
        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("search", options.getSearchString());
        assertEquals("search", options.getSearchString());
        assertEquals("file.txt", options.getInputFile());
        assertEquals("file.txt", options.getInputFile());
        assertEquals("out.txt", options.getOutputFile());
        assertEquals("out.txt", options.getOutputFile());
    }
}
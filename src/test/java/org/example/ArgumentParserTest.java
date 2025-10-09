package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTest {

    @Test
    void testParseCaseInsensitiveStdinMode() {
        String[] args = {"-i", "foo"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("foo", options.getSearchString());
        assertTrue(options.isCaseInsensitive());
        assertTrue(options.isStdinMode());
        assertNull(options.getOutputFile());
    }

    @Test
    void testParseCaseInsensitiveFileMode() {
        String[] args = {"-i", "foo", "file.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("foo", options.getSearchString());
        assertTrue(options.isCaseInsensitive());
        assertFalse(options.isStdinMode());
        assertEquals("file.txt", options.getInputFile());
        assertFalse(options.hasOutputFile());
    }

    @Test
    void testParseCaseInsensitiveWithOutputFile() {
        String[] args = {"-i", "foo", "input.txt", "-o", "output.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("foo", options.getSearchString());
        assertTrue(options.isCaseInsensitive());
        assertEquals("input.txt", options.getInputFile());
        assertEquals("output.txt", options.getOutputFile());
    }

    @Test
    void testParseCaseInsensitiveStdinWithOutputFile() {
        String[] args = {"-i", "foo", "-o", "output.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertEquals("foo", options.getSearchString());
        assertTrue(options.isCaseInsensitive());
        assertTrue(options.isStdinMode());
        assertEquals("output.txt", options.getOutputFile());
    }

    @Test
    void testParseFlagsInDifferentOrders() {
        String[] args1 = {"-i", "search", "file.txt", "-o", "out.txt"};
        GrepOptions opts1 = ArgumentParser.parse(args1);
        assertTrue(opts1.isCaseInsensitive());
        assertEquals("search", opts1.getSearchString());

        String[] args2 = {"-i", "search", "file.txt", "-o", "out.txt"};
        GrepOptions opts2 = ArgumentParser.parse(args2);
        assertEquals("out.txt", opts2.getOutputFile());
    }

    @Test
    void testParseWithoutCaseInsensitiveFlag() {
        String[] args = {"search", "file.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertFalse(options.isCaseInsensitive(), "Should be case-sensitive by default");
    }

    @Test
    void testStory4ExampleParsing() {
        String[] args = {"-i", "foo", "filename.txt", "-o", "outfile.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertTrue(options.isCaseInsensitive());
        assertEquals("foo", options.getSearchString());
        assertEquals("filename.txt", options.getInputFile());
        assertEquals("outfile.txt", options.getOutputFile());
    }

    @Test
    void testAllFlagsCombined() {
        String[] args = {"-i", "lorem", "loreipsum.txt", "-o", "out.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertTrue(options.isCaseInsensitive());
        assertEquals("lorem", options.getSearchString());
        assertEquals("loreipsum.txt", options.getInputFile());
        assertTrue(options.hasOutputFile());
        assertEquals("out.txt", options.getOutputFile());
    }

    @Test
    void testParseMissingSearchString() {
        String[] args = {"-i"};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentParser.parse(args)
        );

        assertTrue(exception.getMessage().contains("search string is required"));
    }

    @Test
    void testParseUnexpectedArgument() {
        String[] args = {"-i", "search", "file1.txt", "file2.txt"};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentParser.parse(args)
        );

        assertTrue(exception.getMessage().contains("unexpected argument"));
    }

    @Test
    void testParseOutputFlagWithoutFilename() {
        String[] args = {"-i", "search", "-o"};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentParser.parse(args)
        );

        assertTrue(exception.getMessage().contains("option requires an argument"));
    }

    @Test
    void testParseCaseInsensitiveWithSpecialCharacters() {
        String[] args = {"-i", "@#$%", "file.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertTrue(options.isCaseInsensitive());
        assertEquals("@#$%", options.getSearchString());
    }

    @Test
    void testParseCaseInsensitiveWithSpacesInSearch() {
        String[] args = {"-i", "search with spaces", "file.txt"};

        GrepOptions options = ArgumentParser.parse(args);

        assertTrue(options.isCaseInsensitive());
        assertEquals("search with spaces", options.getSearchString());
    }

    @Test
    void testBackwardCompatibilityConstructor() {
        GrepOptions options = new GrepOptions("search", "file.txt", "out.txt");

        assertFalse(options.isCaseInsensitive());
        assertEquals("search", options.getSearchString());
    }
}
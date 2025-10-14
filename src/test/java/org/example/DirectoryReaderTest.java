package org.example;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DirectoryReader - recursive file discovery
 */
class DirectoryReaderTest {

    @Test
    void testIsDirectory(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("testdir");
        Files.createDirectory(dir);

        Path file = tempDir.resolve("testfile.txt");
        Files.writeString(file, "content");

        assertTrue(DirectoryReader.isDirectory(dir.toString()));
        assertFalse(DirectoryReader.isDirectory(file.toString()));
        assertFalse(DirectoryReader.isDirectory("nonexistent"));
    }

    @Test
    void testListFilesEmptyDirectory(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("empty");
        Files.createDirectory(dir);

        List<Path> files = DirectoryReader.listFiles(dir.toString());

        assertTrue(files.isEmpty());
    }

    @Test
    void testListFilesSingleFile(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("dir");
        Files.createDirectory(dir);

        Path file = dir.resolve("file.txt");
        Files.writeString(file, "content");

        List<Path> files = DirectoryReader.listFiles(dir.toString());

        assertEquals(1, files.size());
        assertEquals(file, files.get(0));
    }

    @Test
    void testListFilesMultipleFiles(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("dir");
        Files.createDirectory(dir);

        Files.writeString(dir.resolve("file1.txt"), "content1");
        Files.writeString(dir.resolve("file2.txt"), "content2");
        Files.writeString(dir.resolve("file3.txt"), "content3");

        List<Path> files = DirectoryReader.listFiles(dir.toString());

        assertEquals(3, files.size());
    }

    @Test
    void testListFilesRecursivelyEmptyDirectory(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("empty");
        Files.createDirectory(dir);

        List<Path> files = DirectoryReader.listFilesRecursively(dir.toString());

        assertTrue(files.isEmpty());
    }

    @Test
    void testListFilesRecursivelySingleLevel(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("tests");
        Files.createDirectory(dir);

        Path file1 = dir.resolve("test1.txt");
        Path file2 = dir.resolve("test2.txt");
        Files.writeString(file1, "content1");
        Files.writeString(file2, "content2");

        List<Path> files = DirectoryReader.listFilesRecursively(dir.toString());

        assertEquals(2, files.size());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
    }

    @Test
    void testListFilesRecursivelyNestedDirectories(@TempDir Path tempDir) throws IOException {
        // Create structure: tests/test1.txt, tests/inner/test2.txt
        Path testsDir = tempDir.resolve("tests");
        Files.createDirectory(testsDir);

        Path file1 = testsDir.resolve("test1.txt");
        Files.writeString(file1, "content1");

        Path innerDir = testsDir.resolve("inner");
        Files.createDirectory(innerDir);

        Path file2 = innerDir.resolve("test2.txt");
        Files.writeString(file2, "content2");

        List<Path> files = DirectoryReader.listFilesRecursively(testsDir.toString());

        assertEquals(2, files.size());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
    }

    @Test
    void testListFilesRecursivelyDeeplyNested(@TempDir Path tempDir) throws IOException {
        Path level1 = tempDir.resolve("level1");
        Files.createDirectory(level1);
        Files.writeString(level1.resolve("file1.txt"), "content");

        Path level2 = level1.resolve("level2");
        Files.createDirectory(level2);
        Files.writeString(level2.resolve("file2.txt"), "content");

        Path level3 = level2.resolve("level3");
        Files.createDirectory(level3);
        Files.writeString(level3.resolve("file3.txt"), "content");

        List<Path> files = DirectoryReader.listFilesRecursively(level1.toString());

        assertEquals(3, files.size());
    }

    @Test
    void testListFilesRecursivelyIgnoresSubdirectories(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("parent");
        Files.createDirectory(dir);

        Files.writeString(dir.resolve("file.txt"), "content");
        Files.createDirectory(dir.resolve("subdir"));

        List<Path> files = DirectoryReader.listFilesRecursively(dir.toString());

        // Should only include the file, not the subdirectory itself
        assertEquals(1, files.size());
        assertTrue(files.get(0).toString().endsWith("file.txt"));
    }

    @Test
    void testListFilesRecursivelySortedOutput(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("dir");
        Files.createDirectory(dir);

        Files.writeString(dir.resolve("c.txt"), "content");
        Files.writeString(dir.resolve("a.txt"), "content");
        Files.writeString(dir.resolve("b.txt"), "content");

        List<Path> files = DirectoryReader.listFilesRecursively(dir.toString());

        // Files should be sorted
        assertTrue(files.get(0).toString().endsWith("a.txt"));
        assertTrue(files.get(1).toString().endsWith("b.txt"));
        assertTrue(files.get(2).toString().endsWith("c.txt"));
    }

    @Test
    void testListFilesRecursivelyNonExistentDirectory() {
        IOException exception = assertThrows(IOException.class, () -> {
            DirectoryReader.listFilesRecursively("nonexistent");
        });

        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    void testListFilesRecursivelyNotADirectory(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "content");

        IOException exception = assertThrows(IOException.class, () -> {
            DirectoryReader.listFilesRecursively(file.toString());
        });

        assertTrue(exception.getMessage().contains("Not a directory"));
    }

    @Test
    void testListFilesNonDirectory(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "content");

        IOException exception = assertThrows(IOException.class, () -> {
            DirectoryReader.listFiles(file.toString());
        });

        assertTrue(exception.getMessage().contains("Not a directory"));
    }
}
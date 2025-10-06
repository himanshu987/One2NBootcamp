package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlingTest {

    @Test
    void testFileDoesNotExist() {
        GrepException exception = assertThrows(GrepException.class, () -> {
            Main.readFile("foo.txt");
        });

        assertTrue(exception.getMessage().contains("foo.txt"));
        assertTrue(exception.getMessage().contains("No such file or directory"));
    }

    @Test
    void testFileIsDirectory(@TempDir Path tempDir) throws IOException {
        Path dir = tempDir.resolve("bar");
        Files.createDirectory(dir);

        GrepException exception = assertThrows(GrepException.class, () -> {
            Main.readFile(dir.toString());
        });

        assertTrue(exception.getMessage().contains("bar"));
        assertTrue(exception.getMessage().contains("Is a directory"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testFileNoReadPermission(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("protected_file.txt");
        Files.writeString(file, "content");

        Set<PosixFilePermission> emptyPerms = new HashSet<>();
        Files.setPosixFilePermissions(file, emptyPerms);

        try {
            GrepException exception = assertThrows(GrepException.class, () -> {
                Main.readFile(file.toString());
            });

            assertTrue(exception.getMessage().contains("protected_file.txt"));
            assertTrue(exception.getMessage().contains("Permission denied"));
        } finally {
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(file, perms);
        }
    }

    @Test
    void testValidFileRead(@TempDir Path tempDir) throws IOException, GrepException {
        Path file = tempDir.resolve("valid.txt");
        Files.writeString(file, "Line 1\nLine 2");

        assertDoesNotThrow(() -> Main.readFile(file.toString()));

        var lines = Main.readFile(file.toString());
        assertEquals(2, lines.size());
    }

    @Test
    void testErrorMessageFormat() {
        GrepException exception = assertThrows(GrepException.class, () -> {
            Main.readFile("missing.txt");
        });

        String expectedPattern = "^\\./mygrep: missing\\.txt: .*";
        assertTrue(exception.getMessage().matches(expectedPattern),
                "Error message should start with './mygrep: filename:'");
    }
}
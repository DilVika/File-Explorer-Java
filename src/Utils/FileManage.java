package Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManage {
    /// Receive a path to target file (include the name of the file you want to create)
    // and a boolean to show if it is a directory
    /// Return true if the file was created successfully
    /// Return false if failed due to file existed
    /// Throw IOException if IO errors occur, SecurityException if access denied
    public static boolean create(String path, boolean isDir) throws IOException, SecurityException {
        var fPath = Paths.get(path);
        var f = fPath.toFile();
        if (isDir) {
            return f.mkdir();
        }
        return f.createNewFile();
    }

    /// Receive a path to target file
    /// Return true if the file was created successfully
    /// Return false if failed due to file existed
    /// Throw SecurityException if access denied
    public static boolean delete(String path) throws SecurityException {
        var fPath = Paths.get(path);
        var f = fPath.toFile();

        return deleteDirectory(f);
    }

    private static boolean deleteDirectory(File target) {
        File[] allContents = target.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return target.delete();
    }

    /// Receive a src path and a des path
    /// Throw IOException if IO errors occur, SecurityException if access denied
    public static void rename(String path, String newPath) throws SecurityException, IOException {
        var fPathSrc = Paths.get(path);
        var fPathDes = Paths.get(newPath);

        Files.move(fPathSrc, fPathDes);
    }

    /// Receive a path to open
    /// Throws IOException if IO errors occur
    public static void openFile(String path) throws IOException {
        var fPath = Paths.get(path);
        var f = fPath.toFile();
        if (f.isDirectory()) {
            return;
        }

        var desktop = Desktop.getDesktop();
        desktop.open(f);
    }
}

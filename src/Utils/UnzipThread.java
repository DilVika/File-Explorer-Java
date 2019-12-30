package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class UnzipThread extends Thread {
    Path src;
    Path desDir;
    Runnable onDone;
    Runnable onCancel;
    Consumer<Integer> onProgressIncrease;
    Consumer<Integer> maxProgressSet;

    public void cancel() {
        isCancel = true;
    }

    boolean isCancel;

    public UnzipThread(String src, String desDir, Runnable onDone, Runnable onCancel, Consumer<Integer> onProgressIncrease, Consumer<Integer> maxProgressSet) {
        this.src = Paths.get(src);
        this.desDir = Paths.get(desDir);
        this.onDone = onDone;
        this.onCancel = onCancel;
        this.onProgressIncrease = onProgressIncrease;
        this.maxProgressSet = maxProgressSet;
    }

    @Override
    public void run() {
        if (!desDir.toFile().exists()) {
            var mkdirRes = desDir.toFile().mkdir();
        }
        if (!desDir.toFile().isDirectory()) {
            return;
        }

        try {
            var zf = new ZipFile(src.toFile());
            maxProgressSet.accept(zf.size());
            zf.close();

            ZipInputStream zis = new ZipInputStream(new FileInputStream(src.toFile()));

            byte[] buffer = new byte[2048];
            var entry = zis.getNextEntry();

            while (!isCancel && (entry != null)) {
                var f = newFile(desDir.toFile(), entry);
                var os = new FileOutputStream(f);

                int l;
                int progress = 0;
                while (!isCancel && (l = zis.read(buffer)) > 0) {
                    os.write(buffer, 0, l);
                    final var fProgress = ++progress;
                    onProgressIncrease.accept(fProgress);
                }

                os.close();
                entry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            if (isCancel) {
                onCancel.run();
            } else {
                onDone.run();
            }
        } catch (Exception ignored) {
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}

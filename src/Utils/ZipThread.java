package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipThread extends Thread {
    Path srcDir;
    Path des;
    Runnable onDone;
    Runnable onCancel;
    Consumer<Integer> onProgressIncrease;
    Consumer<Integer> maxProgressSet;

    public void cancel() {
        isCancel = true;
    }

    boolean isCancel;

    public ZipThread(String srcDir, String des, Runnable onDone, Runnable onCancel, Consumer<Integer> onProgressIncrease, Consumer<Integer> maxProgressSet) {
        this.srcDir = Paths.get(srcDir);
        this.des = Paths.get(des);
        this.onDone = onDone;
        this.onCancel = onCancel;
        this.onProgressIncrease = onProgressIncrease;
        this.maxProgressSet = maxProgressSet;
    }

    @Override
    public void run() {
        File[] files = srcDir.toFile().listFiles();

        try {
            var createRes = des.toFile().createNewFile();
            if (!createRes) {
                return;
            }

            if (files == null) {
                return;
            }

            var totalLength = Arrays.stream(files).map(File::length).reduce(0L, Long::sum);
            maxProgressSet.accept((int) (totalLength / 2048));

            var os = new FileOutputStream(des.toFile());
            var zos = new ZipOutputStream(os);
            byte[] buffer = new byte[2048];

            for (var file : files) {
                if (!isCancel) {
                    var entry = new ZipEntry(file.getName());
                    zos.putNextEntry(entry);

                    var is = new FileInputStream(file);
                    int l = 0;
                    int progress = 0;
                    while (!isCancel && ((l = is.read(buffer)) > 0)) {
                        zos.write(buffer, 0, l);
                        final var fProgress = ++progress;
                        onProgressIncrease.accept(fProgress);
                    }
                    is.close();
                }
            }

            zos.finish();
            zos.close();

            if (isCancel) {
                onCancel.run();
            } else {
                onDone.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

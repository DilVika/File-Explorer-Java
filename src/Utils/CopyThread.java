package Utils;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class CopyThread extends Thread {
    Path src;
    Path des;
    Runnable onDone;
    Runnable onCancel;
    Consumer<Integer> onProgressIncrease;
    Consumer<Integer> maxProgressSet;

    boolean isCancel = false;

    public CopyThread(String src, String des, Runnable onDone, Runnable onCancel, Consumer<Integer> onProgressIncrease, Consumer<Integer> maxProgressSet) {
        this.src = Paths.get(src);
        this.des = Paths.get(des);
        this.onDone = onDone;
        this.onCancel = onCancel;
        this.onProgressIncrease = onProgressIncrease;
        this.maxProgressSet = maxProgressSet;
    }


    public void cancel() {
        isCancel = true;
    }

    @Override
    public void run() {
        try {
            var fSrc = src.toFile();
            if (!fSrc.exists()) {
                return;
            }

            var fDes = des.toFile();
            var createRes = fDes.createNewFile();
            if (!createRes) {
                return;
            }

            var is = new FileInputStream(fSrc);
            var os = new FileOutputStream(fDes);
            byte[] buffer = new byte[2048];
            SwingUtilities.invokeLater(() -> maxProgressSet.accept((int) fSrc.length() / 2048));

            int progress = 0;
            int l;
            while (!isCancel && ((l = is.read(buffer)) > 0)) {
                os.write(buffer, 0, l);
                int finalProgress = ++progress;
                SwingUtilities.invokeLater(() -> onProgressIncrease.accept(finalProgress));
            }

            is.close();
            os.close();

            if (isCancel)
            {
                onCancel.run();
            } else {
                onDone.run();
            }
        } catch (Exception ignored) {

        }
    }
}

package mainWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Utils.*;
import tree.design.DesignTree;
import tree.model.ComputerDirectoryTreeModel;

public class FileExplorer {

    private JFrame frame;
    private JTextField findField;
    private JList filesView;
    private DefaultListModel<String> filesList;
    private DesignTree tree;
    private JProgressBar progressBar;
    private JButton cancelBtn;
    private JPanel progressbarPane;

    private JCheckBoxMenuItem contendCheckbx;
    private boolean isViewingHistory = false;
    private JCheckBoxMenuItem iconCheckbox;

    private String copyPath = null;
    private boolean isBusy = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FileExplorer window = new FileExplorer();
                window.frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    FileExplorer() {
        initialize();
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean showYesNoDialog(String message) {
        return JOptionPane.showConfirmDialog(frame, message, "Confirm", JOptionPane.YES_NO_OPTION) == 0;
    }

    private String showInputDialog(String message) {
        return JOptionPane.showInputDialog(message);
    }

    public String getCurrentSelectedDir(boolean isShowDialog) {
        var selected = GlobalMap.store.get(GlobalMap.SELECTED_DIR);
        if (selected == null) {
            if (isShowDialog)
                showErrorDialog("Please select a folder!");
            return null;
        }
        return (String) selected;
    }

    public String getCurrentSelectedFileName(boolean isShowDialog) {
        var selectedIndex = filesView.getSelectedIndex();
        if (selectedIndex < 0) {
            if (isShowDialog)
                showErrorDialog("Please select a file");
            return null;
        }
        return filesList.get(selectedIndex);
    }

    public String getFullPathToFile(String dir, String filePath) {
        if (!dir.substring(dir.length() - 1).equals("\\")) {
            return dir + '\\' + filePath;
        }
        return dir + filePath;
    }

    private void initialize() {

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setBounds(100, 100, 755, 638);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 600));
        frame.setPreferredSize(frame.getMinimumSize());
        frame.setMaximumSize(new Dimension(800, 600));
        frame.setTitle("File Explorer");

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu createMenu = new JMenu("CREATE");
        menuBar.add(createMenu);

        JMenuItem fileItem = new JMenuItem("File");
        fileItem.addActionListener((e) -> {
            var selected = getCurrentSelectedDir(true);
            var name = showInputDialog("Enter file name: ");
            var dirPath = getFullPathToFile(selected, name);
            boolean res = false;
            try {
                res = FileManage.create(dirPath, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (res) {
                showSuccessDialog("Create success");
            } else {
                showErrorDialog("Create failed");
            }
            updateFileList(selected);
        });
        createMenu.add(fileItem);
        // fileItem.addActionListener((e) -> {
        // if (isViewingHistory) {
        // isViewingHistory = false;
        // iconList.setModel(icList);
        // } else {
        // isViewingHistory = true;
        // iconList.setModel(historyList);
        // }
        // });

        JMenuItem folderItem = new JMenuItem("Folder");
        folderItem.addActionListener((e) -> {
            var selected = getCurrentSelectedDir(true);
            var name = showInputDialog("Enter directory name: ");
            var dirPath = getFullPathToFile(selected, name);
            boolean res = false;
            try {
                res = FileManage.create(dirPath, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (res) {
                showSuccessDialog("Create success");
            } else {
                showErrorDialog("Create failed");
            }
            tree.updateUI();
        });
        createMenu.add(folderItem);
        // saveAllItem.addActionListener((e) -> {
        // FileManager.writerFile(Collections.list(icList.elements()));
        // });

        JMenu editMenu = new JMenu("EDIT");
        menuBar.add(editMenu);

        JMenuItem copyFileItem = new JMenuItem("Copy");
        copyFileItem.addActionListener((e) -> {
            var selectedDir = getCurrentSelectedDir(true);
            var selectedFile = getCurrentSelectedFileName(true);

            if (selectedDir == null || selectedFile == null) {
                return;
            }

            copyPath = getFullPathToFile(selectedDir, selectedFile);
        });
        editMenu.add(copyFileItem);

        JMenuItem pasteFileItem = new JMenuItem("Paste");
        pasteFileItem.addActionListener((e) -> {
            if (isBusy) {
                showErrorDialog("Busying, please try again later");
                return;
            }
            var selectedDir = getCurrentSelectedDir(true);

            if (selectedDir == null) {
                return;
            }

            var targetPath = getFullPathToFile(selectedDir, Paths.get(copyPath).toFile().getName());
            progressbarPane.setVisible(true);
            var copier = new CopyThread(copyPath, targetPath, () -> {
                showSuccessDialog("Copy finished");
                progressbarPane.setVisible(false);
                progressBar.setValue(0);
                isBusy = false;
            }, () -> {
                showErrorDialog("Canceled");
                progressbarPane.setVisible(false);
                FileManage.delete(targetPath);
                progressBar.setValue(0);
                isBusy = false;
            }, progressBar::setValue, progressBar::setMaximum);
            isBusy = true;
            for (var l : cancelBtn.getActionListeners()) {
                cancelBtn.removeActionListener(l);
            }
            cancelBtn.addActionListener((cancelEvent) -> copier.cancel());
            copier.start();
        });
        editMenu.add(pasteFileItem);

        JMenuItem renameFileItem = new JMenuItem("Rename File");
        renameFileItem.addActionListener((e) -> {
            var selectedDir = getCurrentSelectedDir(true);
            var selectedFile = getCurrentSelectedFileName(true);


            if (selectedDir == null || selectedFile == null) {
                return;
            }

            var newName = showInputDialog("Enter new name: ");
            if (newName == null || newName.equals("")) {
                return;
            }

            var selected = getFullPathToFile(selectedDir, selectedFile);

            var newNamePath = getFullPathToFile(selectedDir, newName);
            try {
                FileManage.rename(selected, newNamePath);
            } catch (IOException ignored) {
                showErrorDialog("Rename failed");
                return;
            }
            showSuccessDialog("Rename success");
            updateFileList(selectedDir);
        });
        editMenu.add(renameFileItem);
        // addItem.addActionListener((e) -> {
        // var d = new AddIconDialog();
        // var newEmo = new Emotion();
        // d.showDialog(newEmo);
        // if (!newEmo.icon.equals("")) {
        // icList.addElement(newEmo);
        // }
        // });

        JMenuItem renameFolderItem = new JMenuItem("Rename Folder");
        renameFolderItem.addActionListener((e) -> {
            var selected = getCurrentSelectedDir(true);

            if (selected == null) {
                return;
            }

            var newName = showInputDialog("Enter new name: ");
            if (newName == null || newName.equals("")) {
                return;
            }

            var selectedParent = Paths.get(selected).getParent();
            var newNamePath = getFullPathToFile(selectedParent.toString(), newName);
            try {
                FileManage.rename(selected, newNamePath);
            } catch (IOException ignored) {
                showErrorDialog("Rename failed");
                return;
            }
            showSuccessDialog("Rename success");
            tree.updateUI();
        });
        editMenu.add(renameFolderItem);

        JMenuItem deleteItem = new JMenuItem("Delete Files");
        deleteItem.addActionListener((e) -> {
            var selected = getCurrentSelectedDir(true);
            var selectedFile = getCurrentSelectedFileName(true);
            if (selected == null || selectedFile == null) {
                return;
            }
            var filePath = getFullPathToFile(selected, selectedFile);
            var confirm = showYesNoDialog("Are you sure?");
            if (!confirm) {
                return;
            }
            FileManage.delete(filePath);
            updateFileList(selected);
        });
        editMenu.add(deleteItem);
        // removeItem.addActionListener((e) -> {
        // var selected = iconList.getSelectedValue();
        // if (selected == null) {
        // return;
        // }
        // icList.removeElement(selected);
        // });

        JMenuItem deleteFolderItem = new JMenuItem("Delete Folder");
        deleteFolderItem.addActionListener((e) -> {
            var selected = getCurrentSelectedDir(true);
            if (selected == null) {
                return;
            }
            var confirm = showYesNoDialog("Are you sure?");
            if (!confirm) {
                return;
            }
            FileManage.delete(selected);
            tree.updateUI();
        });
        editMenu.add(deleteFolderItem);
        // removeItem.addActionListener((e) -> {
        // var selected = iconList.getSelectedValue();
        // if (selected == null) {
        // return;
        // }
        // icList.removeElement(selected);
        // });

        JMenuItem zipItem = new JMenuItem("Compress to zip");
        zipItem.addActionListener((e) -> {
            if (isBusy) {
                showErrorDialog("Busying, please try again later");
                return;
            }
            var selected = getCurrentSelectedDir(true);
            if (selected == null) {
                return;
            }
            if (Paths.get(selected).toString().equals(Paths.get(selected).getRoot().toString())) {
                return; // cannot compress a drive
            }
            var name = showInputDialog("Enter zip name");
            if (name == null || name.equals("")) {
                return;
            }

            var zipPath = getFullPathToFile(Paths.get(selected).getParent().toString(), name);
            progressbarPane.setVisible(true);
            var ziper = new ZipThread(selected, zipPath, () -> {
                showSuccessDialog("Compress finished");
                progressbarPane.setVisible(false);
                progressBar.setValue(0);
                isBusy = false;
            }, () -> {
                showErrorDialog("Canceled");
                progressbarPane.setVisible(false);
                FileManage.delete(zipPath);
                progressBar.setValue(0);
                isBusy = false;
            }, progressBar::setValue, progressBar::setMaximum);
            for (var l : cancelBtn.getActionListeners()) {
                cancelBtn.removeActionListener(l);
            }
            cancelBtn.addActionListener((cancelEvent) -> ziper.cancel());
            isBusy = true;
            ziper.start();
        });
        editMenu.add(zipItem);
        // editItem.addActionListener((e) -> {
        // var d = new AddIconDialog();
        // var selected = (Emotion) iconList.getSelectedValue();
        // if (selected == null) {
        // return;
        // }
        // d.type = AddIconDialog.Type.Edit;
        // d.showDialog(selected);
        // if (!selected.icon.equals("")) {
        // icList.addElement(selected);
        // iconList.clearSelection();
        // }
        // });

        JMenuItem unzipItem = new JMenuItem("Unzip");
        unzipItem.addActionListener((e) -> {
            if (isBusy) {
                showErrorDialog("Busying, please try again later");
                return;
            }
            var selectedDir = getCurrentSelectedDir(true);
            var selectedFile = getCurrentSelectedFileName(true);

            if (selectedDir == null || selectedFile == null) {
                return;
            }

            var unzipTarget = getFullPathToFile(selectedDir, selectedFile);
            if (!isArchive(Paths.get(unzipTarget).toFile())) {
                showErrorDialog("Not archive");
                return;
            }

            progressbarPane.setVisible(true);

            var unzipper = new UnzipThread(unzipTarget, selectedDir, () -> {
                showSuccessDialog("Uncompress finished");
                progressbarPane.setVisible(false);
                progressBar.setValue(0);
                isBusy = false;
            }, ()-> {
                showErrorDialog("Canceled");
                progressbarPane.setVisible(false);
                progressBar.setValue(0);
                isBusy = false;
            }, progressBar::setValue, progressBar::setMaximum);
            for (var l : cancelBtn.getActionListeners()) {
                cancelBtn.removeActionListener(l);
            }
            cancelBtn.addActionListener((cancelEvent) -> unzipper.cancel());
            isBusy = true;
            unzipper.start();
        });
        editMenu.add(unzipItem);
        // renameItem.addActionListener((e) -> {
        // var d = new AddIconDialog();
        // var selected = (Emotion) iconList.getSelectedValue();
        // if (selected == null) {
        // return;
        // }
        // d.type = AddIconDialog.Type.Rename;
        // d.showDialog(selected);
        // if (!selected.icon.equals("")) {
        // icList.addElement(selected);
        // iconList.clearSelection();
        // }
        // });
//
//        JMenu findMenu = new JMenu("FIND");
//        menuBar.add(findMenu);
//
//        ButtonGroup btnGr = new ButtonGroup();
//        contendCheckbx = new JCheckBoxMenuItem("Contend");
//        btnGr.add(contendCheckbx);
//        findMenu.add(contendCheckbx);
//        contendCheckbx.setState(true);
//
//        iconCheckbox = new JCheckBoxMenuItem("Icon");
//        btnGr.add(iconCheckbox);
//        findMenu.add(iconCheckbox);

        JMenu helpMenu = new JMenu("HELP");
        menuBar.add(helpMenu);

        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        aboutItem.addActionListener((e) -> {
            var d = new AboutDialog();
            d.showDialog();
        });

        frame.getContentPane().setLayout(new GridBagLayout());
        var gridbagConstraint = new GridBagConstraints();
        gridbagConstraint.gridx = 0;
        gridbagConstraint.gridy = 0;
        gridbagConstraint.weightx = 2;
        gridbagConstraint.weighty = 0;

        gridbagConstraint.fill = GridBagConstraints.BOTH;

        progressbarPane = new JPanel();
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        cancelBtn = new JButton("Cancel");
        cancelBtn.setEnabled(true);
        progressbarPane.add(progressBar);
        progressbarPane.add(cancelBtn);
        frame.getContentPane().add(progressbarPane, gridbagConstraint);
        progressbarPane.setVisible(false);

        tree = new DesignTree(new ComputerDirectoryTreeModel());
        var scroll = new JScrollPane(tree);

        gridbagConstraint.gridy = 1;
        gridbagConstraint.weighty = 10;
        frame.getContentPane().add(scroll, gridbagConstraint);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateFileList(getCurrentSelectedDir(false));
            }
        });

        gridbagConstraint.gridx = 1;
        gridbagConstraint.weightx = 3;
        gridbagConstraint.weighty = 10;
        gridbagConstraint.gridy = 1;
        var contentPanel = new JPanel();

        frame.getContentPane().add(contentPanel, gridbagConstraint);

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel contentLabel = new JLabel("File");
        contentLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        gridbagConstraint.gridx = 1;
        gridbagConstraint.gridy = 0;
        gridbagConstraint.weightx = 3;
        gridbagConstraint.weighty = 0;
        frame.getContentPane().add(contentLabel, gridbagConstraint);
//        contentPanel.add(contentLabel);


        filesList = new DefaultListModel<String>();

        filesView = new JList(filesList);
        filesView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    var currentSelectedFile = getFullPathToFile(getCurrentSelectedDir(false),
                            getCurrentSelectedFileName(false));
                    try {
                        FileManage.openFile(currentSelectedFile);
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        var scrollP = new JScrollPane(filesView);
        contentPanel.add(scrollP);

        tree.updateUI();
    }

    private static boolean isArchive(File f) {
        int fileSignature = 0;
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            fileSignature = raf.readInt();
        } catch (IOException e) {
            // handle if you like
        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

    private void updateFileList(String curPath) {
        if (curPath == null) {
            filesList.clear();
            return;
        }
        try (Stream<Path> walk = Files.walk(Paths.get(curPath), 1)) {
            var files = walk
                    .filter(Files::isRegularFile)
                    .map(f -> f.getFileName()
                            .toString())
                    .collect(Collectors.toList());
            filesList.clear();
            filesList.addAll(files);
        } catch (Exception ignored) {
        }
    }
}

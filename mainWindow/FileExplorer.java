package mainWindow;

import java.awt.*;
import javax.swing.*;

import tree.JFileTreeIE;

public class FileExplorer {

    private JFrame frame;
    private JTextField findField;
    private JList iconList;

    private JTextPane resultTextPane;
    private JCheckBoxMenuItem contendCheckbx;
    private boolean isViewingHistory = false;
    private JCheckBoxMenuItem iconCheckbox;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
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

    private void initialize() {

        frame = new JFrame();

        frame.setBounds(100, 100, 755, 638);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 600));
        frame.setPreferredSize(frame.getMinimumSize());
        frame.setMaximumSize(new Dimension(800, 600));
        frame.setTitle("File Explorer");

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("CREATE");
        menuBar.add(fileMenu);

        JMenuItem historyItem = new JMenuItem("File");
        fileMenu.add(historyItem);
        // historyItem.addActionListener((e) -> {
        // if (isViewingHistory) {
        // isViewingHistory = false;
        // iconList.setModel(icList);
        // } else {
        // isViewingHistory = true;
        // iconList.setModel(historyList);
        // }
        // });

        JMenuItem saveAllItem = new JMenuItem("Folder");
        fileMenu.add(saveAllItem);
        // saveAllItem.addActionListener((e) -> {
        // FileManager.writerFile(Collections.list(icList.elements()));
        // });

        JMenu editMenu = new JMenu("EDIT");
        menuBar.add(editMenu);

        JMenuItem addItem = new JMenuItem("Rename");
        editMenu.add(addItem);
        // addItem.addActionListener((e) -> {
        // var d = new AddIconDialog();
        // var newEmo = new Emotion();
        // d.showDialog(newEmo);
        // if (!newEmo.icon.equals("")) {
        // icList.addElement(newEmo);
        // }
        // });

        JMenuItem removeItem = new JMenuItem("Delete");
        editMenu.add(removeItem);
        // removeItem.addActionListener((e) -> {
        // var selected = iconList.getSelectedValue();
        // if (selected == null) {
        // return;
        // }
        // icList.removeElement(selected);
        // });

        JMenuItem editItem = new JMenuItem("Compress to zip");
        editMenu.add(editItem);
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

        JMenuItem renameItem = new JMenuItem("Unzip");
        editMenu.add(renameItem);
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

        JMenu findMenu = new JMenu("FIND");
        menuBar.add(findMenu);

        ButtonGroup btnGr = new ButtonGroup();
        contendCheckbx = new JCheckBoxMenuItem("Contend");
        btnGr.add(contendCheckbx);
        findMenu.add(contendCheckbx);
        contendCheckbx.setState(true);

        iconCheckbox = new JCheckBoxMenuItem("Icon");
        btnGr.add(iconCheckbox);
        findMenu.add(iconCheckbox);

        JMenu helpMenu = new JMenu("HELP");
        menuBar.add(helpMenu);

        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        // aboutItem.addActionListener((e) -> {
        // var d = new AboutDialog();
        // d.showDialog();
        // });
        //
        frame.getContentPane().setLayout(new GridBagLayout());
        var gridbagConstraint = new GridBagConstraints();
        gridbagConstraint.gridx = 0;
        gridbagConstraint.gridy = 0;
        gridbagConstraint.weightx = 0;
        gridbagConstraint.weighty = 1;

        gridbagConstraint.fill = GridBagConstraints.VERTICAL;

        var leftPanel = new JPanel();
        frame.getContentPane().add(leftPanel, gridbagConstraint);

        gridbagConstraint.gridx = 1;
        gridbagConstraint.weightx = 1;
        var rightPanel = new JPanel();
        frame.getContentPane().add(rightPanel, gridbagConstraint);

        

        JFileTreeIE fileTree = new JFileTreeIE();
        leftPanel.add(fileTree);
 
         
        
       
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel meaningLabel = new JLabel("Meaning");
        meaningLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        rightPanel.add(meaningLabel);

        resultTextPane = new JTextPane();
        resultTextPane.setEditable(false);
        resultTextPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultTextPane.setContentType("fdfdfddff");
        resultTextPane.setBorder(BorderFactory.createLineBorder(Color.black));
        rightPanel.add(resultTextPane);

       
    }
}

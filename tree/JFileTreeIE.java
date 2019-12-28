package tree;

import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tree.design.DesignTree;
import tree.model.ComputerDirectoryTreeModel;

/*
 * Short implementation class to show the program
 */

@SuppressWarnings("serial")
public class JFileTreeIE extends JFrame {
    private JFileTreeIE() {
        super("Directory Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout());
        createPanel();
        setSize(500, 800);
        setVisible(true);
    }

    private void createPanel() {
        DesignTree tree = new DesignTree(new ComputerDirectoryTreeModel());
        JScrollPane scroll = new JScrollPane(tree);
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(scroll);
        getContentPane().add(panel);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new JFileTreeIE());
    }
}
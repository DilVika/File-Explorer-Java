package tree.render;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/*
 * This class handles the rendering of the tree nodes.
 * This includes handling rollover functions, and naming conventions.
 */

@SuppressWarnings("serial")
public class FileNameRenderer extends DefaultTreeCellRenderer {
    public FileNameRenderer() {
        setBackgroundSelectionColor(new Color(205, 232, 255));
        setHoverSelectionColor(new Color(229, 243, 255));
    }

    private boolean mOver = false;
    private boolean selected = false;
    private Color rolloverColor = null;

    public void setHoverSelectionColor(Color rollover) {
        rolloverColor = rollover;
    }

    public Color getHoverSelectionColor() {
        return rolloverColor;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus, false);
    }

    //Replaces the node text from the path of the directory to just the directory name
    //Also update the what node is selected and whether the mouse is over it for other
    //functions in this class
    //Sets some of the icons of the nodes on the tree. Ie if the directory is expanded
    //the icon will change
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus, boolean mouseOver) {
        File holder = null;
        if(value instanceof File) {
            holder = (File) value;
            if(!Arrays.asList(File.listRoots()).contains((File) value)) {
                value = ((File) value).getName();
            }
        }

        JComponent c = (JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        selected = sel;
        mOver = mouseOver;

        if(c instanceof JLabel)
            ((JLabel) c).setText("<html><p style=\"padding: 3px 0;\">" + value + "<p/>");

        if(value instanceof DefaultMutableTreeNode)
            setIcon(new ImageIcon("resources/folder.png"));
        else if(expanded && holder != null && hasDirectory(holder))
            setIcon(new ImageIcon("resources/folder.png"));
        else
            setIcon(new ImageIcon("resources/folder.png"));

        return c;
    }

    //Checks whether the directory has any sub directories.
    //This determines if a handle will be painted next to
    //the node
    private boolean hasDirectory(File path) {
        for(File temp : path.listFiles()) {
            if(temp.isDirectory() && !temp.isHidden())
                if(temp.listFiles() != null)
                    return true;
        }

        return false;
    }

    //Paints the node
    //Changes background colour of the node if the mouse is over it
    //or if it is selected
    //Offsets paint by so much so that the node works with the icons
    @Override
    public void paintComponent(Graphics g) {
        Color bColor;

        if (selected)
            bColor = getBackgroundSelectionColor();
        else if(mOver)
            bColor = getHoverSelectionColor();
        else {
            bColor = getBackgroundNonSelectionColor();
            if (bColor == null)
                bColor = getBackground();
        }

        int imageOffset = -1;
        if (bColor != null) {
            imageOffset = getLabelStart();
            g.setColor(bColor);
            if(getComponentOrientation().isLeftToRight())
                g.fillRect(imageOffset, 0, getWidth() - imageOffset, getHeight());
            else
                g.fillRect(0, 0, getWidth() - imageOffset, getHeight());
        }

        super.paintComponent(g);
    }

    //Used for getting the image offset for the paintComponent function
    private int getLabelStart() {
        Icon currentI = getIcon();
        if(currentI != null && getText() != null)
            return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
        return 0;
    }
}
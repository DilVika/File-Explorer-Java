package tree.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.SwingUtilities;

import tree.ui.TreeHandleUI;

/*
 * Class which controls expand / collapse handle animations
 * and other mouse related functions
 */

public class MouseHandler extends MouseAdapter {
    JTree t = null;
    TreeHandleUI tUI = null;

    public MouseHandler(JTree tree) {
        t = tree;
        tUI = (TreeHandleUI) tree.getUI();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        tUI.fadeHandles(true, 300); //Fades handles in
    }

    @Override
    public void mouseExited(MouseEvent e) {
        tUI.fadeHandles(false, 300); //Fades handles out
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Clears selection when mouse is pressed outside of on of the nodes
        if((SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isRightMouseButton(e))
                && t.getRowForLocation(e.getX(), e.getY()) == -1)
            t.clearSelection();
    }
}
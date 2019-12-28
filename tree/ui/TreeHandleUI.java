package tree.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import tree.icon.AlphaImageIcon;
import tree.model.ComputerDirectoryTreeModel;
import tree.render.FileNameRenderer;

/*
 * This mainly handles the look and feel of the JTree
 * It effects areas such as the expand controls of nodes,
 * the way the nodes look, and how lines are drawn on the
 * tree if they are drawn at all
 */

public class TreeHandleUI extends BasicTreeUI {
    ///Variables
    private JTree t = null;
    private boolean lines = false;
    private boolean lineTypeDashed = true;
    private Icon rolloverIcon = null;
    private boolean iconRolloverEnabled = false;
    private ArrayList<Timer> timers = new ArrayList<Timer>(); //For preventing any nasty glitching if fade method is still running
    private UpdateHandler uH = null;
    private ComputerDirectoryTreeModel tM; //Used so that we can quickly check if the file contains any children
    private boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    private int count = 0;
    private float val = 0F;

    ///Constructors
    //Sets most of the icons for the JTree and changes the default mouse handler
    public TreeHandleUI(JTree tree) {
        t = tree;
        uH = new UpdateHandler(t);
        t.addMouseMotionListener(uH);

        EventQueue.invokeLater(() -> tM = (ComputerDirectoryTreeModel) treeModel); //Is like this so that tM does not return an NPE
        EventQueue.invokeLater(() -> setCollapsedIcon(new AlphaImageIcon(new ImageIcon("resources/closed.png"), 0F)));
        EventQueue.invokeLater(() -> setExpandedIcon(new AlphaImageIcon(new ImageIcon("resources/open.png"), 0F)));
        setRolloverIcon(new AlphaImageIcon(new ImageIcon("resources/rollover.png") , 1F));
        removeLines(true); //Removes all the visible lines on the JTree
    }

    ///Methods
    //Methods used for rollover icon
    public void setRolloverIcon(Icon rolloverG) {
        Icon oldValue = rolloverIcon;
        rolloverIcon = rolloverG;
        setIconRolloverEnabled(true);
        if (rolloverG != oldValue) {
            t.repaint();
        }
    }

    private void setIconRolloverEnabled(boolean handleRolloverEnabled) {
        boolean oldValue = iconRolloverEnabled;
        iconRolloverEnabled = handleRolloverEnabled;
        if (handleRolloverEnabled != oldValue) {
            t.repaint();
        }
    }

    //Paints the correct icon for the expand control
    //Ie when the mouse is over the collapsed icon it
    //changes to the rollover icon
    @Override
    protected void paintExpandControl(Graphics g,
                                      Rectangle clipBounds, Insets insets,
                                      Rectangle bounds, TreePath path,
                                      int row, boolean isExpanded,
                                      boolean hasBeenExpanded,
                                      boolean isLeaf) {
        Object value = path.getLastPathComponent();

        if(tM.hasChildren(value)) {
            int middleXOfKnob;
            if(isLeftToRight(t))
                middleXOfKnob = bounds.x - getRightChildIndent() + 1;
            else
                middleXOfKnob = bounds.x + bounds.width + getRightChildIndent() - 1;
            int middleYOfKnob = bounds.y + (bounds.height / 2);

            if(isExpanded) {
                Icon expandedIcon = getExpandedIcon();
                if(expandedIcon != null)
                  drawCentered(tree, g, expandedIcon, middleXOfKnob, middleYOfKnob );
            } else if(isLocationInExpandControl(path, uH.getXPos(), uH.getYPos())
                    & !isExpanded && iconRolloverEnabled) {
                if(row == uH.getRowHandle()) {
                    if(rolloverIcon != null)
                        drawCentered(tree, g, rolloverIcon, middleXOfKnob, middleYOfKnob);
                } else {
                    Icon collapsedIcon = getCollapsedIcon();
                    if(collapsedIcon != null)
                      drawCentered(tree, g, collapsedIcon, middleXOfKnob, middleYOfKnob);
                }
            } else {
                Icon collapsedIcon = getCollapsedIcon();
                if(collapsedIcon != null)
                  drawCentered(tree, g, collapsedIcon, middleXOfKnob, middleYOfKnob);
            }
        }
    }

    //Paints the row, updates background if mouse over row
    @Override
    protected void paintRow(Graphics g, Rectangle clipBounds,
                            Insets insets, Rectangle bounds, TreePath path,
                            int row, boolean isExpanded,
                            boolean hasBeenExpanded, boolean isLeaf) {
        // Don't paint the renderer if editing this row.
        if(editingComponent != null && editingRow == row)
            return;

        int leadIndex;

        if(tree.hasFocus())
            leadIndex = getLeadSelectionRow();
        else
            leadIndex = -1;

        Component component = ((FileNameRenderer) t.getCellRenderer()).getTreeCellRendererComponent
                      (tree, path.getLastPathComponent(),
                       tree.isRowSelected(row), isExpanded, isLeaf, row,
                       (leadIndex == row), (row == uH.getRow()));

        rendererPane.paintComponent(g, component, tree, bounds.x, bounds.y,
                                    bounds.width, bounds.height, true);
    }

    //Small utility class used for retrieving information so that
    //tasks such as adding a rollover icon or rollover background
    //can be completed
    private class UpdateHandler extends BasicTreeUI.MouseHandler {
        private JTree t = null;
        private int xPos = 0;
        private int yPos = 0;

        public UpdateHandler(JTree tree) {
            t = tree;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            xPos = e.getX();
            yPos = e.getY();
            t.repaint();
        }

        public int getXPos() {
            return xPos;
        }

        public int getYPos() {
            return yPos;
        }

        public int getRow() {
            return t.getRowForLocation(xPos, yPos);
        }

        public int getRowHandle() {
            return getRowForPath(t, getClosestPathForLocation(t, xPos, yPos));
        }
    }

    //Method used for fade handles in or out
    public void fadeHandles(boolean show, int msDur) {
        Timer time = new Timer((int) Math.floor(msDur / 25), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(show)
                    count++;
                else
                    count--;

                val = count * 0.04F;
                setCollapsedIcon(new AlphaImageIcon(new ImageIcon("resources/closed.png") , val));
                setExpandedIcon(new AlphaImageIcon(new ImageIcon("resources/open.png") , val));
                setRolloverIcon(new AlphaImageIcon(new ImageIcon("resources/rollover.png") , val));

                if(count == 0 || count == 25)
                    ((javax.swing.Timer) e.getSource()).stop();
                t.repaint();
            }
        });

        timers.add(time);
        for(javax.swing.Timer timer : timers) {
            timer.stop();
        }

        time.start();
    }

    //Methods used for remove lines
    public void removeLines(boolean ShowLines) {
        lines = ShowLines;
    }

    public void removeLines(boolean ShowLines, boolean dashedLines) {
        lines = ShowLines;
        lineTypeDashed = dashedLines;
    }

    @Override
    protected void paintDropLine(Graphics g) {
        if(!lines) {
            JTree.DropLocation loc = tree.getDropLocation();
            if (!isDropLine(loc))
                return;

            Color c = UIManager.getColor("Tree.dropLineColor");
            if (c != null) {
                g.setColor(c);
                Rectangle rect = getDropLineRect(loc);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
        }
    }

    @Override
    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
        if(!lines) {
            if (lineTypeDashed)
                drawDashedHorizontalLine(g, y, left, right);
            else
                g.drawLine(left, y, right, y);
        }
    }

    @Override
    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
        if(!lines) {
            if (lineTypeDashed)
                drawDashedVerticalLine(g, x, top, bottom);
            else
                g.drawLine(x, top, x, bottom);
        }
    }
}
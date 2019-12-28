package tree.design;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import tree.listeners.MouseHandler;
import tree.render.FileNameRenderer;
import tree.ui.TreeHandleUI;

/*
 * Class to set a range of custom UIs, renderers, and handlers
 */

@SuppressWarnings("serial")
public class DesignTree extends JTree {
    public DesignTree(TreeModel model) {
        super(model);

        setUI(new TreeHandleUI(this));
        setCellRenderer(new FileNameRenderer());

        setRootVisible(false); //Allows for each drive to act as a root
        setShowsRootHandles(true); //Shows handles for each individual drive

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        addMouseListener(new MouseHandler(this));
    }
}
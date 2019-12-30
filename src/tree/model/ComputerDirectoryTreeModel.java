package tree.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;



public class ComputerDirectoryTreeModel implements TreeModel {
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    public ComputerDirectoryTreeModel() {
        createRootLayout();
    }

    private void createRootLayout() {
        for(File path : File.listRoots()) { //Loops through the "root" directories on a computer - Determined by system
            if(path.exists())
                if(addable(path))
                    if(listDirectories(path).length != 0)
                        root.add(new DefaultMutableTreeNode(path));
        }
    }


    private boolean addable(File path) {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        String type = fsv.getSystemTypeDescription(path);
        //System.out.println(type); //For Debugging Purposes
        if(type != null) {
            if(type.equalsIgnoreCase("Local Disk"))
                return true;
            if(type.equalsIgnoreCase("USB Drive"))
                return true;
            return true; //Temporary fix for users who don't use windows
        }
        // return false;
        return true; // This make this shit work on macOS 
    }

    //Not used
    @Override
    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {}

    //Overrides the original getChild so that the method returns the correct directory
    @Override
    public Object getChild(Object parent, int index) {
        if(parent != root && parent instanceof DefaultMutableTreeNode) {
            File f = (File) (((DefaultMutableTreeNode) parent).getUserObject());
            return listDirectories(f)[index];
        } else if(parent != root) {
            File f = (File) parent;
            return listDirectories(f)[index];
        }
        return root.getChildAt(index);
    }

    //Overrides the original getChildCount so that
    //the method returns the correct value
    @Override
    public int getChildCount(Object parent) {
        if(parent != root && parent instanceof DefaultMutableTreeNode) {
            File f = (File) (((DefaultMutableTreeNode) parent).getUserObject());
            if (!f.isDirectory())
                return 0;
            else
                return listDirectories(f).length;
        } else if(parent != root) {
            File f = (File) parent;
            if (!f.isDirectory())
                return 0;
            else
                return listDirectories(f).length;
        }
        return root.getChildCount();
    }

    //Overrides the original hasChildren so that
    //the method returns the correct value
    public boolean hasChildren(Object parent) {
        if(parent != root && parent instanceof DefaultMutableTreeNode) {
            File f = (File) (((DefaultMutableTreeNode) parent).getUserObject());
            if (!f.isDirectory())
                return false;
            else
                return hasDirectory(f);
        } else if(parent != root) {
            File f = (File) parent;
            if (!f.isDirectory())
                return false;
            else
                return hasDirectory(f);
        }
        return root.getChildCount() != 0 ? true : false;
    }

    //Overrides the original getIndexOfChild so that
    //the method returns the correct value
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if(parent != root && parent instanceof DefaultMutableTreeNode) {
            File par = (File) (((DefaultMutableTreeNode) parent).getUserObject());
            File ch = (File) child;
            return Arrays.asList(listDirectories(par)).indexOf(ch);
        } else if(parent != root) {
            File par = (File) parent;
            File ch = (File) child;
            return Arrays.asList(listDirectories(par)).indexOf(ch);
        }

        return root.getIndex((TreeNode) child);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    //There should technically be no leaves as every directory
    //could potentially have a sub directory in it.
    @Override
    public boolean isLeaf(Object node) {
        return false;
    }

    //Not used
    @Override
    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {}

    //Not used
    @Override
    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {}

    //Lists all the sub directories of the given directory
    //if it is accessible. This will not work for folders
    //that require administrator privilages to view.
    private File[] listDirectories(File path) {
        ArrayList<File> arrayList = new ArrayList<File>();
        for(File temp : path.listFiles()) {
            if(temp.isDirectory() && !temp.isHidden())
                if(temp.listFiles() != null)
                    arrayList.add(temp);
        }

        return arrayList.toArray(new File[0]);
    }

    //Written to improve performance of listDirectories(path) != 0
    private boolean hasDirectory(File path) {
        for(File temp : path.listFiles()) {
            if(temp.isDirectory() && !temp.isHidden())
                if(temp.listFiles() != null)
                    return true;
        }

        return false;
    }
}
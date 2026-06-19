package com.adonis.Nukepad;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;

class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
public Component getTreeCellRendererComponent(
        JTree tree, Object value, boolean selected,
        boolean expanded, boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(
            tree, value, selected, expanded, leaf, row, hasFocus);

    if (value instanceof DefaultMutableTreeNode node) {
        Object userObj = node.getUserObject();
        if (userObj instanceof File file) {
            setText(file.getName());
            setIcon(MaterialIconLoader.forFile(file, expanded)); // ← changed
        } else if (userObj instanceof String s && !s.equals("Loading...")) {
            setIcon(MaterialIconLoader.getIcon("folder"));       // ← changed
        }
    }
    return this;
}
}
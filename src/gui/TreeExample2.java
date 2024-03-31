package gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TreeExample2 extends JFrame {
    private JTree tree;
    private JButton expandButton;
    private JButton collapseButton;
    private JButton expandToPathButton;
    private JButton refreshButton;
    private JButton expandToLevelButton;
    private JTextField levelTextField;

    public TreeExample2() {
        setTitle("Tree Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 350));

        // Create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        // Create the tree model with the root node
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        // Create the tree using the tree model
        tree = new JTree(treeModel);

        // Create the expand button
        expandButton = new JButton("Expand All");
        expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expandAllNodes(tree, true);
            }
        });

        // Create the collapse button
        collapseButton = new JButton("Collapse All");
        collapseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expandAllNodes(tree, false);
            }
        });

        // Create the expand to path button
        expandToPathButton = new JButton("Expand to Path");
        expandToPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = tree.getPathForRow(3); // Change the row index here
                if (path != null) {
                    expandToPath(tree, path);
                }
            }
        });

        // Create the refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTree(tree, treeModel);
            }
        });

        // Create the expand to level button
        expandToLevelButton = new JButton("Expand to Level");
        expandToLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String levelText = levelTextField.getText();
                try {
                    int level = Integer.parseInt(levelText);
                    expandToLevel(tree, new TreePath(treeModel.getRoot()), level);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(TreeExample2.this, "Invalid level value", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create the level input text field
        levelTextField = new JTextField("1asdadsd0", 10);

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(expandButton);
        buttonPanel.add(collapseButton);
        buttonPanel.add(expandToPathButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(new JLabel("Expand to Level:"));
        buttonPanel.add(levelTextField);
        buttonPanel.add(expandToLevelButton);

        // Add the tree and button panel to the frame
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TreeExample2();
            }
        });
    }

    // Expand or collapse all nodes of the tree
    private void expandAllNodes(JTree tree, boolean expand) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), expand);
    }

    // Recursively expand or collapse tree nodes
    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
                TreePath path = parent.pathByAddingChild(childNode);
                expandAll(tree, path, expand);
            }
        }

        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    // Expand tree nodes to the specified path
    private void expandToPath(JTree tree, TreePath path) {
        tree.expandPath(path);
    }

    // Refresh the tree by rebuilding it with new directory information
    private void refreshTree(JTree tree, DefaultTreeModel treeModel) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();
        buildTree(root, "Root", 4); // Specify the directory structure here
        treeModel.reload();
    }

    // Build the tree with directory information
    private void buildTree(DefaultMutableTreeNode parent, String name, int level) {
        if (level <= 0) {
            return;
        }

        for (int i = 1; i <= level; i++) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(name + " " + i);
            parent.add(node);
            buildTree(node, name, level - 1);
        }
    }

    // Expand tree nodes up to the specified level
    private void expandToLevel(JTree tree, TreePath parent, int level) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
        if (parent.getPathCount() <= level) {
            tree.expandPath(parent);
            if (node.getChildCount() >= 0) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
                    TreePath path = parent.pathByAddingChild(childNode);
                    expandToLevel(tree, path, level);
                }
            }
        }
    }
}
package gui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import structure.TreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TreeExample extends JFrame {
    public DefaultTreeModel treeModel;
    private JTree tree;
    private JButton expandButton;
    private JButton collapseButton;
    private JButton expandToPathButton;
    private JButton refreshButton;
    private JButton expandToLevelButton;

    public TreeExample(Notepad parent) {
        setTitle("目录窗口");
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭目录窗口时，不退出程序
        setPreferredSize(new Dimension(400, 300));
        // 改变系统默认字体
        Font font = new Font("SimSun", Font.PLAIN, 12);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }

        // Create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("目录");

        // Create the tree model with the root node
        treeModel = new DefaultTreeModel(root);

        // Create the tree using the tree model
        tree = new JTree(treeModel);

        // Create the expand button
        expandButton = new JButton("全部展开");
        // expandButton = new JButton("expand all");
        expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expandAllNodes(tree, true);
            }
        });

        // Create the collapse button
        // collapseButton = new JButton("collapse all");
        collapseButton = new JButton("折叠所有");
        collapseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expandAllNodes(tree, false);
            }
        });

        // Create the expand to path button
        expandToPathButton = new JButton("展开至指定目录");
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
        refreshButton = new JButton("更新目录");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTree(tree, treeModel);
            }
        });

        // Create the expand to level button
        expandToLevelButton = new JButton("展开至指定级别");
        expandToLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int level = 4; // Specify the level to expand
                expandToLevel(tree, new TreePath(treeModel.getRoot()), level);
            }
        });

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        // buttonPanel.add(expandButton);
        // buttonPanel.add(collapseButton);
        // buttonPanel.add(expandToPathButton);
        // buttonPanel.add(refreshButton);
        // buttonPanel.add(expandToLevelButton);

        // Add the tree and button panel to the frame
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.viewMenu_Directory.setSelected(false);
                parent.directoryVisible = false;
                parent.directoryIsActive = false;
            }
        });
    }

    // Expand or collapse all nodes of the tree

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TreeExample(new Notepad());
            }
        });
    }
    // Recursively expand or collapse tree nodes

    private void expandAllNodes(JTree tree, boolean expand) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), expand);
    }
    // Expand tree nodes to the specified path

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
    // Refresh the tree by rebuilding it with new directory information

    private void expandToPath(JTree tree, TreePath path) {
        tree.expandPath(path);
    }
    // Build the tree with directory information

    // 更新目录
    private void refreshTree(JTree tree, DefaultTreeModel treeModel) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();
        // buildTree(root, "Root", 4); // Specify the directory structure here
        treeModel.reload();
    }
    // Expand tree nodes up to the specified level

    public void buildTree(DefaultTreeModel treeModel, String name, int level, JSONArray directoryArray) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        for (int i = 0; i < directoryArray.size(); i++) {
            JSONObject level1TitleObject = directoryArray.getJSONObject(i);
            // DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(level1TitleObject.getString("title"));
            // node1.setUserObject(level1TitleObject.getInteger("lineIndex"));
            DefaultMutableTreeNode node1 = new DefaultMutableTreeNode();
            TreeNode treeNode1 = new TreeNode(level1TitleObject.getString("title"), level1TitleObject.getInteger("lineIndex"));
            node1.setUserObject(treeNode1);
            root.add(node1);

            JSONArray subTitles = level1TitleObject.getJSONArray("subTitles");
            for (int j = 0; j < subTitles.size(); j++) {
                JSONObject level2TitleObject = subTitles.getJSONObject(j);
                // DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(level2TitleObject.getString("title"));
                // node2.setUserObject(level2TitleObject.getInteger("lineIndex"));
                DefaultMutableTreeNode node2 = new DefaultMutableTreeNode();
                TreeNode treeNode2 = new TreeNode(level2TitleObject.getString("title"), level2TitleObject.getInteger("lineIndex"));
                node2.setUserObject(treeNode2);
                node1.add(node2);

                JSONArray subsubTitles = level2TitleObject.getJSONArray("subTitles");
                for (int k = 0; k < subsubTitles.size(); k++) {
                    JSONObject level3TitleObject = subsubTitles.getJSONObject(k);
                    // DefaultMutableTreeNode node3 = new DefaultMutableTreeNode(level3TitleObject.getString("title"));
                    // node3.setUserObject(level3TitleObject.getInteger("lineIndex"));
                    DefaultMutableTreeNode node3 = new DefaultMutableTreeNode();
                    TreeNode treeNode3 = new TreeNode(level3TitleObject.getString("title"), level3TitleObject.getInteger("lineIndex"));
                    node3.setUserObject(treeNode3);
                    node2.add(node3);

                }
            }
        }

        treeModel.reload();


        // if (level <= 0) {
        //     return;
        // }
        //
        // for (int i = 1; i <= level; i++) {
        //     DefaultMutableTreeNode node = new DefaultMutableTreeNode(name + " " + i);
        //     parent.add(node);
        //     buildTree(node, name, level - 1);
        // }
    }

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
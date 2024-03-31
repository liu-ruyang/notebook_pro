package structure;

import java.util.ArrayList;
import java.util.List;

public class DirectoryNode {
    private String name;
    private List<DirectoryNode> children;
    private DirectoryNode parent;

    public DirectoryNode(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<DirectoryNode> getChildren() {
        return children;
    }

    public void addChild(DirectoryNode child) {
        children.add(child);
        child.setParent(this);
    }

    public DirectoryNode getParent() {
        return parent;
    }

    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public int getLevel() {
        int level = 0;
        DirectoryNode currentNode = this;
        while (currentNode.getParent() != null) {
            level++;
            currentNode = currentNode.getParent();
        }
        return level;
    }
}
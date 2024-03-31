package structure;

public class TreeNode {
    public String title;
    public Integer lineIndex;

    public TreeNode() {
    }

    public TreeNode(String title, Integer lineIndex) {
        this.title = title;
        this.lineIndex = lineIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(Integer lineIndex) {
        this.lineIndex = lineIndex;
    }

    @Override
    public String toString() {
        return title + " ตฺ" + lineIndex + "าณ";
    }
}

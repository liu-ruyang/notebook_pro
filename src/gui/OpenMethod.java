package gui;

import com.alibaba.fastjson.JSONArray;
import structure.DirectoryParser;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.io.*;

/**
 * 将打开文件的两种方法分装成类
 *
 * @author Nancy
 * @version 1.0
 * @since jdk1.8
 */
public class OpenMethod extends JDialog {
    /**
     * 新建文件
     *
     * @param editArea      文本编辑区
     * @param oldValue      存放编辑区原来的内容
     * @param currentFile   当前文件名
     * @param isNewFile     是否新文件
     * @param statusLabel   状态栏标签
     * @param undo          撤销操作管理器的一个对象
     * @param editMenu_Undo 撤销菜单
     */
    public void openNewFile(JTextArea editArea, String oldValue, File currentFile, boolean isNewFile,
                            JLabel statusLabel, UndoManager undo, JMenuItem editMenu_Undo) {
        editArea.requestFocus();
        String currentValue = editArea.getText();
        boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
        if (isTextChange) {
            int saveChoose = JOptionPane.showConfirmDialog(this, "您的文件尚未保存，是否保存？", "提示",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (saveChoose == JOptionPane.YES_OPTION) {
                String str = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("另存为");
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel.setText("您没有选择任何文件");
                    return;
                }
                File saveFileName = fileChooser.getSelectedFile();
                if (saveFileName == null || saveFileName.getName().equals("")) {
                    JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        FileWriter fw = new FileWriter(saveFileName);
                        BufferedWriter bfw = new BufferedWriter(fw);
                        bfw.write(editArea.getText(), 0, editArea.getText().length());
                        bfw.flush();// 刷新该流的缓冲
                        bfw.close();
                        isNewFile = false;
                        Notepad.currentFile = saveFileName;
                        oldValue = editArea.getText();
                        this.setTitle(saveFileName.getName() + " - 记事本");
                        statusLabel.setText("当前打开文件：" + saveFileName.getAbsoluteFile());
                    } catch (IOException ioException) {
                    }
                }
            } else if (saveChoose == JOptionPane.NO_OPTION) {
                editArea.replaceRange("", 0, editArea.getText().length());
                statusLabel.setText(" 新建文件");
                this.setTitle("无标题 - 记事本");
                isNewFile = true;
                undo.discardAllEdits(); // 撤消所有的"撤消"操作
                editMenu_Undo.setEnabled(false);
                oldValue = editArea.getText();
            } else if (saveChoose == JOptionPane.CANCEL_OPTION) {
                return;
            }
        } else {
            editArea.replaceRange("", 0, editArea.getText().length());
            statusLabel.setText(" 新建文件");
            this.setTitle("无标题 - 记事本");
            isNewFile = true;
            undo.discardAllEdits();// 撤消所有的"撤消"操作
            editMenu_Undo.setEnabled(false);
            oldValue = editArea.getText();
        }
    }

    /**
     * 打开文件
     *
     * @param editArea    文本编辑区
     * @param oldValue    存放编辑区原来的内容
     * @param currentFile 当前文件名
     * @param isNewFile   是否新文件
     * @param statusLabel 状态栏标签
     */
    void openFile(JTextArea editArea, String oldValue, File currentFile, boolean isNewFile, JLabel statusLabel, TreeExample directory) {
        editArea.requestFocus();
        String currentValue = editArea.getText();
        boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
        // 文档被修改
        if (isTextChange) {
            int saveChoose = JOptionPane.showConfirmDialog(this, "您的文件尚未保存，是否保存？", "提示",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            // 是否要保存
            if (saveChoose == JOptionPane.YES_OPTION) {
                String str = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // fileChooser.setApproveButtonText("确定");
                fileChooser.setDialogTitle("另存为");
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel.setText("您没有选择任何文件");
                    return;
                }
                File saveFileName = fileChooser.getSelectedFile();
                // 存储到指定的文件
                if (saveFileName == null || saveFileName.getName().equals("")) {
                    JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        FileWriter fw = new FileWriter(saveFileName);
                        BufferedWriter bfw = new BufferedWriter(fw);
                        bfw.write(editArea.getText(), 0, editArea.getText().length());
                        bfw.flush();// 刷新该流的缓冲
                        bfw.close();
                        isNewFile = false;
                        Notepad.currentFile = saveFileName;
                        oldValue = editArea.getText();
                        this.setTitle(saveFileName.getName() + " - 记事本");
                        statusLabel.setText("当前打开文件：" + saveFileName.getAbsoluteFile());
                    } catch (IOException ioException) {
                    }
                }
            }
            // 文档不保存打开新文档
            else if (saveChoose == JOptionPane.NO_OPTION) {
                String str = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // fileChooser.setApproveButtonText("确定");
                fileChooser.setDialogTitle("打开文件");
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel.setText("您没有选择任何文件");
                    return;
                }
                File fileName = fileChooser.getSelectedFile();
                // 打开指定的新文档
                if (fileName == null || fileName.getName().equals("")) {
                    JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        // 解析目录
                        getDirectory(fileName, directory);

                        FileReader fr = new FileReader(fileName);
                        BufferedReader bfr = new BufferedReader(fr);
                        editArea.setText("");
                        while ((str = bfr.readLine()) != null) {
                            editArea.append(str);
                        }
                        this.setTitle(fileName.getName() + " - 记事本");
                        statusLabel.setText(" 当前打开文件：" + fileName.getAbsoluteFile());
                        fr.close();
                        isNewFile = false;
                        Notepad.currentFile = fileName;
                        oldValue = editArea.getText();
                    } catch (IOException ioException) {
                    }
                }
            } else {
                return;
            }
        }
        // 文档未被修改
        else {
            String str = null;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            // fileChooser.setApproveButtonText("确定");
            fileChooser.setDialogTitle("打开文件");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                statusLabel.setText(" 您没有选择任何文件 ");
                return;
            }
            // 打开指定的新文档
            File fileName = fileChooser.getSelectedFile();
            if (fileName == null || fileName.getName().equals("")) {
                JOptionPane.showMessageDialog(this, "不合法的文件名", "不合法的文件名", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    // 解析目录
                    getDirectory(fileName, directory);

                    FileReader fr = new FileReader(fileName);
                    BufferedReader bfr = new BufferedReader(fr);
                    editArea.setText("");
                    while ((str = bfr.readLine()) != null) {
                        editArea.append(str + "\n");
                    }
                    this.setTitle(fileName.getName() + " - 记事本");
                    statusLabel.setText(" 当前打开文件：" + fileName.getAbsoluteFile());
                    fr.close();
                    isNewFile = false;
                    Notepad.currentFile = fileName;
                    oldValue = editArea.getText();
                } catch (IOException ioException) {
                }
            }
        }
    }

    /**
     * 解析目录并保存至本地
     * 目录文件保存在同级目录下
     *
     * @param file      需要解析目录的文件
     * @param directory 目录窗口对象
     */
    public void getDirectory(File file, TreeExample directory) {
        String fileName = file.getAbsolutePath();
        JSONArray directories = DirectoryParser.readTextFile(fileName);
        Notepad.directories = directories;

        // 去后缀
        int dotIndex = fileName.lastIndexOf('.');
        String fileNameWithoutExtension = "";
        if (dotIndex != -1) {
            fileNameWithoutExtension = fileName.substring(0, dotIndex);
        }
        String directoryFilePath = fileNameWithoutExtension + "-directory.json";
        DirectoryParser.saveDirectoriesToJsonFile(directories, directoryFilePath);

        // 额外创建一个书签文件
        String bookmarkPath = fileNameWithoutExtension + "-bookmark.json";
        File bookmarkFile = new File(bookmarkPath);
        // 如果书签文件不存在,则新建一个
        if (!(bookmarkFile.exists())) {
            try {
                bookmarkFile.createNewFile();
                // FileWriter writer = new FileWriter(bookmarkFile);
                // writer.write("[]");
                // FileOutputStream fos = new FileOutputStream(bookmarkFile);
                // OutputStreamWriter writer = new OutputStreamWriter(fos, "GBK");
                // writer.write("[]");
                // writer.write("[]");
                // writer.write("[]");
                // writer.write("[]");
                // writer.write("[]");
                // JSONArray array = new JSONArray();
                // writer.write(array.toJSONString());
                // System.out.println("暂停");
                // fos.close();
                // writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            if (bookmarkFile.length() == 0) {
                FileWriter writer = new FileWriter(bookmarkFile);
                writer.write("[]");
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 显示目录到目录窗口上
        directory.buildTree(directory.treeModel, null, 0, directories);

    }
}

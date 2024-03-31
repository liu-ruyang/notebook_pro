package gui;

import com.alibaba.fastjson.JSONArray;
import structure.DirectoryParser;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.io.*;

/**
 * �����ļ������ַ�����װ����
 *
 * @author Nancy
 * @version 1.0
 * @since jdk1.8
 */
public class OpenMethod extends JDialog {
    /**
     * �½��ļ�
     *
     * @param editArea      �ı��༭��
     * @param oldValue      ��ű༭��ԭ��������
     * @param currentFile   ��ǰ�ļ���
     * @param isNewFile     �Ƿ����ļ�
     * @param statusLabel   ״̬����ǩ
     * @param undo          ����������������һ������
     * @param editMenu_Undo �����˵�
     */
    public void openNewFile(JTextArea editArea, String oldValue, File currentFile, boolean isNewFile,
                            JLabel statusLabel, UndoManager undo, JMenuItem editMenu_Undo) {
        editArea.requestFocus();
        String currentValue = editArea.getText();
        boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
        if (isTextChange) {
            int saveChoose = JOptionPane.showConfirmDialog(this, "�����ļ���δ���棬�Ƿ񱣴棿", "��ʾ",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (saveChoose == JOptionPane.YES_OPTION) {
                String str = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("���Ϊ");
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel.setText("��û��ѡ���κ��ļ�");
                    return;
                }
                File saveFileName = fileChooser.getSelectedFile();
                if (saveFileName == null || saveFileName.getName().equals("")) {
                    JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        FileWriter fw = new FileWriter(saveFileName);
                        BufferedWriter bfw = new BufferedWriter(fw);
                        bfw.write(editArea.getText(), 0, editArea.getText().length());
                        bfw.flush();// ˢ�¸����Ļ���
                        bfw.close();
                        isNewFile = false;
                        Notepad.currentFile = saveFileName;
                        oldValue = editArea.getText();
                        this.setTitle(saveFileName.getName() + " - ���±�");
                        statusLabel.setText("��ǰ���ļ���" + saveFileName.getAbsoluteFile());
                    } catch (IOException ioException) {
                    }
                }
            } else if (saveChoose == JOptionPane.NO_OPTION) {
                editArea.replaceRange("", 0, editArea.getText().length());
                statusLabel.setText(" �½��ļ�");
                this.setTitle("�ޱ��� - ���±�");
                isNewFile = true;
                undo.discardAllEdits(); // �������е�"����"����
                editMenu_Undo.setEnabled(false);
                oldValue = editArea.getText();
            } else if (saveChoose == JOptionPane.CANCEL_OPTION) {
                return;
            }
        } else {
            editArea.replaceRange("", 0, editArea.getText().length());
            statusLabel.setText(" �½��ļ�");
            this.setTitle("�ޱ��� - ���±�");
            isNewFile = true;
            undo.discardAllEdits();// �������е�"����"����
            editMenu_Undo.setEnabled(false);
            oldValue = editArea.getText();
        }
    }

    /**
     * ���ļ�
     *
     * @param editArea    �ı��༭��
     * @param oldValue    ��ű༭��ԭ��������
     * @param currentFile ��ǰ�ļ���
     * @param isNewFile   �Ƿ����ļ�
     * @param statusLabel ״̬����ǩ
     */
    void openFile(JTextArea editArea, String oldValue, File currentFile, boolean isNewFile, JLabel statusLabel, TreeExample directory) {
        editArea.requestFocus();
        String currentValue = editArea.getText();
        boolean isTextChange = (currentValue.equals(oldValue)) ? false : true;
        // �ĵ����޸�
        if (isTextChange) {
            int saveChoose = JOptionPane.showConfirmDialog(this, "�����ļ���δ���棬�Ƿ񱣴棿", "��ʾ",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            // �Ƿ�Ҫ����
            if (saveChoose == JOptionPane.YES_OPTION) {
                String str = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // fileChooser.setApproveButtonText("ȷ��");
                fileChooser.setDialogTitle("���Ϊ");
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel.setText("��û��ѡ���κ��ļ�");
                    return;
                }
                File saveFileName = fileChooser.getSelectedFile();
                // �洢��ָ�����ļ�
                if (saveFileName == null || saveFileName.getName().equals("")) {
                    JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        FileWriter fw = new FileWriter(saveFileName);
                        BufferedWriter bfw = new BufferedWriter(fw);
                        bfw.write(editArea.getText(), 0, editArea.getText().length());
                        bfw.flush();// ˢ�¸����Ļ���
                        bfw.close();
                        isNewFile = false;
                        Notepad.currentFile = saveFileName;
                        oldValue = editArea.getText();
                        this.setTitle(saveFileName.getName() + " - ���±�");
                        statusLabel.setText("��ǰ���ļ���" + saveFileName.getAbsoluteFile());
                    } catch (IOException ioException) {
                    }
                }
            }
            // �ĵ�����������ĵ�
            else if (saveChoose == JOptionPane.NO_OPTION) {
                String str = null;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // fileChooser.setApproveButtonText("ȷ��");
                fileChooser.setDialogTitle("���ļ�");
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    statusLabel.setText("��û��ѡ���κ��ļ�");
                    return;
                }
                File fileName = fileChooser.getSelectedFile();
                // ��ָ�������ĵ�
                if (fileName == null || fileName.getName().equals("")) {
                    JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        // ����Ŀ¼
                        getDirectory(fileName, directory);

                        FileReader fr = new FileReader(fileName);
                        BufferedReader bfr = new BufferedReader(fr);
                        editArea.setText("");
                        while ((str = bfr.readLine()) != null) {
                            editArea.append(str);
                        }
                        this.setTitle(fileName.getName() + " - ���±�");
                        statusLabel.setText(" ��ǰ���ļ���" + fileName.getAbsoluteFile());
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
        // �ĵ�δ���޸�
        else {
            String str = null;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            // fileChooser.setApproveButtonText("ȷ��");
            fileChooser.setDialogTitle("���ļ�");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                statusLabel.setText(" ��û��ѡ���κ��ļ� ");
                return;
            }
            // ��ָ�������ĵ�
            File fileName = fileChooser.getSelectedFile();
            if (fileName == null || fileName.getName().equals("")) {
                JOptionPane.showMessageDialog(this, "���Ϸ����ļ���", "���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    // ����Ŀ¼
                    getDirectory(fileName, directory);

                    FileReader fr = new FileReader(fileName);
                    BufferedReader bfr = new BufferedReader(fr);
                    editArea.setText("");
                    while ((str = bfr.readLine()) != null) {
                        editArea.append(str + "\n");
                    }
                    this.setTitle(fileName.getName() + " - ���±�");
                    statusLabel.setText(" ��ǰ���ļ���" + fileName.getAbsoluteFile());
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
     * ����Ŀ¼������������
     * Ŀ¼�ļ�������ͬ��Ŀ¼��
     *
     * @param file      ��Ҫ����Ŀ¼���ļ�
     * @param directory Ŀ¼���ڶ���
     */
    public void getDirectory(File file, TreeExample directory) {
        String fileName = file.getAbsolutePath();
        JSONArray directories = DirectoryParser.readTextFile(fileName);
        Notepad.directories = directories;

        // ȥ��׺
        int dotIndex = fileName.lastIndexOf('.');
        String fileNameWithoutExtension = "";
        if (dotIndex != -1) {
            fileNameWithoutExtension = fileName.substring(0, dotIndex);
        }
        String directoryFilePath = fileNameWithoutExtension + "-directory.json";
        DirectoryParser.saveDirectoriesToJsonFile(directories, directoryFilePath);

        // ���ⴴ��һ����ǩ�ļ�
        String bookmarkPath = fileNameWithoutExtension + "-bookmark.json";
        File bookmarkFile = new File(bookmarkPath);
        // �����ǩ�ļ�������,���½�һ��
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
                // System.out.println("��ͣ");
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


        // ��ʾĿ¼��Ŀ¼������
        directory.buildTree(directory.treeModel, null, 0, directories);

    }
}

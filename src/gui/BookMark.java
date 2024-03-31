package gui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookMark extends JFrame {
    public static JSONArray bookmarkArray;
    private List<String> bookmarks;
    private JList<String> bookmarkList;
    private DefaultListModel<String> listModel;


    public BookMark(Notepad parent) {
        bookmarks = new ArrayList<>();
        listModel = new DefaultListModel<>();
        bookmarkList = new JList<>(listModel);

        JButton addButton = new JButton("添加书签");
        JButton editButton = new JButton("编辑书签");
        JButton deleteButton = new JButton("删除书签");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookmark = JOptionPane.showInputDialog(BookMark.this,
                        "请输入书签名称：", "添加书签", JOptionPane.PLAIN_MESSAGE);
                if (bookmark != null && !bookmark.isEmpty()) {
                    bookmarks.add(bookmark);
                    listModel.addElement(bookmark);
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = bookmarkList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String bookmark = JOptionPane.showInputDialog(BookMark.this,
                            "请输入新的书签名称：", "编辑书签", JOptionPane.PLAIN_MESSAGE);
                    if (bookmark != null && !bookmark.isEmpty()) {
                        bookmarks.set(selectedIndex, bookmark);
                        listModel.set(selectedIndex, bookmark);
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = bookmarkList.getSelectedIndex();
                if (selectedIndex != -1) {
                    bookmarks.remove(selectedIndex);
                    listModel.remove(selectedIndex);
                    bookmarkArray.remove(selectedIndex);
                    saveBookMark(bookmarkArray);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        // buttonPanel.add(addButton);
        // buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JScrollPane scrollPane = new JScrollPane(bookmarkList);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setTitle("书签");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.isBookMarkActive = false;
                parent.isBookMarkVisible = false;
                parent.viewMenu_BookMark.setSelected(false);
            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
        loadBookmarksFromFile(Notepad.currentFile.getAbsolutePath());
    }

    public static void main(String[] args) {
        BookMark bookMark = new BookMark(new Notepad());
        bookMark.loadBookmarksFromFile(Notepad.currentFile.getAbsolutePath());
    }

    /**
     * 从文件中读取书签的数据
     *
     * @param filePath
     */
    public void loadBookmarksFromFile(String filePath) {
        // 去后缀=>获取书签文件的绝对路径
        int dotIndex = filePath.lastIndexOf('.');
        String fileNameWithoutExtension = "";
        if (dotIndex != -1) {
            fileNameWithoutExtension = filePath.substring(0, dotIndex);
        }
        String bookmarkFilePath = fileNameWithoutExtension + "-bookmark.json";
        // 获取书签的数据
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(bookmarkFilePath)));
            bookmarkArray = JSONArray.parseArray(jsonContent);

            for (int i = 0; i < bookmarkArray.size(); i++) {
                JSONObject jsonObject = bookmarkArray.getJSONObject(i);
                String content = jsonObject.getString("content");
                String time = jsonObject.getString("time");
                String lineIndex = jsonObject.getString("lineIndex");
                String bookmarkItem = time + "       第" + lineIndex + "行       " + content;
                bookmarks.add(bookmarkItem);
                listModel.addElement(bookmarkItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addBookMark(String content, int lineIndex) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        JSONObject bookmarkItem = new JSONObject();
        bookmarkItem.put("time", formattedDateTime);
        bookmarkItem.put("content", content);
        bookmarkItem.put("lineIndex", lineIndex);
        bookmarkArray.add(bookmarkItem);
        saveBookMark(bookmarkArray);
    }

    public void saveBookMark(JSONArray bookmarks) {
        String fileName = Notepad.currentFile.getAbsolutePath();
        // 去后缀
        int dotIndex = fileName.lastIndexOf('.');
        String fileNameWithoutExtension = "";
        if (dotIndex != -1) {
            fileNameWithoutExtension = fileName.substring(0, dotIndex);
        }
        String bookmarkPath = fileNameWithoutExtension + "-bookmark.json";
        // 若文件不存在，则需要创建文件
        File file = new File(bookmarkPath);
        if (file.exists())
            file.delete();
        file.delete(); // 删除文件，防止更新目录有之前的目录残留
        try {
            if (file.createNewFile()) {
                // System.out.println("文件创建成功");
            } else {
                // System.out.println("文件创建失败");
            }
        } catch (IOException e) {
            // System.out.println("创建文件时发生异常: " + e.getMessage());
        }

        // 存储书签至本地
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "GBK")) {
            writer.write(bookmarkArray.toJSONString());
            writer.close();
            // System.out.println("目录已保存到文件: " + file.getAbsolutePath());
            // 清空之前的书签列表
            bookmarks.clear();
            listModel.removeAllElements();
            // 重新加载书签列表
            loadBookmarksFromFile(Notepad.currentFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            // System.out.println("保存文件时出错: " + e.getMessage());
        }

    }
}
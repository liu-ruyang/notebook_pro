package gui;

import com.alibaba.fastjson.JSONArray;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ����ģ��Window�Դ��ļ��±���ʵ��90%���Ϲ��ܵ����Ƽ��±������±���������岼���� ��Ϊ�˵��������������ı��༭����״̬����
 * ������
 * 1��Notepad��̳���JFrame��ActionListener, DocumentListener�࣬���ڴ���ͼ�λ����档
 * 2������С�������ʼ����������ӵ�������һ������У�����ƴ��һ�������Ľ��档
 * ���裺
 * 1��������Ҫ�õ����ࡣ
 * 2����main()�д���Notepad�Ķ��󣬵����乹�캯����������г�ʼ����
 * 3��JMenuBar�����˵�����JMenu��JMenuItem�����������˵�������˵�����ӵ�JMenuBar�С�
 * 4��JToolBar������������
 * 5��JTextArea�����ı��༭����ʵ�ּ������ܣ���ʵ���Ҽ������˵����ܡ�
 * 6��JLabel����״̬����ʵ�ֱ༭״̬�Ŀ��ӻ���
 * 7������Щ�����������Container�У���������Ľ��档
 * 8�������������ʵ�ֲ˵��������������ı��༭��ִ����Ӧ����ʱ�������¼���
 * 9�����巽����ʵ�ּ������¼��Ĵ�������
 *
 * @author Nancy
 * @version 1.0
 * @since jdk1.8
 */
public class Notepad extends JFrame implements ActionListener, DocumentListener {
    static public JSONArray directories;
    static File currentFile;// ��ǰ�ļ���
    // Ŀ¼���Ĭ�ϲ��ɼ�
    public boolean directoryVisible = false;
    public boolean directoryIsActive = false;
    public boolean isBookMarkVisible = false;
    public boolean isBookMarkActive = false;
    // ������������������(�볷�������й�)
    protected UndoManager undo = new UndoManager();
    protected UndoableEditListener undoHandler = new UndoHandler();
    // �˵�
    JMenu fileMenu, editMenu, formatMenu, viewMenu, helpMenu;
    // �Ҽ������˵���
    JPopupMenu popupMenu;
    JMenuItem popupMenu_Undo, popupMenu_Cut, popupMenu_Copy, popupMenu_Paste, popupMenu_Delete, popupMenu_SelectAll, popupMenu_Bookmark;
    // ���ļ����Ĳ˵���
    JMenuItem fileMenu_New, fileMenu_Open, fileMenu_Save, fileMenu_SaveAs, fileMenu_Exit;
    // ���༭���Ĳ˵���
    JMenuItem editMenu_Undo, editMenu_Cut, editMenu_Copy, editMenu_Paste, editMenu_Delete, editMenu_Find,
            editMenu_Replace, editMenu_GoTo, editMenu_SelectAll, editMenu_TimeDate;
    // ����ʽ���Ĳ˵���
    JCheckBoxMenuItem formatMenu_LineWrap;
    JMenuItem formatMenu_Font;
    JMenuItem formatMenu_Color;
    // ���鿴���Ĳ˵���
    JCheckBoxMenuItem viewMenu_Status, viewMenu_ToolBar, editMenu_AutoSave, viewMenu_Directory, viewMenu_BookMark;
    // "������"��ť
    JButton newButton, openButton, saveButton, copyButton, cutButton, pasteButton;
    JPanel toolBar;
    // ���������Ĳ˵���
    JMenuItem helpMenu_HelpTopics, helpMenu_AboutNotepad;
    // ���ı����༭����
    JTextArea editArea;
    // Ŀ¼չʾ����
    JPanel directoryPanel;
    // ״̬����ǩ
    JLabel statusLabel;
    // JToolBar statusBar;
    // ϵͳ������
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Clipboard clipBoard = toolkit.getSystemClipboard();
    // ��������
    String oldValue;// ��ű༭��ԭ�������ݣ����ڱȽ��ı��Ƿ��иĶ�
    boolean isNewFile = true;// �Ƿ����ļ�(δ�������)
    private TreeExample directory;
    private TimerTask saveTask;
    private Timer saveTimer;
    private BookMark bookMark;

    // �޲ι��캯����ʼ
    public Notepad() {
        super("�๦�ܼ��±�");
        // �ı�ϵͳĬ������
        Font font = new Font("Dialog", Font.PLAIN, 12);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
        // ----------------------------����ͼ��
        Icon newIcon = new ImageIcon("src/gui/resource/new.gif");
        Icon openIcon = new ImageIcon("src/gui/resource/open.gif");
        Icon saveIcon = new ImageIcon("src/gui/resource/save.gif");
        Icon copyIcon = new ImageIcon("src/gui/resource/copy.gif");
        Icon cutIcon = new ImageIcon("src/gui/resource/cut.gif");
        Icon pasteIcon = new ImageIcon("src/gui/resource/paste.gif");

        // Icon newIcon = new ImageIcon("/new.gif");
        // Icon openIcon = new ImageIcon("/open.gif");
        // Icon saveIcon = new ImageIcon("/save.gif");
        // Icon copyIcon = new ImageIcon("/copy.gif");
        // Icon cutIcon = new ImageIcon("cut.gif");
        // Icon pasteIcon = new ImageIcon("./source/paste.gif");
        // ----------------------------�����˵���
        JMenuBar menuBar = new JMenuBar();
        // ----------------------------�����ļ��˵����˵��ע���¼�����
        fileMenu = new JMenu("�ļ�(F)");
        fileMenu.setMnemonic('F');// ���ÿ�ݼ�ALT+F

        fileMenu_New = new JMenuItem("�½�(N)", newIcon);
        fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        fileMenu_New.addActionListener(this);

        fileMenu_Open = new JMenuItem("��(O)...", openIcon);
        fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        fileMenu_Open.addActionListener(this);

        fileMenu_Save = new JMenuItem("����(S)", saveIcon);
        fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        fileMenu_Save.addActionListener(this);

        fileMenu_SaveAs = new JMenuItem("���Ϊ(A)...");
        fileMenu_SaveAs.addActionListener(this);

        fileMenu_Exit = new JMenuItem("�˳�(X)");
        fileMenu_Exit.addActionListener(this);

        // ----------------------------�����༭�˵����˵��ע���¼�����
        editMenu = new JMenu("�༭(E)");
        editMenu.setMnemonic('E');// ���ÿ�ݼ�ALT+E
        // ��ѡ��༭�˵�ʱ�����ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
        editMenu.addMenuListener(new MenuListener() {
            public void menuCanceled(MenuEvent e)// ȡ���˵�ʱ����
            {
                checkMenuItemEnabled();// ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
            }

            public void menuDeselected(MenuEvent e)// ȡ��ѡ��ĳ���˵�ʱ����
            {
                checkMenuItemEnabled();// ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
            }

            public void menuSelected(MenuEvent e)// ѡ��ĳ���˵�ʱ����
            {
                checkMenuItemEnabled();// ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
            }
        });

        editMenu_Undo = new JMenuItem("����(U)");
        editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        editMenu_Undo.addActionListener(this);
        editMenu_Undo.setEnabled(false);

        editMenu_Cut = new JMenuItem("����(T)", cutIcon);
        editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        editMenu_Cut.addActionListener(this);

        editMenu_Copy = new JMenuItem("����(C)", copyIcon);
        editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        editMenu_Copy.addActionListener(this);

        editMenu_Paste = new JMenuItem("ճ��(P)", pasteIcon);
        editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        editMenu_Paste.addActionListener(this);

        editMenu_Delete = new JMenuItem("ɾ��(D)");
        editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        editMenu_Delete.addActionListener(this);

        editMenu_Find = new JMenuItem("����(F)...");
        editMenu_Find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        editMenu_Find.addActionListener(this);

        editMenu_Replace = new JMenuItem("�滻(R)...", 'R');
        editMenu_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        editMenu_Replace.addActionListener(this);

        editMenu_GoTo = new JMenuItem("ת��(G)...", 'G');
        editMenu_GoTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
        editMenu_GoTo.addActionListener(this);

        editMenu_SelectAll = new JMenuItem("ȫѡ", 'A');
        editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        editMenu_SelectAll.addActionListener(this);

        editMenu_TimeDate = new JMenuItem("ʱ��/����(D)", 'D');
        editMenu_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        editMenu_TimeDate.addActionListener(this);

        // �Զ�����
        editMenu_AutoSave = new JCheckBoxMenuItem("�Զ�����");
        editMenu_AutoSave.setState(false);
        editMenu_AutoSave.addActionListener(this);

        // ----------------------------������ʽ�˵����˵��ע���¼�����
        formatMenu = new JMenu("��ʽ(O)");
        formatMenu.setMnemonic('O');// ���ÿ�ݼ�ALT+O

        formatMenu_LineWrap = new JCheckBoxMenuItem("�Զ�����(W)");
        formatMenu_LineWrap.setMnemonic('W');// ���ÿ�ݼ�ALT+W
        formatMenu_LineWrap.setState(true);
        formatMenu_LineWrap.addActionListener(this);

        formatMenu_Font = new JMenuItem("����(F)...");
        formatMenu_Font.addActionListener(this);

        formatMenu_Color = new JMenuItem("��ɫ(C)...");
        formatMenu_Color.addActionListener(this);
        // ----------------------------�����鿴�˵����˵��ע���¼�����
        viewMenu = new JMenu("�鿴(V)");
        viewMenu.setMnemonic('V');// ���ÿ�ݼ�ALT+V

        viewMenu_Status = new JCheckBoxMenuItem("״̬��(S)");
        viewMenu_Status.setMnemonic('S');// ���ÿ�ݼ�ALT+S
        viewMenu_Status.setState(true);
        viewMenu_Status.addActionListener(this);

        viewMenu_ToolBar = new JCheckBoxMenuItem("������(T)");
        viewMenu_ToolBar.setMnemonic('T');// ���ÿ�ݼ�ALT+S
        viewMenu_ToolBar.setState(true);
        viewMenu_ToolBar.addActionListener(this);

        // Ŀ¼
        viewMenu_Directory = new JCheckBoxMenuItem("Ŀ¼");
        viewMenu_Directory.setState(false);
        viewMenu_Directory.addActionListener(this);

        // ��ǩ
        viewMenu_BookMark = new JCheckBoxMenuItem("��ǩ");
        viewMenu_BookMark.setState(false);
        viewMenu_BookMark.addActionListener(this);

        // ----------------------------���������˵����˵��ע���¼�����
        helpMenu = new JMenu("����(H)");
        helpMenu.setMnemonic('H');// ���ÿ�ݼ�ALT+H

        helpMenu_HelpTopics = new JMenuItem("��������(H)");
        helpMenu_HelpTopics.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpMenu_HelpTopics.addActionListener(this);

        helpMenu_AboutNotepad = new JMenuItem("���ڼ��±�(A)");
        helpMenu_AboutNotepad.addActionListener(this);

        // ��˵������"�ļ�"�˵����˵���
        menuBar.add(fileMenu);
        fileMenu.add(fileMenu_New);
        fileMenu.add(fileMenu_Open);
        fileMenu.add(fileMenu_Save);
        fileMenu.add(fileMenu_SaveAs);
        fileMenu.addSeparator(); // �ָ���
        fileMenu.add(fileMenu_Exit);

        // ��˵������"�༭"�˵����˵���
        menuBar.add(editMenu);
        editMenu.add(editMenu_Undo);
        editMenu.addSeparator(); // �ָ���
        editMenu.add(editMenu_Cut);
        editMenu.add(editMenu_Copy);
        editMenu.add(editMenu_Paste);
        editMenu.add(editMenu_Delete);
        editMenu.addSeparator(); // �ָ���
        editMenu.add(editMenu_Find);
        editMenu.add(editMenu_Replace);
        editMenu.add(editMenu_GoTo);
        editMenu.addSeparator(); // �ָ���
        editMenu.add(editMenu_SelectAll);
        editMenu.add(editMenu_TimeDate);
        editMenu.addSeparator(); // �ָ���
        editMenu.add(editMenu_AutoSave);

        // ��˵������"��ʽ"�˵����˵���
        menuBar.add(formatMenu);
        formatMenu.add(formatMenu_LineWrap);
        formatMenu.add(formatMenu_Font);
        formatMenu.add(formatMenu_Color);

        // ��˵������"�鿴"�˵����˵���
        menuBar.add(viewMenu);
        viewMenu.add(viewMenu_Status);
        // viewMenu.add(viewMenu_ToolBar);
        viewMenu.add(viewMenu_Directory);
        viewMenu.add(viewMenu_BookMark);

        // ��˵������"����"�˵����˵���
        menuBar.add(helpMenu);
        helpMenu.add(helpMenu_HelpTopics);
        helpMenu.addSeparator();
        helpMenu.add(helpMenu_AboutNotepad);

        // �򴰿���Ӳ˵���
        this.setJMenuBar(menuBar);

        // ----------------------------�����ı��༭������ӹ�����
        editArea = new JTextArea(20, 50);
        JScrollPane scroller = new JScrollPane(editArea);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Ŀ¼���򲿷�
        // directoryPanel = new JPanel();
        // TreeExample treeExample = new TreeExample();
        // directoryPanel.setBackground(Color.LIGHT_GRAY); // ����Ŀ¼����ı���ɫ
        // JLabel directoryLabel = new JLabel("Directory");
        // directoryPanel.setVisible(directoryVisible);
        // directoryPanel.add(directoryLabel);
        // directoryPanel.add(treeExample);
        // Ŀ¼���ı��༭�����
        // JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroller, directoryPanel);
        // splitPane.setDividerLocation(400); // ���÷ָ�����λ�ã������������ֵĴ�С
        // toggleButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         directoryVisible = toggleButton.isSelected();
        //         directoryPanel.setVisible(directoryVisible);
        //     }
        // });
        // JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // togglePanel.add(toggleButton);

        // this.add(splitPane, BorderLayout.CENTER);// �򴰿�����ı��༭����Ŀ¼
        // this.add(togglePanel, BorderLayout.SOUTH); //
        this.add(scroller, BorderLayout.CENTER);// �򴰿�����ı��༭��
        editArea.setWrapStyleWord(true);// ���õ�����һ�в�������ʱ����
        editArea.setLineWrap(true);// �����ı��༭���Զ�����Ĭ��Ϊtrue,����"�Զ�����"
        oldValue = editArea.getText();// ��ȡԭ�ı��༭��������

        // �༭��ע���¼�����(�볷�������й�)
        editArea.getDocument().addUndoableEditListener(undoHandler);
        editArea.getDocument().addDocumentListener(this);

        // �����Ҽ������˵�
        popupMenu = new JPopupMenu();
        popupMenu_Undo = new JMenuItem("����(U)");
        popupMenu_Cut = new JMenuItem("����(T)");
        popupMenu_Copy = new JMenuItem("����(C)");
        popupMenu_Paste = new JMenuItem("ճ��(P)");
        popupMenu_Delete = new JMenuItem("ɾ��(D)");
        popupMenu_SelectAll = new JMenuItem("ȫѡ(A)");
        popupMenu_Bookmark = new JMenuItem("�����ǩ");

        popupMenu_Undo.setEnabled(false);

        // ���Ҽ��˵���Ӳ˵���ͷָ���
        popupMenu.add(popupMenu_Undo);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_Cut);
        popupMenu.add(popupMenu_Copy);
        popupMenu.add(popupMenu_Paste);
        popupMenu.add(popupMenu_Delete);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_SelectAll);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_Bookmark);

        // �ı��༭��ע���Ҽ��˵��¼�
        popupMenu_Undo.addActionListener(this);
        popupMenu_Cut.addActionListener(this);
        popupMenu_Copy.addActionListener(this);
        popupMenu_Paste.addActionListener(this);
        popupMenu_Delete.addActionListener(this);
        popupMenu_SelectAll.addActionListener(this);
        popupMenu_Bookmark.addActionListener(this);


        // �ı��༭��ע���Ҽ��˵��¼�
        editArea.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())// ���ش�����¼��Ƿ�Ϊ��ƽ̨�ĵ����˵������¼�
                {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());// ����������ߵ�����ռ��е�λ��X��Y
                }
                checkMenuItemEnabled();// ���ü��У����ƣ�ճ����ɾ���ȹ��ܵĿ�����
                editArea.requestFocus();// �༭����ȡ����
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())// ���ش�����¼��Ƿ�Ϊ��ƽ̨�ĵ����˵������¼�
                {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());// ����������ߵ�����ռ��е�λ��X��Y
                }
                checkMenuItemEnabled();// ���ü��У����ƣ�ճ����ɾ���ȹ��ܵĿ�����
                editArea.requestFocus();// �༭����ȡ����
            }
        });// �ı��༭��ע���Ҽ��˵��¼�����

        // ----------------------------����������
        toolBar = new JPanel();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        newButton = new JButton(newIcon);
        openButton = new JButton(openIcon);
        saveButton = new JButton(saveIcon);
        copyButton = new JButton(copyIcon);
        copyButton.setEnabled(false);
        cutButton = new JButton(cutIcon);
        cutButton.setEnabled(false);
        pasteButton = new JButton(pasteIcon);
        pasteButton.setEnabled(false);
        newButton.setPreferredSize(new Dimension(26, 22));
        openButton.setPreferredSize(new Dimension(26, 22));
        saveButton.setPreferredSize(new Dimension(26, 22));
        copyButton.setPreferredSize(new Dimension(26, 22));
        cutButton.setPreferredSize(new Dimension(26, 22));
        pasteButton.setPreferredSize(new Dimension(26, 22));
        // ע�Ṥ������ť�¼�
        newButton.addActionListener(this);
        openButton.addActionListener(this);
        saveButton.addActionListener(this);
        copyButton.addActionListener(this);
        cutButton.addActionListener(this);
        pasteButton.addActionListener(this);
        // �򹤾�����Ӱ�ť
        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(copyButton);
        toolBar.add(cutButton);
        toolBar.add(pasteButton);
        // ��������ӹ�����
        // this.add(toolBar, BorderLayout.NORTH);
        // -----------------------------------���������״̬��
        statusLabel = new JLabel("״̬��");
        this.add(statusLabel, BorderLayout.SOUTH);// �򴰿����״̬����ǩ
        // �ı�������������Ĭ��ͼ��
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.createImage("src/gui/resource/logo.png");
        this.setIconImage(image);
        // ���ô�������Ļ�ϵ�λ�á���С�Ϳɼ���
        this.setLocation(100, 100);
        this.setSize(650, 550);
        this.setVisible(true);

        // ������ʱ������
        if (saveTask == null) {
            saveTask = new TimerTask() {
                @Override
                public void run() {
                    saveTextAreaContent();
                }
            };
        }

        // ��Ӵ��ڼ�����
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                QuitMethod quitmethod = new QuitMethod();
                quitmethod.exitWindowChoose(editArea, oldValue, currentFile, isNewFile, statusLabel);
            }
        });
        checkMenuItemEnabled();
        editArea.requestFocus();
    }// ���췽������

    /**
     * main ����
     *
     * @param args-String
     */
    public static void main(String args[]) {
        Notepad notepad = new Notepad();
        // ʹ�� System exit �����˳�Ӧ�ó���
        notepad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * ���ò˵���Ŀ����ԣ����У����ƣ�ճ����ɾ������
     */
    public void checkMenuItemEnabled() {
        String selectText = editArea.getSelectedText();
        if (selectText == null) {
            editMenu_Cut.setEnabled(false);
            popupMenu_Cut.setEnabled(false);
            editMenu_Copy.setEnabled(false);
            popupMenu_Copy.setEnabled(false);
            editMenu_Delete.setEnabled(false);
            popupMenu_Delete.setEnabled(false);
            cutButton.setEnabled(false);
            copyButton.setEnabled(false);
        } else {
            editMenu_Cut.setEnabled(true);
            popupMenu_Cut.setEnabled(true);
            editMenu_Copy.setEnabled(true);
            popupMenu_Copy.setEnabled(true);
            editMenu_Delete.setEnabled(true);
            popupMenu_Delete.setEnabled(true);
            cutButton.setEnabled(true);
            copyButton.setEnabled(true);
        }
        // ճ�����ܿ������ж�
        Transferable contents = clipBoard.getContents(this);
        if (contents == null) {
            editMenu_Paste.setEnabled(false);
            popupMenu_Paste.setEnabled(false);
            pasteButton.setEnabled(false);
        } else {
            editMenu_Paste.setEnabled(true);
            popupMenu_Paste.setEnabled(true);
            pasteButton.setEnabled(true);
        }
    }

    /**
     * ���а�ť���¼�����
     */
    public void actionPerformed(ActionEvent e) {
        // �½�
        if (e.getSource() == fileMenu_New || e.getSource() == newButton) {
            OpenMethod openmethod = new OpenMethod();
            openmethod.openNewFile(editArea, oldValue, currentFile, isNewFile, statusLabel, undo, editMenu_Undo);
        } // �½�����
        // ��
        else if (e.getSource() == fileMenu_Open || e.getSource() == openButton) {
            OpenMethod openmethod = new OpenMethod();

            // Ŀ¼������
            if (!directoryIsActive) {
                directory = new TreeExample(this);

                viewMenu_Directory.setSelected(true);
                directoryVisible = viewMenu_Directory.isSelected();
                directoryIsActive = true;
            }

            openmethod.openFile(editArea, oldValue, currentFile, isNewFile, statusLabel, directory);
        } // �򿪽���
        // ����
        else if (e.getSource() == fileMenu_Save || e.getSource() == saveButton) {
            editArea.requestFocus();
            SaveMethod savemethod = new SaveMethod();
            savemethod.Save(editArea, oldValue, currentFile, isNewFile, statusLabel);
        } // �������
        // ���Ϊ
        else if (e.getSource() == fileMenu_SaveAs) {
            SaveMethod savemethod = new SaveMethod();
            savemethod.SaveAs(editArea, oldValue, currentFile, statusLabel);
        } // ���Ϊ����
        // �˳�
        else if (e.getSource() == fileMenu_Exit) {
            int exitChoose = JOptionPane.showConfirmDialog(this, "ȷ��Ҫ�˳���?", "�˳���ʾ", JOptionPane.OK_CANCEL_OPTION);
            if (exitChoose == JOptionPane.OK_OPTION) {
                System.exit(0);
            } else {
                return;
            }
        } // �˳�����
        // ����
        else if (e.getSource() == editMenu_Undo || e.getSource() == popupMenu_Undo) {
            editArea.requestFocus();
            if (undo.canUndo()) {
                try {
                    undo.undo();
                } catch (CannotUndoException ex) {
                    System.out.println("Unable to undo:" + ex);
                }
            }
            if (!undo.canUndo()) {
                editMenu_Undo.setEnabled(false);
            }
        } // ��������
        // ����
        else if (e.getSource() == editMenu_Cut || e.getSource() == popupMenu_Cut || e.getSource() == cutButton) {
            editArea.requestFocus();
            String text = editArea.getSelectedText();
            StringSelection selection = new StringSelection(text);
            clipBoard.setContents(selection, null);
            editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
            checkMenuItemEnabled();// ���ü��У����ƣ�ճ����ɾ�����ܵĿ�����
        } // ���н���
        // ����
        else if (e.getSource() == editMenu_Copy || e.getSource() == popupMenu_Copy || e.getSource() == copyButton) {
            editArea.requestFocus();
            String text = editArea.getSelectedText();
            StringSelection selection = new StringSelection(text);
            clipBoard.setContents(selection, null);
            checkMenuItemEnabled();// ���ü��У����ƣ�ճ����ɾ�����ܵĿ�����
        } // ���ƽ���
        // ճ��
        else if (e.getSource() == editMenu_Paste || e.getSource() == popupMenu_Paste || e.getSource() == pasteButton) {
            editArea.requestFocus();
            Transferable contents = clipBoard.getContents(this);
            if (contents == null)
                return;
            String text = "";
            try {
                text = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception exception) {
            }
            editArea.replaceRange(text, editArea.getSelectionStart(), editArea.getSelectionEnd());
            checkMenuItemEnabled();
        } // ճ������
        // ɾ��
        else if (e.getSource() == editMenu_Delete || e.getSource() == popupMenu_Delete) {
            editArea.requestFocus();
            editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
            checkMenuItemEnabled(); // ���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
        } // ɾ������
        // ����
        else if (e.getSource() == editMenu_Find) {
            editArea.requestFocus();
            findDialog f = new findDialog();
            f.find(editArea);
        } // ���ҽ���
        // �滻
        else if (e.getSource() == editMenu_Replace) {
            editArea.requestFocus();
            replaceDialog r = new replaceDialog();
            r.replace(editArea);
        } // �滻����
        // ת��
        else if (e.getSource() == editMenu_GoTo) {
            Go_toDialog go_to = new Go_toDialog();
            go_to.Go_toMethod(editArea);
        } // ת������
        // ʱ������
        else if (e.getSource() == editMenu_TimeDate) {
            editArea.requestFocus();
            Calendar rightNow = Calendar.getInstance();
            Date date = rightNow.getTime();
            editArea.insert(date.toString(), editArea.getCaretPosition());
        } // ʱ�����ڽ���
        // �����Զ�����
        else if (e.getSource() == editMenu_AutoSave) {
            boolean selected = editMenu_AutoSave.isSelected();
            if (selected) {
                // û��ָ���ļ�
                if (currentFile == null) {
                    editMenu_AutoSave.setSelected(false);
                    JOptionPane.showConfirmDialog(this, "�����ļ���δ���棬��Ҫ�ȱ�����ָ���ļ�!", "��ʾ", JOptionPane.PLAIN_MESSAGE);
                }
                // �����Զ�����
                else {
                    if (saveTimer == null) {
                        saveTimer = new Timer();
                    }
                    // SwingUtilities.invokeLater(new Runnable() {
                    //     @Override
                    //     public void run() {
                    saveTimer.schedule(saveTask, 5000, 5000);
                    // }
                    // });
                }
            } else {
                saveTimer.cancel();
            }
        }// �����Զ�����
        // ȫѡ
        else if (e.getSource() == editMenu_SelectAll || e.getSource() == popupMenu_SelectAll) {
            editArea.selectAll();
        } // ȫѡ����
        // ������ǩ
        else if (e.getSource() == popupMenu_Bookmark) {
            if (currentFile == null) {
                JOptionPane.showConfirmDialog(this, "�����ļ���δ���棬��Ҫ�ȱ�����ָ���ļ�!", "��ʾ", JOptionPane.PLAIN_MESSAGE);
                return;
            }
            // ��ǩ���ڲ�����
            if (!isBookMarkActive) {
                bookMark = new BookMark(this);
                isBookMarkActive = true;
                isBookMarkVisible = true;
                viewMenu_BookMark.setSelected(true);
            }
            // ��ǩ�����Ѵ���
            else {
                isBookMarkActive = true;
                isBookMarkVisible = true;
                bookMark.setVisible(true);
                viewMenu_BookMark.setSelected(true);
            }
            editArea.requestFocus();
            String selectedText = editArea.getSelectedText();
            int caretPosition = editArea.getCaretPosition();
            int lineNumber;
            try {
                lineNumber = editArea.getLineOfOffset(caretPosition);
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
            bookMark.addBookMark(selectedText, lineNumber);

        }// ������ǩ
        // �Զ�����(����ǰ������)
        else if (e.getSource() == formatMenu_LineWrap) {
            if (formatMenu_LineWrap.getState()) {
                editArea.setLineWrap(true);
                viewMenu_Status.setState(false);
                statusLabel.setVisible(false);
            } else
                editArea.setLineWrap(false);
        } // �Զ����н���
        // ��������
        else if (e.getSource() == formatMenu_Font) {
            editArea.requestFocus();
            fontDialog fon = new fontDialog();
            fon.font(editArea);
        } // �������ý���
        // ��ɫ����
        else if (e.getSource() == formatMenu_Color) {
            editArea.requestFocus();
            Color color = JColorChooser.showDialog(this, "����������ɫ", Color.black);
            if (color != null) {
                editArea.setForeground(color);
            } else
                return;
        } // ��ɫ���ý���
        // ����״̬���ɼ���
        else if (e.getSource() == viewMenu_Status) {
            if (viewMenu_Status.getState())
                statusLabel.setVisible(true);
            else
                statusLabel.setVisible(false);
        } // ����״̬���ɼ��Խ���
        // ���ù������ɼ���
        else if (e.getSource() == viewMenu_ToolBar) {
            if (viewMenu_ToolBar.getState())
                toolBar.setVisible(true);
            else
                toolBar.setVisible(false);
        } // ���ù������ɼ��Խ���
        // ����Ŀ¼�Ŀɼ���
        else if (e.getSource() == viewMenu_Directory) {
            // viewMenu_Directory.addActionListener(new ActionListener() {
            //     @Override
            //     public void actionPerformed(ActionEvent e) {
            directoryVisible = viewMenu_Directory.isSelected();
            // System.out.println(directoryVisible);
            // Ŀ¼���ڲ�����
            if (!directoryIsActive && directories != null) {
                directory = new TreeExample(this);
                // ��ʾĿ¼��Ŀ¼������
                directory.buildTree(directory.treeModel, null, 0, directories);
                directoryIsActive = true;
            } else if (!directoryIsActive) {
                directory = new TreeExample(this);
                // ��ʾĿ¼��Ŀ¼������
                directoryIsActive = true;
            }
            // Ŀ¼�Ѿ�����
            else {
                // ��ǰĿ¼����Ϊ�ɼ�
                if (directoryVisible) {
                    directory.setVisible(true);
                    directoryVisible = true;
                }
                // Ŀ¼�ɼ�
                else {
                    directory.setVisible(false);
                    directoryVisible = false;
                }
            }
            // directoryPanel.setVisible(directoryVisible);
            // if (directoryVisible == true) {
            // int parentWidth=(int) (parentPanel.getWidth() * 0.7);
            // ���ù������Ŀ��
            // scrollPane.setPreferredSize(new Dimension(400, scrollPane.getPreferredSize().height));
            // editArea.setPreferredSize(new Dimension(parentWidth, textArea.getPreferredSize().height);
            // }
            // }
            // });
        }// ����Ŀ¼�Ŀɼ���
        // ��ǩ����
        else if (e.getSource() == viewMenu_BookMark) {
            isBookMarkVisible = viewMenu_BookMark.isSelected();
            if (currentFile == null) {
                JOptionPane.showConfirmDialog(this, "�����ļ���δ���棬��Ҫ�ȱ�����ָ���ļ�!", "��ʾ", JOptionPane.PLAIN_MESSAGE);
                viewMenu_BookMark.setSelected(false);
                return;
            }
            // ��ǩ���ڲ�����
            if (!isBookMarkActive) {
                bookMark = new BookMark(this);
                // System.out.println(isBookMarkActive);
                isBookMarkActive = true;
            }
            // ��ǩ�����Ѵ���
            else {
                // ��ǩ���ڱ���Ϊ�ɼ�
                if (isBookMarkVisible) {
                    bookMark.setVisible(true);
                    isBookMarkVisible = true;
                }
                // ��ǩ���ڱ���Ϊ���ɼ�
                else {
                    bookMark.setVisible(false);
                    isBookMarkVisible = false;
                }
            }
        }// ��ǩ
        // ��������
        else if (e.getSource() == helpMenu_HelpTopics) {
            editArea.requestFocus();
            JOptionPane.showMessageDialog(this, "·��������Զ�⣬�Ὣ���¶�������", "��������", JOptionPane.INFORMATION_MESSAGE);
        } // �����������
        // ����
        else if (e.getSource() == helpMenu_AboutNotepad) {
            editArea.requestFocus();
            JOptionPane.showMessageDialog(this, "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n" + " ��д�ߣ�lry \n"
                            + " ��дʱ�䣺2024-03-22                          \n" + " e-mail��2920255877@qq.com                \n"
                            + " һЩ�ط�������ˣ�����֮��ϣ���������������лл��  \n" + "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n", "���±�",
                    JOptionPane.INFORMATION_MESSAGE);
        } // ���ڽ���
    }// ����actionPerformed()����

    // ʵ��DocumentListener�ӿ��еķ���(�볷�������й�)
    public void removeUpdate(DocumentEvent e) {
        editMenu_Undo.setEnabled(true);
    }

    public void insertUpdate(DocumentEvent e) {
        editMenu_Undo.setEnabled(true);
    }

    public void changedUpdate(DocumentEvent e) {
        editMenu_Undo.setEnabled(true);
    }// DocumentListener����

    // �����Զ������ļ�
    private void saveTextAreaContent() {
        String content = editArea.getText();
        try (FileWriter writer = new FileWriter(currentFile)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        oldValue = content;
        // System.out.println("JTextArea content saved.");
    }

    // ʵ�ֽӿ�UndoableEditListener����UndoHandler(�볷�������й�)
    class UndoHandler implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent uee) {
            undo.addEdit(uee.getEdit());
        }
    }
}

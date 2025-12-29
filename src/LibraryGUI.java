import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LibraryGUI extends JFrame {
    private Library library;
    private JTabbedPane tabbedPane;

    // 图书相关组件
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JTextField searchField;

    // 成员相关组件
    private JTable membersTable;
    private DefaultTableModel membersTableModel;

    // 借阅相关组件
    private JTextField borrowMemberField;
    private JTextField borrowBookField;
    private JTextField returnMemberField;
    private JTextField returnBookField;

    public LibraryGUI() {
        library = new Library();
        library.loadFromFile();

        setTitle("图书管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initComponents();
        loadInitialData();
    }

    private void initComponents() {
        // 创建标签页
        tabbedPane = new JTabbedPane();

        // 图书管理标签页
        tabbedPane.addTab("图书管理", createBooksPanel());

        // 成员管理标签页
        tabbedPane.addTab("成员管理", createMembersPanel());

        // 借阅管理标签页
        tabbedPane.addTab("借阅管理", createBorrowPanel());

        // 统计信息标签页
        tabbedPane.addTab("统计信息", createStatisticsPanel());

        // 添加标签页到窗口
        add(tabbedPane, BorderLayout.CENTER);

        // 添加底部状态栏
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 搜索和操作面板
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        // 搜索部分
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("搜索标题:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> searchBooks());
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.WEST);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addBookButton = new JButton("添加图书");
        addBookButton.addActionListener(e -> showAddBookDialog());
        buttonPanel.add(addBookButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshBooks());
        buttonPanel.add(refreshButton);

        topPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // 图书表格
        String[] columns = {"ID", "类型", "标题", "作者", "可用/总数", "状态"};
        booksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };

        booksTable = new JTable(booksTableModel);
        booksTable.setRowHeight(25);
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        booksTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(booksTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 底部操作按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton sortByTitleButton = new JButton("按标题排序");
        sortByTitleButton.addActionListener(e -> sortBooksByTitle());
        bottomPanel.add(sortByTitleButton);

        JButton sortByAuthorButton = new JButton("按作者排序");
        sortByAuthorButton.addActionListener(e -> sortBooksByAuthor());
        bottomPanel.add(sortByAuthorButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部按钮面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addMemberButton = new JButton("添加成员");
        addMemberButton.addActionListener(e -> showAddMemberDialog());
        topPanel.add(addMemberButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshMembers());
        topPanel.add(refreshButton);

        panel.add(topPanel, BorderLayout.NORTH);

        // 成员表格
        String[] columns = {"ID", "姓名", "当前借阅数"};
        membersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        membersTable = new JTable(membersTableModel);
        membersTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(membersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 查看成员借阅详情按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton viewBorrowedButton = new JButton("查看借阅详情");
        viewBorrowedButton.addActionListener(e -> showMemberBorrowedBooks());
        bottomPanel.add(viewBorrowedButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建主面板（使用GridBagLayout）
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 借书部分
        JPanel borrowPanel = new JPanel();
        borrowPanel.setBorder(BorderFactory.createTitledBorder("借阅图书"));
        borrowPanel.setLayout(new GridBagLayout());
        GridBagConstraints bgbc = new GridBagConstraints();
        bgbc.insets = new Insets(5, 5, 5, 5);
        bgbc.fill = GridBagConstraints.HORIZONTAL;

        bgbc.gridx = 0;
        bgbc.gridy = 0;
        borrowPanel.add(new JLabel("成员ID:"), bgbc);

        bgbc.gridx = 1;
        borrowMemberField = new JTextField(15);
        borrowPanel.add(borrowMemberField, bgbc);

        bgbc.gridx = 0;
        bgbc.gridy = 1;
        borrowPanel.add(new JLabel("图书ID:"), bgbc);

        bgbc.gridx = 1;
        borrowBookField = new JTextField(15);
        borrowPanel.add(borrowBookField, bgbc);

        bgbc.gridx = 0;
        bgbc.gridy = 2;
        bgbc.gridwidth = 2;
        JButton borrowButton = new JButton("借书");
        borrowButton.addActionListener(e -> borrowBook());
        borrowPanel.add(borrowButton, bgbc);

        // 还书部分
        JPanel returnPanel = new JPanel();
        returnPanel.setBorder(BorderFactory.createTitledBorder("归还图书"));
        returnPanel.setLayout(new GridBagLayout());

        bgbc.gridx = 0;
        bgbc.gridy = 0;
        bgbc.gridwidth = 1;
        returnPanel.add(new JLabel("成员ID:"), bgbc);

        bgbc.gridx = 1;
        returnMemberField = new JTextField(15);
        returnPanel.add(returnMemberField, bgbc);

        bgbc.gridx = 0;
        bgbc.gridy = 1;
        returnPanel.add(new JLabel("图书ID:"), bgbc);

        bgbc.gridx = 1;
        returnBookField = new JTextField(15);
        returnPanel.add(returnBookField, bgbc);

        bgbc.gridx = 0;
        bgbc.gridy = 2;
        bgbc.gridwidth = 2;
        JButton returnButton = new JButton("还书");
        returnButton.addActionListener(e -> returnBook());
        returnPanel.add(returnButton, bgbc);

        // 将借书和还书面板添加到主面板
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(borrowPanel, gbc);

        gbc.gridx = 1;
        mainPanel.add(returnPanel, gbc);

        panel.add(mainPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建文本区域显示统计信息
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(statsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 刷新按钮
        JButton refreshButton = new JButton("刷新统计");
        refreshButton.addActionListener(e -> updateStatistics(statsArea));
        panel.add(refreshButton, BorderLayout.SOUTH);

        // 初始加载统计
        updateStatistics(statsArea);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel(" 就绪");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusBar.add(statusLabel, BorderLayout.WEST);

        JLabel dataStatus = new JLabel("数据已加载 ");
        dataStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        statusBar.add(dataStatus, BorderLayout.EAST);

        return statusBar;
    }

    private void loadInitialData() {
        refreshBooks();
        refreshMembers();
    }

    private void refreshBooks() {
        booksTableModel.setRowCount(0);
        List<Book> books = library.listBooks();

        for (Book book : books) {
            String type = book instanceof EBook ? "电子书" : "印刷书";
            String status = book.canBorrow() ? "可借阅" : "不可借阅";

            booksTableModel.addRow(new Object[]{
                    book.getId(),
                    type,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getAvailableCopies() + "/" + book.getTotalCopies(),
                    status
            });
        }
    }

    private void refreshMembers() {
        membersTableModel.setRowCount(0);
        List<Member> members = library.listMembers();

        for (Member member : members) {
            List<BorrowRecord> borrowed = library.getMemberBorrowedBooks(member.getId());
            int borrowCount = borrowed.size();

            membersTableModel.addRow(new Object[]{
                    member.getId(),
                    member.getName(),
                    borrowCount
            });
        }
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshBooks();
            return;
        }

        booksTableModel.setRowCount(0);
        List<Book> results = library.searchBooksByTitle(keyword);

        for (Book book : results) {
            String type = book instanceof EBook ? "电子书" : "印刷书";
            String status = book.canBorrow() ? "可借阅" : "不可借阅";

            booksTableModel.addRow(new Object[]{
                    book.getId(),
                    type,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getAvailableCopies() + "/" + book.getTotalCopies(),
                    status
            });
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到相关图书", "搜索结果", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "添加图书", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 图书ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("图书ID:"), gbc);

        gbc.gridx = 1;
        JTextField idField = new JTextField(15);
        dialog.add(idField, gbc);

        // 标题
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("标题:"), gbc);

        gbc.gridx = 1;
        JTextField titleField = new JTextField(15);
        dialog.add(titleField, gbc);

        // 作者
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("作者:"), gbc);

        gbc.gridx = 1;
        JTextField authorField = new JTextField(15);
        dialog.add(authorField, gbc);

        // 类型选择
        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("类型:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"印刷书", "电子书"});
        dialog.add(typeCombo, gbc);

        // 副本数（仅印刷书）
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel copiesLabel = new JLabel("总副本数:");
        dialog.add(copiesLabel, gbc);

        gbc.gridx = 1;
        JTextField copiesField = new JTextField("1", 15);
        dialog.add(copiesField, gbc);

        // 类型选择监听器
        typeCombo.addActionListener(e -> {
            boolean isPrinted = "印刷书".equals(typeCombo.getSelectedItem());
            copiesLabel.setEnabled(isPrinted);
            copiesField.setEnabled(isPrinted);
        });

        // 按钮面板
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            String id = idField.getText().trim().toUpperCase();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();

            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请填写所有必填字段", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (library.hasBook(id)) {
                JOptionPane.showMessageDialog(dialog, "图书ID已存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Book book;
                if ("印刷书".equals(typeCombo.getSelectedItem())) {
                    int copies = Integer.parseInt(copiesField.getText().trim());
                    book = new PrintedBook(id, title, author, copies);
                } else {
                    book = new EBook(id, title, author);
                }

                library.addBook(book);
                refreshBooks();
                JOptionPane.showMessageDialog(dialog, "图书添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "副本数必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddMemberDialog() {
        JDialog dialog = new JDialog(this, "添加成员", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 成员ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("成员ID:"), gbc);

        gbc.gridx = 1;
        JTextField idField = new JTextField(15);
        dialog.add(idField, gbc);

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        dialog.add(nameField, gbc);

        // 按钮面板
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "姓名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (library.hasMember(id)) {
                    JOptionPane.showMessageDialog(dialog, "成员ID已存在", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Member member = new Member(id, name);
                library.addMember(member);
                refreshMembers();
                JOptionPane.showMessageDialog(dialog, "成员添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "ID必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void borrowBook() {
        try {
            int memberId = Integer.parseInt(borrowMemberField.getText().trim());
            String bookId = borrowBookField.getText().trim().toUpperCase();

            String result = library.borrowBook(memberId, bookId);
            JOptionPane.showMessageDialog(this, result, "借书结果", JOptionPane.INFORMATION_MESSAGE);

            // 清空输入框
            borrowMemberField.setText("");
            borrowBookField.setText("");

            // 刷新显示
            refreshBooks();
            refreshMembers();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "成员ID必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        try {
            int memberId = Integer.parseInt(returnMemberField.getText().trim());
            String bookId = returnBookField.getText().trim().toUpperCase();

            String result = library.returnBook(memberId, bookId);
            JOptionPane.showMessageDialog(this, result, "还书结果", JOptionPane.INFORMATION_MESSAGE);

            // 清空输入框
            returnMemberField.setText("");
            returnBookField.setText("");

            // 刷新显示
            refreshBooks();
            refreshMembers();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "成员ID必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortBooksByTitle() {
        booksTableModel.setRowCount(0);
        List<Book> books = library.listBooksSortedByTitle();

        for (Book book : books) {
            String type = book instanceof EBook ? "电子书" : "印刷书";
            String status = book.canBorrow() ? "可借阅" : "不可借阅";

            booksTableModel.addRow(new Object[]{
                    book.getId(),
                    type,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getAvailableCopies() + "/" + book.getTotalCopies(),
                    status
            });
        }
    }

    private void sortBooksByAuthor() {
        booksTableModel.setRowCount(0);
        List<Book> books = library.listBooksSortedByAuthor();

        for (Book book : books) {
            String type = book instanceof EBook ? "电子书" : "印刷书";
            String status = book.canBorrow() ? "可借阅" : "不可借阅";

            booksTableModel.addRow(new Object[]{
                    book.getId(),
                    type,
                    book.getTitle(),
                    book.getAuthor(),
                    book.getAvailableCopies() + "/" + book.getTotalCopies(),
                    status
            });
        }
    }

    private void showMemberBorrowedBooks() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个成员", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) membersTableModel.getValueAt(selectedRow, 0);
        String memberName = (String) membersTableModel.getValueAt(selectedRow, 1);

        List<BorrowRecord> records = library.getMemberBorrowedBooks(memberId);

        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "成员 " + memberName + " 当前没有借阅任何图书",
                    "借阅详情",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("成员: ").append(memberName).append(" (ID: ").append(memberId).append(")\n\n");
        sb.append("当前借阅的图书:\n");
        sb.append("=================================\n");

        for (int i = 0; i < records.size(); i++) {
            BorrowRecord record = records.get(i);
            sb.append(i + 1).append(") ")
                    .append(record.getBook().getId()).append(" - ")
                    .append(record.getBook().getTitle()).append("\n")
                    .append("   借阅日期: ").append(record.getBorrowDate()).append("\n")
                    .append("   到期日期: ").append(record.getDueDate()).append("\n\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "借阅详情", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStatistics(JTextArea statsArea) {
        StringBuilder sb = new StringBuilder();

        // 图书统计
        sb.append("===== 图书统计 =====\n");
        List<Book> books = library.listBooks();
        long printedCount = books.stream().filter(b -> b instanceof PrintedBook).count();
        long ebookCount = books.stream().filter(b -> b instanceof EBook).count();

        sb.append("总图书数量: ").append(books.size()).append("\n");
        sb.append("  印刷书: ").append(printedCount).append("\n");
        sb.append("  电子书: ").append(ebookCount).append("\n\n");

        // 成员统计
        sb.append("===== 成员统计 =====\n");
        List<Member> members = library.listMembers();
        sb.append("总成员数量: ").append(members.size()).append("\n\n");

        // 借阅统计
        sb.append("===== 借阅统计 =====\n");

        // 最活跃借阅者
        Member mostActive = library.findMostActiveBorrower();
        if (mostActive != null) {
            sb.append("最活跃借阅者: ").append(mostActive.getName())
                    .append(" (ID: ").append(mostActive.getId()).append(")\n");
        }

        sb.append("\n图书借阅次数:\n");
        sb.append("----------------\n");

        for (Book book : books) {
            long count = library.getBookBorrowCount(book.getId());
            sb.append(String.format("%-25s: %d 次\n", book.getTitle(), count));
        }

        statsArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 设置系统外观
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);

            // 注册窗口关闭时的保存操作
            gui.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    gui.library.saveToFile();
                }
            });
        });
    }
}
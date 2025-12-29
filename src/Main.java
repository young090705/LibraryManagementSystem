import java.util.*;

public class Main {
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // 初始化示例数据
        library.loadFromFile();

        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": listBooks(); break;
                case "2": listMembers(); break;
                case "3": addBook(); break;
                case "4": addMember(); break;
                case "5": searchBooks(); break;
                case "6": borrowBook(); break;
                case "7": returnBook(); break;
                case "8": showMemberBooks(); break;
                case "9": listBooksSortedByTitle(); break;
                case "10": listBooksSortedByAuthor(); break;
                case "11": showStatistics(); break;
                case "0": exit();
                library.saveToFile();
                return;
                default: System.out.println("Invalid option. Please try again.");
            }
//            pressEnterToContinue();
        }
    }

    private static void initializeSampleData() {
        // 添加图书
        library.addBook(new PrintedBook("B001", "The Hobbit", "J.R.R. Tolkien", 5));
        library.addBook(new PrintedBook("B002", "Clean Code", "Robert C. Martin", 2));
        library.addBook(new EBook("E100", "Java Basics", "Some Author"));
        library.addBook(new PrintedBook("B003", "Algorithms Illustrated", "Jane Doe", 1));

        // 添加成员
        library.addMember(new Member(1, "Alice"));
        library.addMember(new Member(2, "Bob"));
        library.addMember(new Member(3, "Charlie"));
    }

    private static void showMenu() {
        System.out.println("\n==================== 图书管理系统 ====================");
        System.out.println("请选择一个选项：");
        System.out.println("1) 列出所有图书");
        System.out.println("2) 列出所有成员");
        System.out.println("3) 添加图书");        // ← 新增
        System.out.println("4) 添加成员");        // ← 新增
        System.out.println("5) 按标题搜索图书");
        System.out.println("6) 借书");
        System.out.println("7) 还书");
        System.out.println("8) 显示成员借阅的图书");
        System.out.println("9) 按标题排序列出图书");
        System.out.println("10) 按作者排序列出图书");
        System.out.println("11) 统计信息");
        System.out.println("0) 退出");
        System.out.print(">");
    }

    private static void listBooks() {
        System.out.println("\n--- Book List ---");
        List<Book> books = library.listBooks();
        for (Book book : books) {
            System.out.println(book.getInfo());
        }
        System.out.println("Total books: " + books.size());
    }

    private static void listMembers() {
        System.out.println("\n--- Member List ---");
        List<Member> members = library.listMembers();
        for (Member member : members) {
            System.out.println(member);
        }
        System.out.println("Total members: " + members.size());
    }

    private static void addBook() {
        System.out.println("\n--- 添加图书 ---");

        // 输入ID
        System.out.print("请输入图书ID（如B001，E001）: ");
        String id = scanner.nextLine().trim().toUpperCase();
        if (library.hasBook(id)) {
            System.out.println("错误：图书ID " + id + " 已存在！");
            return;
        }

        // 输入标题
        System.out.print("请输入图书标题: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("错误：标题不能为空！");
            return;
        }

        // 输入作者
        System.out.print("请输入作者: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("错误：作者不能为空！");
            return;
        }

        // 选择类型
        System.out.print("请选择类型 (1-印刷版, 2-电子书): ");
        String type = scanner.nextLine().trim();

        try {
            Book book;
            if ("1".equals(type)) {
                // 印刷版
                System.out.print("请输入总副本数量: ");
                int copies = Integer.parseInt(scanner.nextLine().trim());
                book = new PrintedBook(id, title, author, copies);
            } else if ("2".equals(type)) {
                // 电子书
                book = new EBook(id, title, author);
            } else {
                System.out.println("错误：无效的类型选择！");
                return;
            }

            library.addBook(book);
            System.out.println("✓ 成功添加图书: " + book.getInfo());
        } catch (NumberFormatException e) {
            System.out.println("错误：数量必须是数字！");
        }
    }

    private static void addMember() {
        System.out.println("\n--- 添加成员 ---");

        // 输入ID
        System.out.print("请输入成员ID（数字）: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (library.hasMember(id)) {
                System.out.println("错误：成员ID " + id + " 已存在！");
                return;
            }

            // 输入姓名
            System.out.print("请输入成员姓名: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("错误：姓名不能为空！");
                return;
            }

            Member member = new Member(id, name);
            library.addMember(member);
            System.out.println("成功添加成员: " + member);
        } catch (NumberFormatException e) {
            System.out.println("错误：ID必须是数字！");
        }
    }

    private static void searchBooks() {
        System.out.println("\n--- Search Books by Title ---");
        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine().trim();

        List<Book> results = library.searchBooksByTitle(keyword);
        System.out.println("\nSearch results for \"" + keyword + "\":");
        for (Book book : results) {
            System.out.println(book.getInfo());
        }

        if (results.isEmpty()) {
            System.out.println("No books found.");
        }
    }

    private static void borrowBook() {
        System.out.println("\n--- Borrow a Book ---");
        System.out.print("Enter member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter book ID: ");
        String bookId = scanner.nextLine().trim().toUpperCase();

        String result = library.borrowBook(memberId, bookId);
        System.out.println(result);
    }

    private static void returnBook() {
        System.out.println("\n--- Return a Book ---");
        System.out.print("Enter member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter book ID: ");
        String bookId = scanner.nextLine().trim().toUpperCase();

        String result = library.returnBook(memberId, bookId);
        System.out.println(result);
    }

    private static void showMemberBooks() {
        System.out.println("\n--- Show Member's Borrowed Books ---");
        System.out.print("Enter member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());

        List<BorrowRecord> records = library.getMemberBorrowedBooks(memberId);
        Member member = library.listMembers().stream()
                .filter(m -> m.getId() == memberId)
                .findFirst()
                .orElse(null);

        if (member != null) {
            System.out.println("Borrowed books of member \"" + member.getName() + "\":");
            for (BorrowRecord record : records) {
                System.out.println("1) " + record);
            }
            if (records.isEmpty()) {
                System.out.println("No active borrow records.");
            }
        }
    }

    private static void listBooksSortedByTitle() {
        System.out.println("\n--- Books Sorted by Title ---");
        List<Book> books = library.listBooksSortedByTitle();
        for (Book book : books) {
            System.out.println(book.getInfo());
        }
    }

    private static void listBooksSortedByAuthor() {
        System.out.println("\n--- Books Sorted by Author ---");
        List<Book> books = library.listBooksSortedByAuthor();
        for (Book book : books) {
            System.out.println(book.getInfo());
        }
    }

    private static void showStatistics() {
        System.out.println("\n--- Statistics ---");

        // 最活跃借阅者
        Member mostActive = library.findMostActiveBorrower();
        if (mostActive != null) {
            System.out.println("Most active borrower: " + mostActive.getName());
        }

        // 图书借阅次数
        System.out.println("\nBook borrow counts:");
        for (Book book : library.listBooks()) {
            long count = library.getBookBorrowCount(book.getId());
            System.out.printf("%s: %d times borrowed\n", book.getTitle(), count);
        }
    }

    private static void pressEnterToContinue() {
        System.out.print("\n(Press Enter to continue)");
        scanner.nextLine();
    }

    private static void exit() {
        System.out.println("Exiting Library Management System... \nGoodbye!");
    }
}
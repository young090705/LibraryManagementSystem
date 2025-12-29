import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Library {
    private Map<String, Book> booksById;
    private Map<Integer, Member> membersById;
    private List<BorrowRecord> records;
    private static final String DATA_FILE = "I:\\Java实验\\LibraryManagementSystem\\src\\library_data.txt";

    public Library() {
        this.booksById = new HashMap<>();
        this.membersById = new HashMap<>();
        this.records = new ArrayList<>();
    }

    public void addBook(Book book) {
        booksById.put(book.getId(), book);
    }

    public void addMember(Member member) {
        membersById.put(member.getId(), member);
    }

    public List<Book> listBooks() {
        return new ArrayList<>(booksById.values());
    }

    public List<Member> listMembers() {
        return new ArrayList<>(membersById.values());
    }

    public String borrowBook(int memberId, String bookId) {
        Member member = membersById.get(memberId);
        if (member == null) {
            return String.format("Error: Member with ID %d does not exist.", memberId);
        }

        Book book = booksById.get(bookId);
        if (book == null) {
            return String.format("Error: Book with ID %s not found.", bookId);
        }

        if (!book.canBorrow()) {
            return String.format("Failed: No available copies of \"%s\". (Available:%d/%d)",
                    book.getTitle(), book.getAvailableCopies(), book.getTotalCopies());
        }

        // 检查是否已借过且未归还
        boolean alreadyBorrowed = records.stream()
                .anyMatch(r -> r.getMember().getId() == memberId
                        && r.getBook().getId().equals(bookId)
                        && !r.isReturned());

        if (alreadyBorrowed) {
            return String.format("Failed: Member \"%s\" already borrowed \"%s\" and hasn't returned it.",
                    member.getName(), book.getTitle());
        }

        book.borrowOne();
        BorrowRecord record = new BorrowRecord(book, member);
        records.add(record);
        return String.format("Success! Member \"%s\" borrowed \"%s\". Remaining copies: %d/%d",
                member.getName(), book.getTitle(), book.getAvailableCopies(), book.getTotalCopies());
    }

    public String returnBook(int memberId, String bookId) {
        Member member = membersById.get(memberId);
        if (member == null) {
            return String.format("Error: Member with ID %d does not exist.", memberId);
        }

        Book book = booksById.get(bookId);
        if (book == null) {
            return String.format("Error: Book with ID %s not found.", bookId);
        }

        BorrowRecord record = records.stream()
                .filter(r -> r.getMember().getId() == memberId
                        && r.getBook().getId().equals(bookId)
                        && !r.isReturned())
                .findFirst()
                .orElse(null);

        if (record == null) {
            return String.format("Failed: Member \"%s\" has no active borrow record for book %s.",
                    member.getName(), bookId);
        }

        record.setReturned(true);
        book.returnOne();
        return String.format("Success! Member \"%s\" returned \"%s\". Available copies: %d/%d",
                member.getName(), book.getTitle(), book.getAvailableCopies(), book.getTotalCopies());
    }

    public boolean hasBook(String id) {
        return booksById.containsKey(id);
    }

    public boolean hasMember(int id) {
        return membersById.containsKey(id);
    }

    // Week 2: 搜索功能
    public List<Book> searchBooksByTitle(String keyword) {
        return booksById.values().stream()
                .filter(book -> book.matchesTitle(keyword))
                .collect(Collectors.toList());
    }

    // Week 3: 排序功能
    public List<Book> listBooksSortedByTitle() {
        List<Book> books = new ArrayList<>(booksById.values());
        Collections.sort(books, Comparator.comparing(Book::getTitle));
        return books;
    }

    public List<Book> listBooksSortedByAuthor() {
        List<Book> books = new ArrayList<>(booksById.values());
        Collections.sort(books, Comparator.comparing(Book::getAuthor));
        return books;
    }

    // Week 3: 统计功能
    public List<BorrowRecord> getMemberBorrowedBooks(int memberId) {
        return records.stream()
                .filter(r -> r.getMember().getId() == memberId && !r.isReturned())
                .collect(Collectors.toList());
    }

    public long getBookBorrowCount(String bookId) {
        return records.stream()
                .filter(r -> r.getBook().getId().equals(bookId))
                .count();
    }

    public Member findMostActiveBorrower() {
        Map<Integer, Long> borrowCountMap = records.stream()
                .filter(r -> !r.isReturned())
                .collect(Collectors.groupingBy(r -> r.getMember().getId(), Collectors.counting()));

        return borrowCountMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> membersById.get(entry.getKey()))
                .orElse(null);
    }

    public void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("数据文件不存在，将创建新文件");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // 跳过注释和空行

                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String type = parts[0];
                switch (type) {
                    case "BOOK":
                        loadBook(parts);
                        break;
                    case "MEMBER":
                        loadMember(parts);
                        break;
                    case "RECORD":
                        loadRecord(parts);
                        break;
                }
            }
            System.out.println("数据加载成功！");
        } catch (IOException e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }

    private void loadBook(String[] parts) {
        String bookType = parts[1];
        String id = parts[2];
        String title = parts[3];
        String author = parts[4];
        int copies = Integer.parseInt(parts[5]);

        if ("PRINTED".equals(bookType)) {
            addBook(new PrintedBook(id, title, author, copies));
        } else if ("EBOOK".equals(bookType)) {
            addBook(new EBook(id, title, author));
        }
    }

    private void loadMember(String[] parts) {
        int id = Integer.parseInt(parts[1]);
        String name = parts[2];
        addMember(new Member(id, name));
    }

    private void loadRecord(String[] parts) {
        String bookId = parts[1];
        int memberId = Integer.parseInt(parts[2]);
        LocalDate borrowDate = LocalDate.parse(parts[3]);
        LocalDate dueDate = LocalDate.parse(parts[4]);
        boolean returned = Boolean.parseBoolean(parts[5]);

        Book book = booksById.get(bookId);
        Member member = membersById.get(memberId);

        if (book != null && member != null) {
            BorrowRecord record = new BorrowRecord(book, member);
            record.setReturned(returned);
            // 注意：实际项目中可能需要修改BorrowRecord以支持设置日期
            records.add(record);

            if (!returned) {
                book.borrowOne(); // 更新库存
            }
        }
    }

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            // 写入图书信息
            writer.println("# Books");
            for (Book book : booksById.values()) {
                if (book instanceof PrintedBook) {
                    writer.printf("BOOK,PRINTED,%s,%s,%s,%d\n",
                            book.getId(), book.getTitle(), book.getAuthor(), book.getTotalCopies());
                } else if (book instanceof EBook) {
                    writer.printf("BOOK,EBOOK,%s,%s,%s,0\n",
                            book.getId(), book.getTitle(), book.getAuthor());
                }
            }

            writer.println("\n# Members");
            // 写入成员信息
            for (Member member : membersById.values()) {
                writer.printf("MEMBER,%d,%s\n", member.getId(), member.getName());
            }

            writer.println("\n# BorrowRecords");
            // 写入借阅记录
            for (BorrowRecord record : records) {
                writer.printf("RECORD,%s,%d,%s,%s,%b\n",
                        record.getBook().getId(),
                        record.getMember().getId(),
                        record.getBorrowDate(),
                        record.getDueDate(),
                        record.isReturned());
            }

            System.out.println("数据已保存到 " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }
}
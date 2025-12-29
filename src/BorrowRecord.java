import java.time.LocalDate;

public class BorrowRecord {
    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;

    public BorrowRecord(Book book, Member member) {
        this.book = book;
        this.member = member;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusWeeks(2); // 默认2周归还期
        this.returned = false;
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    @Override
    public String toString() {
        return String.format("%s %s (Borrowed on:%s, Due:%s)",
                book.getId(), book.getTitle(), borrowDate, dueDate);
    }
}
public class PrintedBook extends Book {
    public PrintedBook(String id, String title, String author, int totalCopies) {
        super(id, title, author, totalCopies);
    }

    @Override
    public String getInfo() {
        return String.format("ID:%s [Print] %s by %s (%d/%d copies)",
                id, title, author, availableCopies, totalCopies);
    }
}
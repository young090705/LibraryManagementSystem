import java.time.LocalDate;

public abstract class Book {

    protected String id;
    protected String title;
    protected String author;
    protected int totalCopies;
    protected int availableCopies;

    public Book(String id, String title, String author, int totalCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public boolean canBorrow() {
        return availableCopies > 0;
    }

    public void borrowOne() {
        if (canBorrow()) {
            availableCopies--;
        }
    }

    public void returnOne() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public abstract String getInfo();

    public boolean matchesTitle(String keyword) {
        return title.toLowerCase().contains(keyword.toLowerCase());
    }
}
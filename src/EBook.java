public class EBook extends Book {
    public EBook(String id, String title, String author) {
        super(id, title, author, Integer.MAX_VALUE);
    }

    @Override
    public boolean canBorrow() {
        return true; // 电子书无限副本
    }

    @Override
    public void borrowOne() {
        // 电子书不减少库存
    }

    @Override
    public void returnOne() {
        // 电子书不增加库存
    }

    @Override
    public String getInfo() {
        return String.format("ID:%s [E-Book] %s (E-Book) by %s (unlimited)",
                id, title, author);
    }
}
import java.util.*;
import java.util.stream.Collectors;

public class BooksTest {
    static class Book implements Comparable<Book> {
        private final String title;
        private final String category;
        private final float price;

        Book(String title, String category, float price) {
            this.title = title;
            this.category = category;
            this.price = price;
        }

        public String getTitle() {
            return title;
        }

        public float getPrice() {
            return price;
        }

        public static final Comparator<Book> BY_PRICE_ASC =
                Comparator.comparing(Book::getPrice);

        public static final Comparator<Book> BY_TITLE_ASC =
                Comparator.comparing(Book::getTitle);

        @Override
        public int compareTo(Book o) {
            Comparator<Book> c = BY_TITLE_ASC.thenComparing(BY_PRICE_ASC);
            return c.compare(this, o);
        }

        @Override
        public String toString() {
            // Book A (A) 29.41
            return String.format("%s (%s) %.2f", title, category, price);
        }
    }

    static class BookCollection {
        private final Map<String, Set<Book>> categoryMap;

        public BookCollection() {
            categoryMap = new TreeMap<>();
        }

        public void addBook(Book book) {
            Set<Book> bookSet = categoryMap.computeIfAbsent(book.category, k -> new TreeSet<>());
            bookSet.add(book);
        }

        public void printByCategory(String category) {
            Set<Book> bookSet = categoryMap.get(category);
            bookSet.forEach(System.out::println);
        }
        // ги печати сите книги од проследената категорија
        // (се споредува стрингот без разлика на мали и големи букви),
        // сортирани според насловот на книгата (ако насловот е ист, се сортираат според цената).

        public List<Book> getCheapestN(int n) {
            return categoryMap.values().stream().flatMap(Collection::stream)
                    .sorted(Book.BY_PRICE_ASC.thenComparing(Book.BY_TITLE_ASC))
                    .limit(n)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        // враќа листа на најевтините N книги
        // (ако има помалку од N книги во колекцијата, ги враќа сите).

    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        BookCollection booksCollection = new BookCollection();
        Set<String> categories = fillCollection(scanner, booksCollection);
        System.out.println("=== PRINT BY CATEGORY ===");
        for (String category : categories) {
            System.out.println("CATEGORY: " + category);
            booksCollection.printByCategory(category);
        }
        System.out.println("=== TOP N BY PRICE ===");
        print(booksCollection.getCheapestN(n));
    }

    static void print(List<Book> books) {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    static TreeSet<String> fillCollection(Scanner scanner,
                                          BookCollection collection) {
        TreeSet<String> categories = new TreeSet<String>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            Book book = new Book(parts[0], parts[1], Float.parseFloat(parts[2]));
            collection.addBook(book);
            categories.add(parts[1]);
        }
        return categories;
    }
}


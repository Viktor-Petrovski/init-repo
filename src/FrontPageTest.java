import java.util.*;
import java.util.stream.Collectors;

public class FrontPageTest {
    static class CategoryNotFoundException extends Exception {
        public CategoryNotFoundException(String category) {
            super(String.format("Category %s was not found", category));
        }
    }
    static class Category {
        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Category category = (Category) o;
            return Objects.equals(name, category.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }
    abstract static class NewsItem {
        protected final String title;
        protected final Date published;
        protected final Category category;

        NewsItem(String title, Date published, Category category) {
            this.title = title;
            this.published = published;
            this.category = category;
        }

        public Category getCategory() {
            return category;
        }

        protected String getTeaser() {
            return String.format("");
            // враќа String составен од насловот на веста, пред колку минути е објавена веста (цел број минути),
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            NewsItem newsItem = (NewsItem) o;
            return Objects.equals(title, newsItem.title);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(title);
        }
    }

    static class TextNewsItem extends NewsItem {
        private final String text;
        TextNewsItem(String title, Date published, Category category, String text) {
            super(title, published, category);
            this.text = text;
        }
        public String getTeaser() {
            return "";
        }
        // и максимум 80 знаци од содржината на веста, сите одделени со нов ред.

    }

    static class MediaNewsItem extends NewsItem {
        private final String url;
        private final int views;
        MediaNewsItem(String title, Date published, Category category, String url, int views) {
            super(title, published, category);
            this.url = url;
            this.views = views;
        }

        public String getTeaser() {
            return "";
        }
        // url-то на веста и бројот на погледи, сите одделени со нов ред.

    }

    static class FrontPage {
        private final Category[] categories;
        private final List<NewsItem> items;

        FrontPage(Category[] categories) {
            this.categories = categories;
            items = new ArrayList<>();
        }

        void addNewsItem(NewsItem newsItem) {
            items.add(newsItem);
        }

        public List<NewsItem> listByCategory(Category category) {
            return items.stream()
                    .filter(i -> i.getCategory().equals(category))
                    .collect(Collectors.toList());
        }
        // прима еден аргумент рефернца кон објект од Category и враќа листа со сите вести од таа категорија

        public List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
            List<NewsItem> res = items.stream()
                    .filter(i -> i.getCategory().name.equals(category))
                    .collect(Collectors.toList());
            if (res.isEmpty())
                throw new CategoryNotFoundException(category);
            return res;
        }
        // прима еден аргумент String името на категоријата и враќа листа со сите вести од категоријата со тоа име.
        // Ако не постои категорија со вакво име во полето со категории, да се фрли
        // исклучок од тип CategoryNotFoundException во кој се пренесува името на категоријата која не е најдена

        @Override
        public String toString() {
            return String.format("");
        }

        // враќа String составен од сите кратки содржини на вестите (повик на методот getTeaser()).

    }

    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}


// Vasiot kod ovde
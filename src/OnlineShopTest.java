import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class OnlineShopTest {
    enum COMPARATOR_TYPE {
        NEWEST_FIRST,
        OLDEST_FIRST,
        LOWEST_PRICE_FIRST,
        HIGHEST_PRICE_FIRST,
        MOST_SOLD_FIRST,
        LEAST_SOLD_FIRST
    }

    static class ProductNotFoundException extends Exception {
        ProductNotFoundException(String message) {
            super(message);
        }
    }


    static class Product {
        private final String category;
        private final String id;
        private final String name;
        private final LocalDateTime createdAt;
        private final double price;

        private int quantitySold;

        Product(String category, String id, String name, LocalDateTime createdAt, double price) {
            this.category = category;
            this.id = id;
            this.name = name;
            this.createdAt = createdAt;
            this.price = price;

            quantitySold = 0;
        }

        public double buy(int quantity) {
            quantitySold += quantity;
            return price * quantity;
        }

        public static final Comparator<Product> OLDEST_FIRST = Comparator.comparing(p -> p.createdAt);
        public static final Comparator<Product> NEWEST_FIRST = OLDEST_FIRST.reversed();
        public static final Comparator<Product> LOWEST_PRICE_FIRST = Comparator.comparing(p -> p.price);
        public static final Comparator<Product> HIGHEST_PRICE_FIRST = LOWEST_PRICE_FIRST.reversed();
        public static final Comparator<Product> LEAST_SOLD_FIRST = Comparator.comparing(p -> p.quantitySold);
        public static final Comparator<Product> MOST_SOLD_FIRST = LEAST_SOLD_FIRST.reversed();

        public static Comparator<Product> getComparator(COMPARATOR_TYPE comparatorType) {
            switch (comparatorType) {
                case NEWEST_FIRST:
                    return NEWEST_FIRST;
                case OLDEST_FIRST:
                    return OLDEST_FIRST;
                case LOWEST_PRICE_FIRST:
                    return LOWEST_PRICE_FIRST;
                case HIGHEST_PRICE_FIRST:
                    return HIGHEST_PRICE_FIRST;
                case MOST_SOLD_FIRST:
                    return MOST_SOLD_FIRST;
                default:
                    return LEAST_SOLD_FIRST;
            }
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", createdAt=" + createdAt +
                    ", price=" + price +
                    ", quantitySold=" + quantitySold +
                    '}';
        }
    }


    static class OnlineShop {
        private final Map<String, Product> productMap;

        OnlineShop() {
            productMap = new HashMap<>();
        }

        void addProduct(String category, String id, String name, LocalDateTime createdAt, double price){
            productMap.put(id, new Product(category, id, name, createdAt, price));
        }

        double buyProduct(String id, int quantity) throws ProductNotFoundException {
            if (!productMap.containsKey(id))
                throw new ProductNotFoundException(
                        String.format("Product with id %s does not exist in the online shop!", id)
                );
            return productMap.get(id).buy(quantity);
        }

        List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
            List<List<Product>> result = new ArrayList<>();

            Comparator<Product> comparator = Product.getComparator(comparatorType);
            Predicate<Product> filter = p -> category == null || p.category.equals(category);
            List<Product> sorted = productMap.values().stream()
                    .filter(filter)
                    .sorted(comparator).collect(Collectors.toList());

            int size = productMap.size();
            int pages = size / pageSize; // колку страници има
            pages = size % pageSize > 0 ? pages + 1 : pages; // ако има остаток при делење

            IntStream.range(0, pages).forEach(i -> {
                int start = i * pageSize;
                int limit = Math.min(pageSize, size - start);

                List<Product> page = sorted.stream()
                        .skip(start).limit(limit)
                        .collect(Collectors.toList());

                if (!page.isEmpty())
                    result.add(page);
            });

            return result;
        }

    }

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category=null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }
}


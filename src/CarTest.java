import java.util.*;
import java.util.stream.Collectors;

public class CarTest {
    static class Car {
        private final String manufacturer;
        private final String model;
        private final int price;
        private final float power;

        Car(String manufacturer, String model, int price, float power) {
            this.manufacturer = manufacturer;
            this.model = model;
            this.price = price;
            this.power = power;
        }

        @Override
        public String toString() {
            return String.format("%s %s (%.0fKW) %d", manufacturer, model, power, price);
        }

        public int getPrice() {
            return price;
        }

        public float getPower() {
            return power;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getModel() {
            return model;
        }

        public static final Comparator<Car> BY_PRICE = Comparator.comparing(Car::getPrice);
        public static final Comparator<Car> BY_POWER = Comparator.comparing(Car::getPower);
        public static final Comparator<Car> BY_MODEL = Comparator.comparing(Car::getModel);
    }

    static class CarCollection {
        private final List<Car> collection;

        public CarCollection() {
            this.collection = new ArrayList<>();
        }

        public void addCar(Car car) {
            collection.add(car);
        }

        public void sortByPrice(boolean ascending) {
            Comparator<Car> c = Car.BY_PRICE.thenComparing(Car.BY_POWER);
            c = ascending ? c : c.reversed();
            collection.sort(c);
        }
        // подредување на колекцијата по цената на автомобилот
        // (во растечки редослед ако аргументот ascending е true, во спротивно, во опаѓачки редослед).
        // Ако цената на автомобилите е иста, сортирањето да се направи според нивната моќноста.

        public List<Car> filterByManufacturer(String manufacturer) {
            return collection.stream()
                    .filter(c -> c.getManufacturer().equalsIgnoreCase(manufacturer))
                    .sorted(Car.BY_MODEL)
                    .collect(Collectors.toList());
        }
        // враќа листа со автомобили од одреден производител (споредбата е според името на
        // производителот без да се води сметка за големи и мали букви во името).
        // Автомобилите во оваа листата треба да бидат подредени според моделот во растечки редослед.

        public List<Car> getList() {
            return collection;
        }
        // ја враќа листата со автомобили од колекцијата.

    }
    public static void main(String[] args) {
        CarCollection carCollection = new CarCollection();
        String manufacturer = fillCollection(carCollection);
        carCollection.sortByPrice(true);
        System.out.println("=== Sorted By Price ASC ===");
        print(carCollection.getList());
        carCollection.sortByPrice(false);
        System.out.println("=== Sorted By Price DESC ===");
        print(carCollection.getList());
        System.out.printf("=== Filtered By Manufacturer: %s ===\n", manufacturer);
        List<Car> result = carCollection.filterByManufacturer(manufacturer);
        print(result);
    }

    static void print(List<Car> cars) {
        cars.forEach(System.out::println);
    }

    static String fillCollection(CarCollection cc) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            if(parts.length < 4) return parts[0];
            Car car = new Car(parts[0], parts[1], Integer.parseInt(parts[2]),
                    Float.parseFloat(parts[3]));
            cc.addCar(car);
        }
        scanner.close();
        return "";
    }
}


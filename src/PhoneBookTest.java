import java.util.*;
import java.util.stream.Collectors;

public class PhoneBookTest {
    static class DuplicateNumberException extends Exception {
        public DuplicateNumberException(String number) {
            super(String.format("Duplicate number: %s", number));
        }
    }

    static class Contact {
        private String name;
        private String number;

        public Contact(String name, String number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }

        public static final Comparator<Contact> SORTER =
                Comparator.comparing(Contact::getName).thenComparing(Contact::getNumber);

        @Override
        public String toString() {
            return String.format("%s %s", name, number);
        }
    }
    static class PhoneBook {
        private final Map<String, String> map;

        PhoneBook() {
            map = new HashMap<>();
        }

        /**
         * додава нов контакт во именикот.
         * Ако се обидеме да додадеме контакт со веќе постоечки број,
         * треба да се фрли исклучок од класа DuplicateNumberException.
         * Комплексноста на овој метод не треба да надминува O(log N) за N контакти.
         * @param name
         * @param number
         */
        void addContact(String name, String number) throws DuplicateNumberException {
            if (map.containsKey(number))
                throw new DuplicateNumberException(number);
            map.put(number, name);
        }

        private void helper(List<Contact> list) {
            if (list.isEmpty()){
                System.out.println("NOT FOUND");
                return;
            }
            list.sort(Contact.SORTER);
            list.forEach(System.out::println);
        }
        /**
         * ги печати сите контакти кои во бројот го содржат бројот пренесен како аргумент
         * во методот (минимална должина на бројот [number] е 3).
         * Комплексноста на овој метод не треба да надминува O(log N) за N контакти.
         * контактите се печатат сортирани лексикографски според името,
         * а оние со исто име потоа според бројот
         * @param number
         */
        void contactsByNumber(String number) {
            List<Contact> list = map.keySet().stream()
                    .filter(k -> k.contains(number))
                    .map(k -> new Contact(map.get(k), k))
                    .collect(Collectors.toList());

            helper(list);
        }

        /**
         * ги печати сите контакти кои даденото име.
         * Комплексноста на овој метод треба да биде O(1).
         * контактите се печатат сортирани лексикографски според името,
         * а оние со исто име потоа според бројот
         * @param name
         */
        void contactsByName(String name) {
            List<Contact> list = map.keySet().stream()
                    .filter(k -> map.get(k).equals(name))
                    .map(k -> new Contact(map.get(k), k))
                    .collect(Collectors.toList());

            helper(list);
        }
    }
    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}

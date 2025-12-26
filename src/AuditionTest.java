import java.util.*;

public class AuditionTest {
    static class Participant implements Comparable<Participant> {
        private final String code;
        private final String name;
        private final int age;

        Participant(String code, String name, int age) {
            this.code = code;
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return String.format("%s %s %d", code, name, age);
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public static final Comparator<Participant> BY_NAME_THEN_AGE =
                Comparator.comparing(Participant::getName).thenComparing(Participant::getAge);


        @Override
        public int compareTo(Participant o) {
            return BY_NAME_THEN_AGE.compare(this, o);
        }
    }

    static class Audition {
        private final Map<String, Map<String, Participant>> map; // city -> people

        public Audition() {
            map = new HashMap<>();
        }

        void addParticipant(String city, String code, String name, int age) {
            Map<String, Participant> cityMap = map.computeIfAbsent(city, k -> new TreeMap<>(
                    Comparator.naturalOrder()
            ));
            if (cityMap.containsKey(code))
                return;
            cityMap.put(code, new Participant(code, name, age));
        }

        void listByCity(String city) {
            map.get(city).values().stream().sorted().forEach(System.out::println);
        }
        // ги печати сите кандидати од даден град подредени според името, а ако е исто според возраста
        // (комплексноста на овој метод не треба да надминува O(n*log_2(n)),
        // каде n е бројот на кандидати во дадениот град).

    }

    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticipant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NamesTest {
    static class Names {
        private final Map<String, Integer> map;

        Names() {
            map = new TreeMap<>();
        }

        public int uniqueChars(String name) {
            String word = name.toLowerCase();
            Set<Character> set = new HashSet<>();
            IntStream.range(0, word.length()).forEach(i -> set.add(word.charAt(i)));
            return set.size();
        }

        public void addName(String name) {
            map.putIfAbsent(name, 0);
            map.put(name, map.get(name) + 1);
        }

        public void printN(int n) {
            Predicate<String> cond = k -> map.get(k) >= n;
            map.keySet().stream().filter(cond)
                    .forEach(k -> System.out.printf("%s (%d) %d\n", k, map.get(k), uniqueChars(k)));
        }
        // ги печати сите имиња кои се појавуваат n или повеќе пати,
        // подредени лексикографски според името, на крајот на зборот во загради се печати бројот на појавувања,
        // а по него на крај бројот на уникатни букви во зборот (не се прави разлика на големи и мали)

        public String findName(int len, int x) {
            Predicate<String> cond = k -> k.length() < len;
            List<String> filtered = map.keySet().stream().filter(cond).collect(Collectors.toList());
            x %= filtered.size();
            return filtered.get(x);
        }
        // го враќа името кое со наоѓа на позиција x (почнува од 0) во листата од уникатни имиња подредени
        // лексикографски, по бришење на сите имиња со големина поголема или еднаква на len.
        // Позицијата x може да биде поголема од бројот на останати имиња, во тој случај
        // се продожува со броење од почетокот на листата.
        // Пример за листа со 3 имиња A, B, C, ако x = 7, се добива B. A0, B1, C2, A3, B4, C5, A6, B7.

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}

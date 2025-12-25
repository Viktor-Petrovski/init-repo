import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class F1Test {
    static class Driver implements Comparable<Driver> {
        private final String name;
        private final LocalTime l1;
        private final LocalTime l2;
        private final LocalTime l3;

        private Driver(String name, LocalTime l1, LocalTime l2, LocalTime l3) {
            this.name = name;
            this.l1 = l1;
            this.l2 = l2;
            this.l3 = l3;
        }

        private static LocalTime parser(String input) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("m:ss:SSS") // single 'm' allows 1 or 2 digits
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .toFormatter();

            return LocalTime.parse(input, formatter);
        }

        public static Driver create(String input) {
            String[] tokens = input.split("\\s+");

            String name = tokens[0];
            LocalTime l1 = parser(tokens[1]);
            LocalTime l2 = parser(tokens[2]);
            LocalTime l3 = parser(tokens[3]);

            return new Driver(name, l1, l2, l3);
        }

        private LocalTime bestLap() {
            LocalTime best = l1;
            best = best.isBefore(l2) ? best : l2;
            return best.isBefore(l3) ? best : l3;
        }

        @Override
        public String toString() {
            return String.format("%-10s  %s",
                    name, bestLap().toString().substring(4)).replace(".", ":");
        }

        @Override
        public int compareTo(Driver o) {
            return this.bestLap().compareTo(o.bestLap());
        }
    }

    static class F1Race {
        private final List<Driver> driverList;

        F1Race() {
            driverList = new ArrayList<>();
        }

        void readResults() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().map(Driver::create).forEach(driverList::add);
        }

        void printSorted() {
            PrintWriter pw = new PrintWriter(System.out);
            driverList.sort(Comparator.naturalOrder());
            IntStream.range(0, driverList.size()).forEach(i -> {
                pw.printf("%d. %s\n", i + 1, driverList.get(i));
                //1. Hamilton    1:56:074
            });
            pw.flush();
        }
        // метод кој ги печати сите пилоти сортирани според нивното најдобро време (најкраткото време од нивните 3 последни круга) во формат Driver_name best_lap со 10 места за името на возачот (порамнето од лево) и 10 места за времето на најдобриот круг порамнето од десно. Притоа времето е во истиот формат со времињата кои се читаат.
    }

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults();
        f1Race.printSorted();
    }

}
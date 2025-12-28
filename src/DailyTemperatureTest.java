import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DailyTemperatureTest {
    interface Measurement {
        Double value();

        Measurement convert();
    }

    static class Celsius implements Measurement {
        private final Double value;

        Celsius(Double value) {
            this.value = value;
        }

        public static Measurement create(String s) {
            return new Celsius(Double.parseDouble(s.substring(0, s.length() - 1)));
        }

        @Override
        public Double value() {
            return value;
        }

        @Override
        public Measurement convert() {
            return new Fahrenheit((value * 9 / 5) + 32);
        }
    }

    static class Fahrenheit implements Measurement {
        private final Double value;

        Fahrenheit(Double value) {
            this.value = value;
        }

        public static Measurement create(String s) {
            return new Fahrenheit(Double.parseDouble(s.substring(0, s.length() - 1)));
        }

        @Override
        public Double value() {
            return value;
        }

        @Override
        public Measurement convert() {
            return new Celsius((value - 32) * 5 / 9);
        }
    }

    static class Day {
        private final int day;
        private final List<Measurement> measurementList;
        private final char scale;

        private Day(int day, List<Measurement> measurementList, char scale) {
            this.day = day;
            this.measurementList = measurementList;
            this.scale = scale;
        }

        public char getScale() {
            return scale;
        }

        public static Day create(String s) {
            String[] tokens = s.split("\\s+");
            int day = Integer.parseInt(tokens[0]);
            List<Measurement> measurementList = (s.contains("F")) ?
                    Arrays.stream(tokens).skip(1).map(Fahrenheit::create).collect(Collectors.toList()) :
                    Arrays.stream(tokens).skip(1).map(Celsius::create).collect(Collectors.toList());
            char scale = (s.contains("F")) ? 'F' : 'C';

            return new Day(day, measurementList, scale);
        }

        public String toString(char requestedScale) {
            List<Measurement> displayList = measurementList;

            if (this.scale != requestedScale) {
                displayList = measurementList.stream()
                        .map(Measurement::convert)
                        .collect(Collectors.toList());
            }

            int count = displayList.size();
            double min = displayList.stream().mapToDouble(Measurement::value).min().orElse(0.0);
            double max = displayList.stream().mapToDouble(Measurement::value).max().orElse(0.0);
            double avg = displayList.stream().mapToDouble(Measurement::value).average().orElse(0.0);

            return String.format("%3d: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c",
                    day, count, min, requestedScale, max, requestedScale, avg, requestedScale);
        }

        public static final Comparator<Day> BY_DAY = Comparator.comparing(d -> d.day);
    }

    static class DailyTemperatures {
        private final List<Day> dayList;

        DailyTemperatures() {
            dayList = new ArrayList<>();
        }

        void readTemperatures(InputStream is) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            br.lines().map(Day::create).forEach(dayList::add);
        }

        void writeDailyStats(OutputStream os, char scale) {
            PrintWriter pw = new PrintWriter(os);
            dayList.stream()
                    .sorted(Day.BY_DAY)
                    .forEach(d -> System.out.println(d.toString(scale)));
            pw.flush();
        }

    }

    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}


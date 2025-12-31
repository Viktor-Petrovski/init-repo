import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class StudentRecordsTest {

    static class Record implements Comparable<Record> {
        private final String code;
        private final String dir;
        private final List<Integer> grades;

        private Record(String code, String dir, List<Integer> grades) {
            this.code = code;
            this.dir = dir;
            this.grades = grades;
        }

        public double getAvg() {
            return grades.stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);
        }

        public String getCode() {
            return code;
        }

        public String getDir() {
            return dir;
        }

        public List<Integer> getGrades() {
            return grades;
        }

        public static Record create(String s) {
            String[] tokens = s.split("\\s+");

            String code = tokens[0];
            String dir = tokens[1];

            List<Integer> grades = Arrays.stream(tokens)
                    .skip(2)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            return new Record(code, dir, grades);
        }

        @Override
        public String toString() {
            return String.format("%s %.2f", code, getAvg());
        }

        @Override
        public int compareTo(Record o) {
            int cmp = Double.compare(o.getAvg(), this.getAvg());
            if (cmp != 0) return cmp;
            return this.getCode().compareTo(o.getCode());
        }
    }

    static class StudentRecords {
        private final List<Record> recordList = new ArrayList<>();

        int readRecords() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().filter(line -> !line.isBlank())
                    .map(Record::create)
                    .forEach(recordList::add);

            return recordList.size();
        }

        private Map<String, List<Record>> groupedByDir(List<Record> list) {
            return list.stream()
                    .collect(Collectors.groupingBy(
                            Record::getDir,
                            TreeMap::new,
                            Collectors.toList()
                    ));
        }

        void writeTable() {
            Map<String, List<Record>> grouped = groupedByDir(recordList);

            grouped.forEach((dir, records) -> {
                System.out.println(dir);
                records.stream()
                        .sorted()
                        .forEach(System.out::println);
            });
        }

        static class Direction implements Comparable<Direction> {
            private final String name;
            private final List<Integer> grades;
            private final long tens;

            Direction(String name, List<Record> recordList) {
                this.name = name;
                this.grades = recordList.stream()
                        .flatMap(r -> r.getGrades().stream())
                        .collect(Collectors.toList());
                this.tens = grades.stream().filter(g -> g == 10).count();
            }

            private Map<Integer, Long> getDistribution() {
                return grades.stream()
                        .collect(Collectors.groupingBy(
                                Integer::intValue,
                                TreeMap::new,
                                Collectors.counting()
                        ));
            }

            @Override
            public int compareTo(Direction o) {
                return Long.compare(o.tens, this.tens);
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(name).append("\n");

                getDistribution().forEach((grade, count) -> {
                    int stars = (int) (count / 10);
                    stars = count % 10 != 0 ? stars + 1 : stars;

                    sb.append(String.format(
                            "%2d | %s(%d)%n",
                            grade, "*".repeat(stars), count
                    ));
                });

                return sb.toString();
            }
        }

//        void writeDistribution() {
//            PrintWriter pw = new PrintWriter(System.out);
//
//            groupedByDir(recordList).forEach(
//                    (k, v) -> pw.print(new Direction(k, v))
//            );
//
//            pw.flush();
//        }
    void writeDistribution() {
        PrintWriter pw = new PrintWriter(System.out);

        List<Direction> list = groupedByDir(recordList).entrySet()
                .stream()
                .map(e -> new Direction(e.getKey(), e.getValue()))
                .sorted()
                .collect(Collectors.toList());

        list.forEach(pw::print);
        pw.flush();
}

    }

    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();

        int total = studentRecords.readRecords();
        System.out.printf("Total records: %d%n", total);

        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable();

        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution();
    }
}

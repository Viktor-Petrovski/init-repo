import java.util.*;
import java.util.stream.Collectors;

public class LabExercisesTest {
    static class Student {
        private final static int BARE_MINIMUM = 8;
        private final static int CURRENT_YEAR = 20;

        private final String index;
        private final List<Integer> points;

        Student(String index, List<Integer> points) {
            this.index = index;
            this.points = points;
        }

        private double summaryPoints() {
            return points.stream().mapToInt(Integer::intValue).sum() / 10.0;
        }

        public static final Comparator<Student> BY_INDEX =
                Comparator.comparing(s -> s.index);
        public static final Comparator<Student> BY_POINTS =
                Comparator.comparing(Student::summaryPoints);

        public boolean isPassed() {
            return points.size() >= BARE_MINIMUM;
        }

        public boolean isFailed() {
            return !isPassed();
        }

        @Override
        public String toString() {
            String res = isFailed() ? "NO" : "YES";
            return String.format("%s %s %.2f", index, res, summaryPoints());
        }

        public int getYear() {
            int id = Integer.parseInt(index.substring(0, 2));
            return Math.abs(id - CURRENT_YEAR);
        }
    }

    static class LabExercises {
        private final List<Student> studentList;

        LabExercises() {
            studentList = new ArrayList<>();
        }

        public void addStudent (Student student) {
            studentList.add(student);
        }

        public void printByAveragePoints (boolean ascending, int n) {
            Comparator<Student> c = Student.BY_POINTS.thenComparing(Student.BY_INDEX);
            c = ascending ? c : c.reversed();
            studentList.stream().sorted(c).limit(n).forEach(System.out::println);
        }

        public List<Student> failedStudents () {
            Comparator<Student> c = Student.BY_INDEX.thenComparing(Student.BY_POINTS);
            return studentList.stream().filter(Student::isFailed).sorted(c)
                    .collect(Collectors.toList());
        }

        public Map<Integer,Double> getStatisticsByYear() {
            List<Student> list = studentList.stream().filter(Student::isPassed)
                    .collect(Collectors.toList());

            return list.stream().collect(Collectors.groupingBy(
                    Student::getYear,
                    TreeMap::new,
                    Collectors.averagingDouble(Student::summaryPoints)
            ));
        }
        // метод којшто враќа мапа од просекот од сумарните поени на студентите
        // според година на студирање. Да се игнорираат студентите кои не добиле потпис.

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}
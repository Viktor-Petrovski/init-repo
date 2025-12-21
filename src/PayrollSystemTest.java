import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class PayrollSystemTest {
    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Collection<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }

    abstract static class Employee {
        protected final String id;
        protected final String level;

        public Employee(String id, String level) {
            this.id = id;
            this.level = level;
        }

        abstract double getSalary();

        public String getLevel() {
            return level;
        }

    }

    static class EmployeeFactory {
        public Employee create(String s) {
            if (s.startsWith("F"))
                return new FreelanceEmployeeFactory().create(s);
            return new HourlyEmployeeFactory().create(s);
        }
    }

    static class HourlyEmployeeFactory extends EmployeeFactory {
        public HourlyEmployee create(String s) {
            String[] tokens = s.split(";");
            String id = tokens[1];
            String level = tokens[2];
            double hours = Double.parseDouble(tokens[3]);
            return new HourlyEmployee(id, level, hours);
        }
    }

    static class FreelanceEmployeeFactory extends EmployeeFactory {
        public FreelanceEmployee create(String s) {
            String[] tokens = s.split(";");
            String id = tokens[1];
            String level = tokens[2];
            List<Integer> points = Arrays.stream(tokens).skip(3)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return new FreelanceEmployee(id, level, points);
        }

    }

    static class HourlyEmployee extends Employee {
        private static final int REGULAR_HOURS = 40;

        private final double hours;

        public HourlyEmployee(String id, String level, double hours) {
            super(id, level);
            this.hours = hours;
        }

        @Override
        double getSalary() {
            double reg = regularHours();
            double over = overtimeHours();
            return (reg + over * 1.5) * PayrollSystem.hourlyRateByLevel.get(level);

        }

        public double regularHours() {
            return hours >= REGULAR_HOURS ? REGULAR_HOURS : hours;
        }

        public double overtimeHours() {
            return hours >= REGULAR_HOURS ? hours - REGULAR_HOURS : 0;
        }

        @Override
        public String toString() {
            return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f",
                    id, level, getSalary(), regularHours(), overtimeHours());
        }
    }

    static class FreelanceEmployee extends Employee {
        private final List<Integer> points;

        public FreelanceEmployee(String id, String level, List<Integer> points) {
            super(id, level);
            this.points = points;
        }

        private double ticketPoints() {
            return points.stream().mapToDouble(Integer::doubleValue).sum();
        }

        @Override
        double getSalary() {
            return ticketPoints() * PayrollSystem.ticketRateByLevel.get(level);
        }

        @Override
        public String toString() {
            return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d",
                    id, level, getSalary(), points.size(), (int) ticketPoints());
        }

    }

    static class PayrollSystem {
        public static Map<String, Double> hourlyRateByLevel;
        public static Map<String, Double> ticketRateByLevel;
        private final List<Employee> employees;

        public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
            PayrollSystem.hourlyRateByLevel = hourlyRateByLevel;
            PayrollSystem.ticketRateByLevel = ticketRateByLevel;
            employees = new ArrayList<>();
        }

        public void readEmployees(InputStream is) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            EmployeeFactory factory = new EmployeeFactory();
            br.lines().map(factory::create).forEach(employees::add);
        }

        public Map<String, Collection<Employee>> printEmployeesByLevels(OutputStream os, Set<String> levels) {
            Predicate<Employee> condition = e -> levels.contains(e.level);
            Map<String, Collection<Employee>> map = employees.stream().filter(condition)
                    .collect(Collectors.groupingBy(
                            Employee::getLevel,
                            TreeMap::new,
                            Collectors.toCollection(ArrayList::new)
                    ));
            map.values().forEach(c -> {
                List<Employee> list = new ArrayList<>(c);
                list.sort(Comparator.comparing(Employee::getSalary).reversed());
                c.clear();
                c.addAll(list);
            });

            return map;
        }
    }

}

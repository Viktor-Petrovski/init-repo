import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

enum Level {
    IC, //individual contributor
    M, //middle management
    C //C-Level executives
}

class Employee {
    String name;
    String jobTitle;
    Level level;

    public Employee(String name, String jobTitle, Level level) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.level = level;
    }

    @Override
    public String toString() {
        return String.format(
                "Employee: name=%s, title=%s, level=%s",
                name,
                jobTitle,
                level.toString()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(name, employee.name) && Objects.equals(jobTitle, employee.jobTitle) && level == employee.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, jobTitle, level);
    }
}

class Item {
    String name;
    String category;
    double price;

    public Item(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %.2f USD", name, category, price);
    }

}

class Receipt {
    String merchant;
    LocalDateTime date;
    List<Item> items;

    public Receipt(String merchant, LocalDateTime date, List<Item> items) {
        this.merchant = merchant;
        this.date = date;
        this.items = items;
    }

    double totalAmount() {
        return items.stream().mapToDouble(i -> i.price).sum();
    }

    @Override
    public String toString() {
        String itemsStr = items.stream()
                .map(Item::toString)
                .collect(Collectors.joining("; "));

        return String.format(
                "Receipt: merchant=%s, date=%s, items=%s, total=%.2f USD",
                merchant,
                date,
                itemsStr,
                totalAmount()
        );
    }

}

class DailyExpensesPerCountry {
    static Map<String, Double> ALLOWANCE = new HashMap<>();

    static {
        ALLOWANCE.put("US", 50.0);
        ALLOWANCE.put("MK", 10.0);
        ALLOWANCE.put("PT", 30.0);

        ALLOWANCE.put("DE", 45.0);   // Germany
        ALLOWANCE.put("AT", 40.0);   // Austria
        ALLOWANCE.put("CH", 55.0);   // Switzerland
        ALLOWANCE.put("FR", 50.0);   // France
        ALLOWANCE.put("IT", 40.0);   // Italy
        ALLOWANCE.put("ES", 35.0);   // Spain
        ALLOWANCE.put("UK", 50.0);   // United Kingdom
        ALLOWANCE.put("NL", 45.0);   // Netherlands
        ALLOWANCE.put("BE", 45.0);   // Belgium
        ALLOWANCE.put("SE", 50.0);   // Sweden
        ALLOWANCE.put("NO", 55.0);   // Norway
        ALLOWANCE.put("DK", 50.0);   // Denmark
        ALLOWANCE.put("PL", 25.0);   // Poland
        ALLOWANCE.put("CZ", 25.0);   // Czech Republic
        ALLOWANCE.put("SK", 20.0);   // Slovakia
        ALLOWANCE.put("HU", 20.0);   // Hungary
        ALLOWANCE.put("HR", 25.0);   // Croatia
        ALLOWANCE.put("BG", 20.0);   // Bulgaria
        ALLOWANCE.put("RO", 20.0);   // Romania
        ALLOWANCE.put("GR", 30.0);   // Greece
        ALLOWANCE.put("RS", 15.0);   // Serbia
        ALLOWANCE.put("AL", 15.0);   // Albania
        ALLOWANCE.put("TR", 20.0);   // Türkiye

        ALLOWANCE.put("CA", 45.0);   // Canada
        ALLOWANCE.put("MX", 25.0);   // Mexico
        ALLOWANCE.put("BR", 20.0);   // Brazil
        ALLOWANCE.put("AR", 18.0);   // Argentina
        ALLOWANCE.put("CL", 22.0);   // Chile

        ALLOWANCE.put("AU", 50.0);   // Australia
        ALLOWANCE.put("NZ", 40.0);   // New Zealand

        ALLOWANCE.put("JP", 45.0);   // Japan
        ALLOWANCE.put("CN", 30.0);   // China
        ALLOWANCE.put("KR", 35.0);   // South Korea
        ALLOWANCE.put("SG", 50.0);   // Singapore
        ALLOWANCE.put("IN", 20.0);   // India
        ALLOWANCE.put("AE", 45.0);   // UAE (Dubai)
        ALLOWANCE.put("SA", 30.0);   // Saudi Arabia
    }
}



public class ExpensesManagementSystemTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Float maxReceiptAmount = Float.parseFloat(sc.nextLine());

        // Create system with some default max amount
        ExpenseManagementSystem system = new ExpenseManagementSystem(maxReceiptAmount);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("END")) break;
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            String method = parts[0];


            switch (method) {

                case "addReceiptExpense": {
                    // Format:
                    // addReceiptExpense;Name;Job;IC|M|C;description;amount;merchant;datetime;item|cat|price,...
                    String empName = parts[1];
                    String job = parts[2];
                    Level lvl = Level.valueOf(parts[3]);
                    String description = parts[4];

                    String merchant = parts[5];
                    LocalDateTime dt = LocalDateTime.parse(parts[6]);

                    // Items list
                    String itemsRaw = parts[7];
                    List<Item> items = new ArrayList<>();
                    if (!itemsRaw.equalsIgnoreCase("none")) {
                        for (String itemStr : itemsRaw.split(",")) {
                            String[] ip = itemStr.split("\\|");
                            items.add(new Item(ip[0], ip[1], Double.parseDouble(ip[2])));
                        }
                    }

                    Employee e = new Employee(empName, job, lvl);
                    Receipt r = new Receipt(merchant, dt, items);

                    try {
                        system.addReceiptExpense(e, description, r);
                    } catch (NotSupportedExpenseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }

                case "addTravelExpense": {
                    // Format:
                    // addTravelExpense;Name;Job;IC|M|C;description;amount;start;end;country
                    String empName = parts[1];
                    String job = parts[2];
                    Level lvl = Level.valueOf(parts[3]);
                    String description = parts[4];
                    double amount = Double.parseDouble(parts[5]);
                    LocalDateTime start = LocalDateTime.parse(parts[6]);
                    LocalDateTime end = LocalDateTime.parse(parts[7]);
                    String country = parts[8];

                    Employee e = new Employee(empName, job, lvl);

                    try {
                        system.addTravelExpense(e, description, amount, start, end, country);
                    } catch (NotSupportedExpenseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }

                case "printRefunds": {
                    system.printRefunds();
                    break;
                }

                case "totalRefundsPerEmployee": {
                    Map<Employee, Double> map = system.totalRefundsPerEmployee();
                    map.forEach((emp, total) ->
                            System.out.printf("%s -> %.2f%n", emp.name, total));
                    break;
                }

                default:
                    System.out.println("Unknown method: " + method);
            }
        }
    }

    static class NotSupportedExpenseException extends Exception {
        public NotSupportedExpenseException(String message) {
            super(message);
        }
    }

    static class Wrapper {
        // A custom data structure for storing an Employee with the total money spent from that Employee
        // Ја добив идејата од еден од дизајн патерните шо ги презентираше газдата на Нетцетера (Декоратор?)
        private final Employee employee;
        private final double totalAmount;

        Wrapper(Employee employee, double totalAmount) {
            this.employee = employee;
            this.totalAmount = totalAmount;
        }

        @Override
        public String toString() {
            return "employee=" + employee +
                    ", totalAmount=" + totalAmount;
        }

        private double getTotalAmount() {
            return totalAmount;
        }

        public static final Comparator<Wrapper> BY_TOTAL_AMT_DESC =
                Comparator.comparing(Wrapper::getTotalAmount).reversed();
    }
    static class ExpenseManagementSystem {
        private final float maxReceiptAmount;
        private final List<Expense> expenses;

        ExpenseManagementSystem(float maxReceiptAmount) {
            this.maxReceiptAmount = maxReceiptAmount;
            expenses = new ArrayList<>();
        }

        public void addTravelExpense(
                Employee e, String description, double amount,
                LocalDateTime start, LocalDateTime end, String country
        ) throws NotSupportedExpenseException {
            if (amount > maxReceiptAmount)
                throw new NotSupportedExpenseException("totalAmount exceeds the limit.");
            TravelExpense te = new TravelExpense(e, description, amount, start, end, country);
            expenses.add(te);
        }

        public void addReceiptExpense(Employee e, String description, Receipt r) throws NotSupportedExpenseException {
            RegularExpense re = new RegularExpense(e, description, r);

            if (expenses.stream().anyMatch(expense -> expense.isDuring(r.date)))
                throw new NotSupportedExpenseException("Can not refund during travel.");
            if (r.totalAmount() > maxReceiptAmount)
                throw new NotSupportedExpenseException("totalAmount exceeds the limit.");

            expenses.add(re);
        }

        public void printRefunds() {
            Map<Employee, Double> init = totalRefundsPerEmployee();

            List<Wrapper> res = new ArrayList<>();
            init.keySet().forEach(k -> res.add(new Wrapper(k, init.get(k))));

            res.sort(Wrapper.BY_TOTAL_AMT_DESC);
            res.forEach(System.out::println);

            // метод кој ги печати трошоците на вработените во форматот како во тест примерите, подредени според
            // износот кој компанијата ќе го рефундира за реализираниот трошок, во опаѓачки редослед.
        }

        public Map<Employee, Double> totalRefundsPerEmployee() {
            // метод кој враќа мапа во која клучеви се сите вработени,
            // а вредности се вкупните износи кои им се исплатени за рефундација на трошоци на соодветните вработени.
            return expenses.stream().collect(Collectors.groupingBy(Expense::getE, Collectors.summingDouble(Expense::totalAmount)));
        }
    }

    static abstract class Expense {
        protected final Employee e;
        protected final String description;

        Expense(Employee e, String description) {
            this.e = e;
            this.description = description;
        }

        public abstract double totalAmount();
        public abstract boolean isDuring(LocalDateTime ldt);

        public Employee getE() {
            return e;
        }
    }

    public static class TravelExpense extends Expense {
        private final double amount;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final String country;

        public TravelExpense(
                Employee e, String description, double amount,
                LocalDateTime start, LocalDateTime end, String country) {
            super(e, description);
            this.amount = amount;
            this.start = start;
            this.end = end;
            this.country = country;
        }

        private long travelDays() {
            return Duration.between(start, end).toDays();
        }

        @Override
        public double totalAmount() {
            return amount + DailyExpensesPerCountry.ALLOWANCE.get(country) * travelDays();
        }
        //      За патни трошоци: На вработениот му следува рефундирање на целиот износ пријавен за трошокот плус дневница за
        //      секој поминат ден во државата согласно дневниците дефинирани во мапата DailyExpensesPerCountry.ALLOWANCE


        public boolean isDuring(LocalDateTime ldt) {
            return ldt.isEqual(start) || ldt.isEqual(end) ||
                    (ldt.isAfter(start) && ldt.isBefore(end));
        }
    }

    public static class RegularExpense extends Expense {
        private final Receipt r;

        RegularExpense(Employee e, String description, Receipt r) {
            super(e, description);
            this.r = r;
        }

        @Override
        public double totalAmount() {
            BiPredicate<Item, Employee> canBeRefunded = (i, e) -> {
                if (e.level.equals(Level.C))
                    return true;
                if (e.level.equals(Level.IC))
                    return i.category.equalsIgnoreCase("food") ||
                            i.category.equalsIgnoreCase("non-alcohol beverage");
                if (e.level.equals(Level.M))
                    return i.category.equalsIgnoreCase("food") ||
                            i.category.equalsIgnoreCase("non-alcohol beverage") ||
                            i.category.equalsIgnoreCase("transport") ||
                            i.category.equalsIgnoreCase("alcohol beverage");
                return false;
                //          Вработените од ниво IC имаат право да купуваат само ставки од категориите food и non-alcohol beverage.
                //          Вработените од ниво M имаат право и на ставки од категориите transport и alcohol beverage.
                //          Вработените од ниво C немаат ограничувања на категории.
            };
            return r.items.stream().filter(i -> canBeRefunded.test(i, e)).mapToDouble(i -> i.price).sum();
        }
        //      За трошоци направени со фискална сметка: Компанијата ќе ги прегледа сите ставки од фискалната сметка и ќе ги
        //      рефундира само ставките од категориите за кои вработениот има право да прави трошоци.

        @Override
        public boolean isDuring(LocalDateTime ldt) {
            // го ставив тука овој метод кој секогаш враќа неточно поради тоа шо
            // кога итерирам листа од трошоци за да проверам за исклучокот,
            // trade-off правам така шо во друг случај ќе требаше
            // да проверувам дали објектот е инстанца од другата класа, шо на час кажавте дека е лоша пракса
            return false;
        }
    }
}

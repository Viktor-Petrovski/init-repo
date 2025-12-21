import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerTest {
    public static class Task {
        private final String category;
        private final String name;
        private final String description;
        private LocalDateTime time = null;
        private int priority = Integer.MIN_VALUE;


        private Task(String category, String name, String description, LocalDateTime time, int priority)
                throws DeadlineNotValidException {
            if (time.isAfter(LocalDateTime.parse("2020-06-01T23:59:59")))
                throw new DeadlineNotValidException();
            this.category = category;
            this.name = name;
            this.description = description;
            this.time = time;
            this.priority = priority;
        }

        private Task(String category, String name, String description, int priority) {
            this.category = category;
            this.name = name;
            this.description = description;
            this.priority = priority;
        }

        private Task(String category, String name, String description, LocalDateTime time)
                throws DeadlineNotValidException {
            if (time.isAfter(LocalDateTime.parse("2020-06-01T23:59:59")))
                throw new DeadlineNotValidException();
            this.category = category;
            this.name = name;
            this.description = description;
            this.time = time;
        }

        private Task(String category, String name, String description) {
            this.category = category;
            this.name = name;
            this.description = description;
        }



        public static Task create(String s) throws DeadlineNotValidException {
            String[] tokens = s.split(",");

            String category = tokens[0];
            String name = tokens[1];
            String description = tokens[2];

            if (tokens.length == 5) {
                LocalDateTime time = LocalDateTime.parse(tokens[3]);
                int priority = Integer.parseInt(tokens[4]);
                return new Task(category, name, description, time, priority);
            } else if (tokens.length == 4) {
                if (tokens[3].length() < 5)
                    return new Task(category, name, description, Integer.parseInt(tokens[3]));
                return new Task(category, name, description, LocalDateTime.parse(tokens[3]));
            }
            return new Task(category, name, description);
        }



        @Override
        public String toString() {
            if (priority == Integer.MIN_VALUE)
                return "Task{" +
                        "category='" + category + '\'' +
                        ", name='" + name + '\'' +
                        ", description='" + description + '\'' +
                        ", time=" + time +
                        '}';
            if (time == null)
                return "Task{" +
                        "category='" + category + '\'' +
                        ", name='" + name + '\'' +
                        ", description='" + description + '\'' +
                        ", priority=" + priority +
                        '}';
            return "Task{" +
                    "category='" + category + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", time=" + time +
                    ", priority=" + priority +
                    '}';
        }
    }

    public static class DeadlineNotValidException extends Exception {
        public DeadlineNotValidException() {
            super("The deadline 2020-06-01T23:59:59 has already passed");
        }
    }
    public static class TaskManager {
        private final List<Task> tasks;

        public TaskManager() {
            tasks = new ArrayList<>();
        }

        void readTasks () throws DeadlineNotValidException{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(s -> {
                try {
                    Task ins = Task.create(s);
                    tasks.add(ins);
                } catch (DeadlineNotValidException e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        void printTasks(boolean includePriority, boolean includeCategory) {
            PrintWriter pw = new PrintWriter(System.out);
        }

        // Доколку includeCategory e true да се испечатат задачите групирани според категории,
        // во спротивно се печатат сите внесени задачи

        // Доколку includePriority e true да се испечатат задачите сортирани според приоритетот
        // (при што 1 е највисок приоритет), a немаат приоритет или имаат ист приоритет се
        // сортираат растечки според временското растојание помеѓу рокот и моменталниот датум,
        // односно задачите со рок најблизок до денешниот датум се печатат први.

        // Доколку includePriority e false се печатат во растечки редослед
        // според временското растојание помеѓу рокот и моменталниот датум.

        // При печатењето на задачите се користи default опцијата за toString (доколку работите вo IntelliJ),
        // со тоа што треба да внимавате на името на променливите.

    }

    public static void main(String[] args) throws DeadlineNotValidException {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks();
        System.out.println("By categories with priority");
        manager.printTasks(true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(true, false);
        System.out.println("-------------------------");

    }
}

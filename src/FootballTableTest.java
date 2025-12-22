import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class FootballTableTest {
    /**
     * Да се имплементира класа FootballTable за обработка од податоците за повеќе
     * фудбласки натпревари од една лига и прикажување на табелата на освоени поени
     * според резултатите од натпреварите. Во класата да се имплементираат:
     *
     */

    static class Club {
        private final String name;
        private int goalsGiven;
        private int goalsTaken;
        private int wonMatches;
        private int tieMatches;
        private int totalMatches;

        Club(String name) {
            this.name = name;
            goalsGiven = 0;
            goalsTaken = 0;
            wonMatches = 0;
            tieMatches = 0;
            totalMatches = 0;
        }

        public void update(int t, int o) { // this - other
            totalMatches++;
            goalsGiven += t;
            goalsTaken += o;
            if (t > o)
                wonMatches++;
            if (t == o)
                tieMatches++;
        }

        private static final int WON_COEFFICIENT = 3;
        private static final int TIE_COEFFICIENT = 1;

        public int points() {
            return wonMatches * WON_COEFFICIENT + tieMatches * TIE_COEFFICIENT;
        }

        public int goalDifference() {
            return goalsGiven - goalsTaken;
        }

        public String getName() {
            return name;
        }

        public static final Comparator<Club> BY_POINTS_DESC = Comparator.comparing(Club::points).reversed();
        public static final Comparator<Club> BY_GOALS_DESC = Comparator.comparing(Club::goalDifference).reversed();
        public static final Comparator<Club> BY_NAME = Comparator.comparing(Club::getName);

        public static final Comparator<Club> SORTER =
                BY_POINTS_DESC.thenComparing(BY_GOALS_DESC).thenComparing(BY_NAME);

        @Override
        public String toString() {
            int lostMatches = totalMatches - wonMatches - tieMatches;
            return String.format("%-15s%5d%5d%5d%5d%5d",
                    name, totalMatches, wonMatches, tieMatches, lostMatches, points());
        }
    }

    static class FootballTable {
        private final Map<String, Club> clubs;

        FootballTable() {
            clubs = new HashMap<>();
        }

        /**
         * метод за додавање податоци за одигран натпревар помеѓу тимот со име homeTeam (домашен тим)
         * и тимот со име awayTeam (гостински тим), при што homeGoals претставува бројот на постигнати
         * голови од домашниот тим, а awayGoals бројот на постигнати голови од гостинскиот тим.
         */
        public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
            Club home = clubs.computeIfAbsent(homeTeam, k -> new Club(homeTeam));
            Club away = clubs.computeIfAbsent(awayTeam, _k-> new Club(awayTeam));

            home.update(homeGoals, awayGoals);
            away.update(awayGoals, homeGoals);
        }

        /**
         * метод за печатење на табелата според одиграните (внесените) натпревари.
         * Во табелата се прикажуваат редниот број на тимот во табелата, името (со 15 места порамнето во лево),
         * бројот на одиграни натпревари, бројот на победи, бројот на нерешени натпревари,
         * бројот на освоени поени (сите броеви се печатат со 5 места порамнети во десно).
         * Бројот на освоени поени се пресметува како број_на_победи x 3 + број_на_нерешени x 1.
         * Тимовите се подредени според бројот на освоени поени во опаѓачки редослед,
         * ако имаат ист број на освоени поени според гол разликата
         * (разлика од постигнатите голови и примените голови) во опаѓачки редослед,
         * а ако имаат иста гол разлика, според името.
         */
        public void printTable() {
            List<Club> list = clubs.values().stream().sorted(Club.SORTER)
                    .collect(Collectors.toList());

            IntStream.range(0, list.size()).forEach(i -> {
                System.out.printf("%2d. %s\n", i + 1, list.get(i));
            });
        }
    }
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

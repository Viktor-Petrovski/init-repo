import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.IntStream;

public class StadiumTest {
    static class Sector {
        private final String name;
        private final int size;
        private final Boolean[] seat; // false зафатено - true слободно
        private boolean ticketBought;
        private boolean isHome;

        Sector(String name, int size) {
            this.name = name;
            this.size = size;
            seat = new Boolean[size];
            IntStream.range(0, size).forEach(i -> seat[i] = true);
            ticketBought = false;
            isHome = false;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public boolean isTicketBought() {
            return ticketBought;
        }

        public boolean isHome() {
            return isHome;
        }

        public void setTicketBought(boolean ticketBought) {
            this.ticketBought = ticketBought;
        }

        public void setIsHome(boolean home) {
            isHome = home;
        }

        public boolean seatAvailable(int pos) {
            return seat[pos];
        }

        public void buySeat(int pos) {
            seat[pos] = false;
        }

        private long numSeatsAvailable() {
            return Arrays.stream(seat).filter(i -> i == true).count();
        }

        private double percentAvailable() {
            return (double) numSeatsAvailable() / size;
        }

        @Override
        public String toString() {
            return String.format("%s\t%d/%d\t%.1f%%", name, numSeatsAvailable(), size, 100 - percentAvailable() * 100);
        }

        // сите сектори сортирани според бројот на слободни места во опаѓачки редослед
        // (ако повеќе сектори имаат ист број на слободни места, се подредуваат според името).
        public static final Comparator<Sector> BY_AVAILABLE_DESC_THEN_NAME =
                Comparator.comparing(Sector::numSeatsAvailable).reversed().thenComparing(Sector::getName);
    }

    static class SeatTakenException extends Exception {
    }

    static class SeatNotAllowedException extends Exception {
    }

    static class Stadium {
        private final String name; // no use case
        private final Map<String, Sector> map;

        Stadium(String name) {
            this.name = name;
            map = new HashMap<>();
        }

        void createSectors(String[] sectorNames, int[] sizes) {
            IntStream.range(0, sizes.length)
                    .forEach(i -> map.put(sectorNames[i], new Sector(sectorNames[i], sizes[i])));
        }
        // креирање на сектори со имиња String[] sectorNames и број на места int[] sizes
        // (двете низи се со иста големина)

        void buyTicket(String sectorName, int seat, int type)
                throws SeatTakenException, SeatNotAllowedException {
            Sector ins = map.get(sectorName);

            if (!ins.seatAvailable(seat - 1)|| seat > ins.getSize() || seat < 1)
                throw new SeatTakenException();

            if (!ins.isTicketBought()) {
                ins.setTicketBought(true);
                ins.setIsHome(type == 1);
            } else {
                if ((ins.isHome() && type == 2) || (!ins.isHome() && type == 1))
                    throw new SeatNotAllowedException();
            }

            ins.buySeat(seat - 1);
        }
        // за купување билет од проследениот тип (type, 0 - неутрален, 1 - домашен, 2 - гостински),
        // во секторот sectorName со број на место seat (местото секогаш е со вредност во опсег 1 - size).
        // Ако местото е зафатено (претходно е купен билет на ова место) се фрла исклучок од вид SeatTakenException.

        // Исто така ако се обидеме да купиме билет од тип 1, во сектор во кој веќе има купено билет од
        // тип 2 (и обратно) се фрла исклучок од вид SeatNotAllowedException.

        void showSectors() {
            map.values().stream().sorted(Sector.BY_AVAILABLE_DESC_THEN_NAME).forEach(System.out::println);
        }
        // ги печати сите сектори сортирани според бројот на слободни места во опаѓачки редослед
        // (ако повеќе сектори имаат ист број на слободни места, се подредуваат според името).

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}

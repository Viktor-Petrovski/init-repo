import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MojDDVTest {
    static class AmountNotAllowedException extends Exception {
        public AmountNotAllowedException(int sum) {
            super(String.format("Receipt with amount %d is not allowed to be scanned", sum));
        }
    }
    static class Receipt {
        private static final int MAX_AMOUNT = 30_000;
        private final int id;
        private int totalAmount;
        private double taxReturn;

        public Receipt(String input) throws AmountNotAllowedException {
            String[] tokens = input.split("\\s+");
            id = Integer.parseInt(tokens[0]);
            totalAmount = 0;
            taxReturn = 0;

            for (int i = 1; i < tokens.length; i += 2) {
                int amt = Integer.parseInt(tokens[i]);
                char type = tokens[i+1].charAt(0);
                totalAmount += amt;

                if (type == 'A')
                    taxReturn += amt *.15 *.18;
                if (type == 'B')
                    taxReturn += amt *.15 *.05;

            }

            if (totalAmount > MAX_AMOUNT)
                throw new AmountNotAllowedException(totalAmount);
        }

        public double getTaxReturn() {
            return taxReturn;
        }

        @Override
        public String toString() {
            return String.format("%10d\t     %5d\t%10.5f", id, totalAmount, taxReturn);
        }
    }
    static class MojDDV {
        private final List<Receipt> receipts;

        MojDDV() {
            this.receipts = new ArrayList<>();
        }

        void readRecords(InputStream is) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            br.lines().forEach(s -> {
                try {
                    Receipt r = new Receipt(s);
                    receipts.add(r);
                } catch (AmountNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        void printTaxReturns (OutputStream os) {
            PrintWriter pw = new PrintWriter(os);
            receipts.forEach(pw::println);
            pw.flush();
        }

        void printStatistics (OutputStream os) {
            PrintWriter pw = new PrintWriter(os);
            double min = receipts.stream().min(Comparator.comparing(Receipt::getTaxReturn)).get().getTaxReturn();
            double max = receipts.stream().max(Comparator.comparing(Receipt::getTaxReturn)).get().getTaxReturn();
            double sum = receipts.stream().mapToDouble(Receipt::getTaxReturn).sum();
            long cnt = receipts.size();
            double avg = receipts.stream().mapToDouble(Receipt::getTaxReturn).average().orElse(0);

            pw.println(String.format("min:\t%.3f", min));
            pw.println(String.format("max:\t%.3f", max));
            pw.println(String.format("sum:\t%.3f", sum));
            pw.println(String.format("count:\t%d", cnt));
            pw.println(String.format("avg:\t%.3f", avg));
            /*
            min:	29.225
max:	145.509
sum:	726.325
count:	9
avg:	80.703
             */
            pw.flush();

        }
    }
    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);

    }
}
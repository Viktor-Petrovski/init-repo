import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WordVectorsTest {
    static class WordVectors {
        private final Map<String, List<Integer>> map;
        private List<String> readWords;
        /**
         * конструктор за иницијализација со зборови и нивната соодветна репрезентација во вектор од 5 цели броеви (со вредност од 0-9). За секој стринг од низата words соодветствува една листа од 5 цели броеви (негова векторска репрезентација).
         * @param words
         * @param vectors
         */
        public WordVectors(String[] words, List<List<Integer>> vectors) {
            map = new HashMap<>();

            IntStream.range(0, words.length)
                    .forEach(i -> map.put(words[i], vectors.get(i)));
        }

        /**
         * се вчитува листа од зборови од некој текст за кој треба да се пресмета векторска репрезентација.
         * @param words
         */
        public void readWords(List<String> words) {
            readWords = words;
        }

        /**
         * пресметува векторска репрезентација на вчитаниот текст (листа со зборови) користејќи подвижен прозорец (sliding window) со големина n. Прозорец претставуваат n соседни зборови, при што се започнува со обработување од првиот збор (позиција 0) и ги вклучува зборовите од 0 до (n - 1). Потоа овој прозорец се придвижува една позиција на десно, односно од 1 до n, итн. За секој прозорец од n елементи се добива по еден скалар (цел број) на тој начин што се собираат векторите на сите зборови и од збирниот вектор се наоѓа максималната вредност. Пример за векторите на зборовите quiz и attempt:
         * quiz = [1, 5, 7] и attempt = [3, 1, 4] се добива збирен вектор [1 + 3, 5 + 1, 7 + 4] = [4, 6, 11] со максимална вредност 11.
         *
         * Ако за одреден збор не постои векторска репрезентација, се користи неутрална вредност [5, 5, 5, 5, 5].
         * @param n
         * @return
         */
        public List<Integer> slidingWindow(int n) {
            return IntStream.range(0, readWords.size() - n + 1)
                    .map(i -> {
                        int[] sum = new int[5];

                        IntStream.range(i, i + n)
                                .mapToObj(j -> map.getOrDefault(
                                        readWords.get(j),
                                        List.of(0, 0, 0, 0, 0)))
                                .forEach(vec -> {
                                    for (int k = 0; k < 5; k++) {
                                        sum[k] += vec.get(k);
                                    }
                                });

                        return Arrays.stream(sum).max().orElse(0);
//                        int[] sum = new int[5];
//
//                        IntStream.range(i, i + n)
//                                .mapToObj(j -> map.getOrDefault(
//                                        readWords.get(j),
//                                        List.of(0, 0, 0, 0, 0)))
//                                .forEach(vec -> {
//                                    for (int k = 0; k < 5; k++) {
//                                        sum[k] += vec.get(k);
//                                    }
//                                });
//
//                        return Arrays.stream(sum).max().orElse(0);
                    })
                    .boxed()
                    .collect(Collectors.toList());
        }


    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] words = new String[n];
        List<List<Integer>> vectors = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            words[i] = parts[0];
            List<Integer> vector = Arrays.stream(parts[1].split(":"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            vectors.add(vector);
        }
        n = scanner.nextInt();
        scanner.nextLine();
        List<String> wordsList = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            wordsList.add(scanner.nextLine());
        }
        WordVectors wordVectors = new WordVectors(words, vectors);
        wordVectors.readWords(wordsList);
        n = scanner.nextInt();
        List<Integer> result = wordVectors.slidingWindow(n);
        System.out.println(result.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));
        scanner.close();
    }
}




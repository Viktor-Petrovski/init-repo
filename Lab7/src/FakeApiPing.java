import java.util.*;
import java.util.concurrent.*;

public class FakeApiPing {

    // Result holder
    public static class ApiResult {
        public final int requestId;
        public final boolean success;
        public final String value;

        public ApiResult(int requestId, boolean success, String value) {
            this.requestId = requestId;
            this.success = success;
            this.value = value;
        }

        @Override
        public String toString() {
            return "ApiResult{" +
                    "requestId=" + requestId +
                    ", success=" + success +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class Api {
        public static ApiResult get(int requestId, int parameter) throws InterruptedException {
            long delayMillis = parameter * 100L;
            Thread.sleep(delayMillis);

            String response = "VALUE_" + parameter;
            return new ApiResult(requestId, true, response);
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt(); // number of API calls

        List<Callable<ApiResult>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int parameter = sc.nextInt();

            // requestId is the loop index
            int requestId = i+1;
            tasks.add(() -> Api.get(requestId, parameter));
            //TODO add a Callable that invokes the API get method in the tasks list
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        //TODO submit all callables to the executor and get the Futures
        List<Future<ApiResult>> futures = new ArrayList<>();
        tasks.forEach(t -> futures.add(executor.submit(t)));

        List<ApiResult> results = new ArrayList<>();
        futures.forEach(f -> {
            try {
                results.add(f.get());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        long timeoutMillis = 200;

        // todo implemented
        for (int i = 0; i < futures.size(); i++) {
            Future<ApiResult> f = futures.get(i);
            int requestId = i + 1;

            try {
                results.add(f.get(timeoutMillis, TimeUnit.MILLISECONDS));
            } catch (TimeoutException e) {
                f.cancel(true);
                results.add(new ApiResult(requestId, false, "TIMEOUT"));
            } catch (ExecutionException | InterruptedException e) {
                results.add(new ApiResult(requestId, false, "FAILED"));
            }
        }

        // executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);

        //TODO get the ApiResult from all the futures and allow a max timeout of timeoutMillis

        executor.shutdown();

        // Sorting by requestId
        results.sort(Comparator.comparingInt(r -> r.requestId));

        // Output
        for (ApiResult r : results) {
            System.out.printf(
                    "%d %s %s%n",
                    r.requestId,
                    r.success ? "OK" : "FAILED",
                    r.value
            );
        }
    }
}

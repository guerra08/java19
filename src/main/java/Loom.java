import jdk.incubator.concurrent.StructuredTaskScope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Loom {

    public static void helloWorld() {
        Thread.startVirtualThread(() -> System.out.println("Hello from Loom!"));
    }

    public static void executorService() {
        System.out.println("Before");
        try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
            es.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("First task");
            });
            es.submit(() -> System.out.println("Second task"));
        }
        System.out.println("After");
    }

    public static Integer structuredTaskScope() {
        try (var scope = new StructuredTaskScope<Integer>()) {
            var startTime = System.currentTimeMillis();

            var getFirstResult = scope.fork(() -> {
                Thread.sleep(1000);
                return 400;
            });

            var getSecondResult = scope.fork(() -> {
                Thread.sleep(2000);
                return 20;
            });

            scope.join();

            var totalTime = System.currentTimeMillis() - startTime;

            System.out.println("Total in ms: " + totalTime);

            return getFirstResult.resultNow() + getSecondResult.resultNow();
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getUser() {
        try (var executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            var user = executorService.submit(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return "Bruno";
            });
            return user.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getPhone() {
        try (var executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            var phone = executorService.submit(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return "(51)12345-6789";
            });
            return phone.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Each call takes 3000ms, but since they are executed in parallel, it only takes 3000ms to complete
    public static String getDetails() {
        try (var executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            var user = executorService.submit(Loom::getUser);
            var phone = executorService.submit(Loom::getPhone);
            return user.get() + " - " + phone.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String authenticate() {
        try (var es = Executors.newVirtualThreadPerTaskExecutor()) {
            var authResult = es.submit(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                var chance = new Random().nextDouble();
                if (chance > 0.5) {
                    throw new IllegalStateException("Forbidden");
                }
                return "User1";
            });
            return authResult.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Double placeOrder(String auth) {
        try (var es = Executors.newVirtualThreadPerTaskExecutor()) {
            var total = es.submit(() -> {
                try {
                    Thread.sleep(666);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new Random().nextDouble() * 1000.0;
            });
            return total.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String authenticateAndPlaceOrder() {
        var auth = authenticate();
        var orderTotal = placeOrder(auth);
        return auth + " spent " + orderTotal + " USD";
    }

    public static List<String> visitReddit() {
        var requests = buildSubredditRequests();
        var startTime = System.currentTimeMillis();
        var contents = getSubredditsContents(requests);
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Took " + totalTime + "ms.");
        return contents;
    }

    private static List<String> getSubredditsContents(List<HttpRequest> requests) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var httpClient = HttpClient.newHttpClient();
            var tasks = requests.stream()
                .map(request ->
                    scope.fork(() -> {
                        System.out.println("GET " + request.uri());
                        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                    })
                )
                .toList();
            scope.join();
            return tasks.stream()
                .map(Future::resultNow)
                .toList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<HttpRequest> buildSubredditRequests() {
        try (
            var stream = Loom.class.getResourceAsStream("subreddits.txt");
            var br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)))
        ) {
            return br
                .lines()
                .map(url -> {
                    try {
                        var uri = new URI(url);
                        return HttpRequest.newBuilder(uri).GET().build();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}

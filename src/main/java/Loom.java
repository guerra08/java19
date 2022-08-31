import jdk.incubator.concurrent.StructuredTaskScope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        try(var scope = new StructuredTaskScope<Integer>()) {
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
        try (var executorService = Executors.newVirtualThreadPerTaskExecutor()){
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
        try (var executorService = Executors.newVirtualThreadPerTaskExecutor()){
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
        try(var executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            var user = executorService.submit(Loom::getUser);
            var phone = executorService.submit(Loom::getPhone);
            return user.get() + " - " + phone.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
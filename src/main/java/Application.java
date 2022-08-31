public class Application {

    public static void main(String[] args) {
        System.out.println("This feels empty...");
    }

    private static void userDetailsExample() {
        var startTime = System.currentTimeMillis();
        var details = Loom.getDetails();
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.println(details);
        System.out.println("Took " + totalTime + "ms");
    }

}

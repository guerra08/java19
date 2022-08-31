public class Application {

    public static void main(String[] args) {
        userDetailsExample();
//        var result = Loom.structuredTaskScope();
//        System.out.println(result);
    }

    private static void userDetailsExample() {
        var startTime = System.currentTimeMillis();
        var details = Loom.getDetails();
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.println(details);
        System.out.println("Took " + totalTime + "ms");
    }

}

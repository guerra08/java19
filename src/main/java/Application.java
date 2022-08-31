public class Application {

    public static void main(String[] args) {
        //userDetailsExample();
        var result = Loom.structuredTaskScope();
        System.out.println(result);
    }

    private static void userDetailsExample() {
        var start = System.currentTimeMillis();
        var details = Loom.getDetails();
        var end = System.currentTimeMillis();
        System.out.println(details);
        System.out.println("Took " + (end - start) + "ms");
    }

}

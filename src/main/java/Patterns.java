public class Patterns {

    sealed interface User {}

    record Person (String name, String email, String currentClass) implements User {}
    record Teacher (String name, String discipline) implements User {}

    public static void patternDemo() {
        var teacherUser = new Teacher("Juca", "Math");
        var personUser = new Person("Bruno", "email@gmail.com", "Class 1");

        var teacherInfo = getInformation(teacherUser);
        var personInfo = getInformation(personUser);

        var isEmailGmail = hasEmail(personUser, "gmail");

        System.out.println(isEmailGmail);
        System.out.println(teacherInfo);
        System.out.println(personInfo);
    }

    private static String getInformation(User user) {
        return switch (user) {
            case Person p -> p.currentClass;
            case Teacher t -> t.discipline;
        };
    }

    private static boolean hasEmail(Person person, String domain) {
        return switch (person) {
            case Person(String name, String email, String currentClass) p
                when email.contains(domain) -> true;
            case default -> false;
        };
    }

}

public class Patterns {

    sealed interface User {}
    record Person (String name, String email, String currentClass) implements User {}
    record Teacher (String name, String discipline) implements User {}

    public static void patternDemo() {
        var teacherUser = new Teacher("Juca", "Math");
        var personUser = new Person("Bruno", "email@gmail.com", "Class 1");

        var teacherInfo = getInformation(teacherUser);
        var personInfo = getInformation(personUser);

        System.out.println(teacherInfo);
        System.out.println(personInfo);
    }

    private static String getInformation(User user) {
        return switch (user) {
            case Person(String name, String email, String currentClass) -> "Person is enrolled in " + currentClass + " class";
            case Teacher(String name, String discipline) -> "Teacher is responsible for " + discipline;
        };
    }

}

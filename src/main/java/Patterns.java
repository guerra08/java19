import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Patterns {

    private static final Logger logger = LoggerFactory.getLogger(Patterns.class);

    sealed interface User {}
    record Student (String name, String email, String currentClass) implements User {}
    record Professor (String name, String discipline) implements User {}

    public static void patternDemo() {
        var teacherUser = new Professor("Juca", "Math");
        var personUser = new Student("Bruno", "email@gmail.com", "Class 1");

        logger.info(greet(teacherUser));
        logger.info(greet(personUser));
    }

    private static String greet(User user) {
        return switch (user) {
            case Professor p -> "Welcome, professor %s".formatted(p.name);
            case Student s -> "Welcome, student %s".formatted(s.name);
        };
    }

}

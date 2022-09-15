package reddit_visitor.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class Reader {

    public static List<String> resourceFileAsList(String fileName) {
        try (
            var stream = Reader.class.getClassLoader().getResourceAsStream(fileName);
            var br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)))
        ) {
            return br.lines().toList();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}

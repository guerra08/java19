package reddit_visitor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class Reader {

    private static final Logger logger = LoggerFactory.getLogger(Reader.class);

    public static List<String> resourceFileAsList(String fileName) {
        try (
            var stream = Reader.class.getClassLoader().getResourceAsStream(fileName);
            var br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)))
        ) {
            return br.lines().toList();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return List.of();
        }
    }

}

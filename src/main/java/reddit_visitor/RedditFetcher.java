package reddit_visitor;

import jdk.incubator.concurrent.StructuredTaskScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reddit_visitor.page.RedditErrorPage;
import reddit_visitor.page.RedditPage;
import reddit_visitor.page.RedditResultPage;
import reddit_visitor.util.Reader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RedditFetcher {

    private final HttpClient httpClient;
    private static final Logger logger = LoggerFactory.getLogger(RedditFetcher.class);

    public RedditFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<RedditPage> visitReddit() {
        var requests = mapRequests();
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var tasks = requests.stream()
                .map(request -> {
                    logger.info("visitReddit - Fetching {}", request.uri());
                    return scope.fork(() -> doGet(httpClient, request));
                })
                .toList();
            scope.join();
            return tasks.stream()
                .map(redditPageFuture -> {
                    var result = redditPageFuture.resultNow();
                    result.runMatching(
                        redditResultPage -> logger.info("visitReddit - Fetched {}", redditResultPage.url()),
                        redditErrorPage -> logger.info("visitReddit - Error when fetching {}", redditErrorPage.url())
                    );
                    return result;
                })
                .toList();
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException("Error when visiting Reddit.");
        }
    }

    private RedditPage doGet(HttpClient httpClient, HttpRequest request) {
        try {
            var contents = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return new RedditResultPage(contents, request.uri().toString());
        } catch (IOException | InterruptedException ex) {
            logger.error(ex.getMessage());
            return new RedditErrorPage(ex.getMessage(), request.uri().toString());
        }
    }

    private List<HttpRequest> mapRequests() {
        return Reader.resourceFileAsList("subreddits.txt").stream()
            .map(url -> {
                var uri = URI.create(url);
                return HttpRequest.newBuilder(uri).GET().build();
            })
            .toList();
    }

}

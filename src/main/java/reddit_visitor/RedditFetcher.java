package reddit_visitor;

import jdk.incubator.concurrent.StructuredTaskScope;
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
import java.util.logging.Logger;

public class RedditFetcher {

    private final HttpClient httpClient;
    private final Logger logger;

    public RedditFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.logger = Logger.getLogger(RedditFetcher.class.getName());
    }

    public List<RedditPage> visitReddit() {
        var requests = mapRequests();
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var tasks = requests.stream()
                .map(request -> {
                    logger.info("visitReddit - Fetching %s".formatted(request.uri()));
                    return scope.fork(() -> doGet(httpClient, request));
                })
                .toList();
            scope.join();
            return tasks.stream()
                .map(redditPageFuture -> {
                    var result = redditPageFuture.resultNow();
                    result.runMatching(
                        redditResultPage -> logger.info("visitReddit - Fetched %s".formatted(redditResultPage.url())),
                        redditErrorPage -> logger.info("visitReddit - Error when fetching %s".formatted(redditErrorPage.url()))
                    );
                    return result;
                })
                .toList();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error when visiting Reddit.");
        }
    }

    private RedditPage doGet(HttpClient httpClient, HttpRequest request) {
        try {
            var contents = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return new RedditResultPage(contents, request.uri().toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new RedditErrorPage(e.getMessage(), request.uri().toString());
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

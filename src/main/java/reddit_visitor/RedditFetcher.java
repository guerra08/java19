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
import java.util.concurrent.Future;

public class RedditFetcher {

    private final HttpClient httpClient;

    public RedditFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<RedditPage> visitReddit() {
        var requests = Reader.resourceFileAsList("subreddits.txt").stream()
            .map(url -> {
                var uri = URI.create(url);
                return HttpRequest.newBuilder(uri).GET().build();
            })
            .toList();
        try(var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var tasks = requests.stream()
                .map(request -> scope.fork(() -> doGet(httpClient, request)))
                .toList();
            scope.join();
            return tasks.stream()
                .map(Future::resultNow)
                .toList();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private RedditPage doGet(HttpClient httpClient, HttpRequest request) {
        try {
            var contents = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return new RedditResultPage(contents, request.uri().toString());
        } catch (IOException | InterruptedException e) {
            return new RedditErrorPage(e.getMessage(), request.uri().toString());
        }
    }

}

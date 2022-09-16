import reddit_visitor.RedditFetcher;

import java.net.http.HttpClient;

public class Application {

    public static void main(String[] args) {
        var httpClient = HttpClient.newHttpClient();
        var redditFetcher = new RedditFetcher(httpClient);
        redditFetcher.visitReddit();
        System.out.println();
    }

}

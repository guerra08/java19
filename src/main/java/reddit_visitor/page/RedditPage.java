package reddit_visitor.page;

import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface RedditPage permits RedditErrorPage, RedditResultPage {
    boolean isError();
    boolean isSuccess();
    <T> T map(
        Function<RedditResultPage, T> resultPageMapper,
        Function<RedditErrorPage, T> errorPageMapper
    );

    void runMatching(
        Consumer<RedditResultPage> resultPageConsumer,
        Consumer<RedditErrorPage> redditErrorPageConsumer
    );
}

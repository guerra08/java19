package reddit_visitor.page;

import java.util.function.Consumer;
import java.util.function.Function;

public record RedditErrorPage(String error, String url) implements RedditPage {
    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public <T> T map(Function<RedditResultPage, T> resultPageMapper, Function<RedditErrorPage, T> errorPageMapper) {
        return errorPageMapper.apply(this);
    }

    @Override
    public void runMatching(Consumer<RedditResultPage> resultPageConsumer, Consumer<RedditErrorPage> redditErrorPageConsumer) {
        redditErrorPageConsumer.accept(this);
    }
}

package reddit_visitor.page;

import java.util.function.Consumer;
import java.util.function.Function;

public record RedditResultPage(String contents, String url) implements RedditPage {
    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public <T> T map(Function<RedditResultPage, T> resultPageMapper, Function<RedditErrorPage, T> errorPageMapper) {
        return resultPageMapper.apply(this);
    }

    @Override
    public void runMatching(Consumer<RedditResultPage> resultPageConsumer, Consumer<RedditErrorPage> redditErrorPageConsumer) {
        resultPageConsumer.accept(this);
    }
}

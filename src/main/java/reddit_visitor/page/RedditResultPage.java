package reddit_visitor.page;

public record RedditResultPage(String contents, String url) implements RedditPage {
    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}

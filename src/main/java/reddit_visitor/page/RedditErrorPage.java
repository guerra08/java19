package reddit_visitor.page;

public record RedditErrorPage(String error, String url) implements RedditPage {
    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}

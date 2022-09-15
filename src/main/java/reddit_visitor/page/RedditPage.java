package reddit_visitor.page;

public sealed interface RedditPage permits RedditErrorPage, RedditResultPage {
    boolean isError();
    boolean isSuccess();
}

package model;

public class NewsArticle {
    private String title;
    private String author;
    private String source;
    private String description;
    private String url;
    private String publishedAt;

    public NewsArticle(String title, String author, String source,
                       String description, String url, String publishedAt) {
        this.title = title;
        this.author = author;
        this.source = source;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getSource() {
        return source;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", source='" + source + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }
}
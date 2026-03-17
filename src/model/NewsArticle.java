package model;

public class NewsArticle {

    private String title;
    private String source;
    private String author;
    private String description;
    private String url;
    private String publishedAt;

    public NewsArticle(String title, String source, String author, String description, String url, String publishedAt) {
        this.title = title;
        this.source = source;
        this.author = author;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }
}
package model;

public class NewsArticle {

    private String title;
    private String author;
    private String source;
    private String description;
    private String url;
    private String publishedAt;
    private int score;

    public NewsArticle(String title, String author, String source, String description, String url, String publishedAt) {
        this.title = title;
        this.author = author;
        this.source = source;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
        this.score = 0;
    }

    public NewsArticle(String title, String author, String source, String description, String url, String publishedAt, int score) {
        this.title = title;
        this.author = author;
        this.source = source;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return title + " | " + source + " | Score: " + score;
    }
}

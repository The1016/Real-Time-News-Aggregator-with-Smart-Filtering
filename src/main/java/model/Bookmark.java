package model;

public class Bookmark {

    private NewsArticle article;
    private String savedAt;

    public Bookmark(NewsArticle article, String savedAt) {
        this.article = article;
        this.savedAt = savedAt;
    }

    public NewsArticle getArticle() {
        return article;
    }

    public void setArticle(NewsArticle article) {
        this.article = article;
    }

    public String getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(String savedAt) {
        this.savedAt = savedAt;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "article=" + article +
                ", savedAt='" + savedAt + '\'' +
                '}';
    }
}

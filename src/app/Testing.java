package app;

import model.NewsArticle;

public class Testing{
    public static void main(String[] args) {
        NewsArticle article1 = new NewsArticle(
                "AI is transforming software development",
                "Reuters",
                "Jane Smith",
                "A new report explains how AI tools are changing programming workflows.",
                "https://example.com/article1",
                "2026-03-18"
        );

        NewsArticle article2 = new NewsArticle(
                "NASA prepares for a new lunar mission",
                "BBC",
                "Michael Brown",
                "NASA is entering the next phase of its lunar exploration program.",
                "https://example.com/article2",
                "2026-03-17"
        );

        System.out.println(article1);
        System.out.println(article2);

        System.out.println(article1.getTitle());
        System.out.println(article2.getSource());

        article1.setAuthor("Updated Author");
        System.out.println(article1.getAuthor());
    }
}
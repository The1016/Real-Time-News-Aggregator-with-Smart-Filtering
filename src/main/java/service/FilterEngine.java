package service;

import model.NewsArticle;

import java.util.ArrayList;
import java.util.List;

public class FilterEngine {

    /**
     * Filters a list of articles by checking if the keyword appears
     * in the article's title or description (case-insensitive).
     *
     * @param articles the full list of articles to filter
     * @param keyword  the search term to match
     * @return a new list containing only matching articles
     */
    public List<NewsArticle> filterByKeyword(List<NewsArticle> articles, String keyword) {
        List<NewsArticle> filtered = new ArrayList<>();

        if (articles == null || keyword == null || keyword.trim().isEmpty()) {
            return filtered;
        }

        String lowerKeyword = keyword.trim().toLowerCase();

        for (NewsArticle article : articles) {
            String title = article.getTitle() == null ? "" : article.getTitle().toLowerCase();
            String description = article.getDescription() == null ? "" : article.getDescription().toLowerCase();

            if (title.contains(lowerKeyword) || description.contains(lowerKeyword)) {
                filtered.add(article);
            }
        }

        return filtered;
    }

    /**
     * Filters a list of articles by exact source name match (case-insensitive).
     * Passing null or empty string returns the original list unchanged.
     *
     * @param articles the full list of articles to filter
     * @param source   the source name to match exactly
     * @return a new list containing only articles from that source
     */
    public List<NewsArticle> filterBySource(List<NewsArticle> articles, String source) {
        List<NewsArticle> filtered = new ArrayList<>();

        if (articles == null || source == null || source.trim().isEmpty()) {
            return filtered;
        }

        String lowerSource = source.trim().toLowerCase();

        for (NewsArticle article : articles) {
            String articleSource = article.getSource() == null ? "" : article.getSource().toLowerCase();

            if (articleSource.equals(lowerSource)) {
                filtered.add(article);
            }
        }

        return filtered;
    }
}

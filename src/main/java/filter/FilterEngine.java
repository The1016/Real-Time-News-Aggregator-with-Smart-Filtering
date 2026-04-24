package filter;

import model.NewsArticle;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class FilterEngine {

    public List<NewsArticle> applyFilters(List<NewsArticle> articles, String keyword, String source) {
        if (articles == null) {
            return new ArrayList<>();
        }

        List<NewsArticle> filteredArticles = new ArrayList<>(articles);

        if (hasKeyword(keyword)) {
            filteredArticles = filterByKeyword(filteredArticles, keyword);
        }

        if (hasSource(source)) {
            filteredArticles = filterBySource(filteredArticles, source);
        }

        return filteredArticles;
    }

    public List<NewsArticle> filterByKeyword(List<NewsArticle> articles, String keyword) {
        List<NewsArticle> filteredArticles = new ArrayList<>();

        if (articles == null || !hasKeyword(keyword)) {
            return filteredArticles;
        }

        String searchText = keyword.trim().toLowerCase();

        for (NewsArticle article : articles) {
            String title = safeText(article.getTitle()).toLowerCase();
            String description = safeText(article.getDescription()).toLowerCase();

            if (title.contains(searchText) || description.contains(searchText)) {
                filteredArticles.add(article);
            }
        }

        return filteredArticles;
    }

    public List<NewsArticle> filterBySource(List<NewsArticle> articles, String source) {
        List<NewsArticle> filteredArticles = new ArrayList<>();

        if (articles == null || !hasSource(source)) {
            return filteredArticles;
        }

        String selectedSource = source.trim().toLowerCase();

        for (NewsArticle article : articles) {
            String articleSource = safeText(article.getSource()).toLowerCase();

            if (articleSource.equals(selectedSource)) {
                filteredArticles.add(article);
            }
        }

        return filteredArticles;
    }

    public TreeSet<String> getAvailableSources(List<NewsArticle> articles) {
        TreeSet<String> sources = new TreeSet<>();

        if (articles == null) {
            return sources;
        }

        for (NewsArticle article : articles) {
            String source = article.getSource();

            if (source != null && !source.isBlank()) {
                sources.add(source);
            }
        }

        return sources;
    }

    public String buildFilterContext(String keyword, String source) {
        boolean keywordUsed = hasKeyword(keyword);
        boolean sourceUsed = hasSource(source);

        if (keywordUsed && sourceUsed) {
            return "keyword \"" + keyword.trim() + "\" from \"" + source + "\"";
        }

        if (keywordUsed) {
            return "keyword \"" + keyword.trim() + "\"";
        }

        if (sourceUsed) {
            return "source \"" + source + "\"";
        }

        return "current results";
    }

    public boolean hasAnyFilter(String keyword, String source) {
        return hasKeyword(keyword) || hasSource(source);
    }

    private boolean hasKeyword(String keyword) {
        return keyword != null && !keyword.trim().isEmpty();
    }

    private boolean hasSource(String source) {
        return source != null
                && !source.trim().isEmpty()
                && !source.equals("All Sources");
    }

    private String safeText(String text) {
        return text == null ? "" : text;
    }
}
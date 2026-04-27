package filter;

import model.NewsArticle;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class FilterEngine {

    public List<NewsArticle> applyFilters(
            List<NewsArticle> articles,
            String keyword,
            String source,
            String dateRange,
            String specificDate
    ) {
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

        if (hasSpecificDate(specificDate)) {
            filteredArticles = filterBySpecificDate(filteredArticles, specificDate);
        } else if (hasDateRange(dateRange)) {
            filteredArticles = filterByDateRange(filteredArticles, dateRange);
        }

        sortByScore(filteredArticles);

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
            String source = safeText(article.getSource()).toLowerCase();

            if (title.contains(searchText)
                    || description.contains(searchText)
                    || source.contains(searchText)) {
                filteredArticles.add(article);
            }
        }

        sortByScore(filteredArticles);

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

        sortByScore(filteredArticles);

        return filteredArticles;
    }

    public List<NewsArticle> filterByDateRange(List<NewsArticle> articles, String dateRange) {
        List<NewsArticle> filteredArticles = new ArrayList<>();

        if (articles == null || !hasDateRange(dateRange)) {
            return filteredArticles;
        }

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startTime;

        switch (dateRange) {
            case "Today":
                LocalDate today = LocalDate.now();
                startTime = today.atStartOfDay(ZoneId.systemDefault());
                break;

            case "Last 24 Hours":
                startTime = now.minusHours(24);
                break;

            case "Last 3 Days":
                startTime = now.minusDays(3);
                break;

            case "Last 1 Week":
                startTime = now.minusWeeks(1);
                break;

            default:
                return new ArrayList<>(articles);
        }

        for (NewsArticle article : articles) {
            ZonedDateTime publishedDate = parsePublishedDate(article.getPublishedAt());

            if (publishedDate != null
                    && !publishedDate.isBefore(startTime)
                    && !publishedDate.isAfter(now)) {
                filteredArticles.add(article);
            }
        }

        sortByScore(filteredArticles);

        return filteredArticles;
    }

    public List<NewsArticle> filterBySpecificDate(List<NewsArticle> articles, String specificDate) {
        List<NewsArticle> filteredArticles = new ArrayList<>();

        if (articles == null || !hasSpecificDate(specificDate)) {
            return filteredArticles;
        }

        try {
            LocalDate targetDate = LocalDate.parse(specificDate.trim());

            for (NewsArticle article : articles) {
                ZonedDateTime publishedDateTime = parsePublishedDate(article.getPublishedAt());

                if (publishedDateTime == null) {
                    continue;
                }

                LocalDate articleDate = publishedDateTime.toLocalDate();

                if (articleDate.equals(targetDate)) {
                    filteredArticles.add(article);
                }
            }

        } catch (Exception ex) {
            return filteredArticles;
        }

        sortByScore(filteredArticles);

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

    public String buildFilterContext(String keyword, String source, String dateRange, String specificDate) {
        StringBuilder context = new StringBuilder();

        if (hasKeyword(keyword)) {
            context.append("keyword \"").append(keyword.trim()).append("\"");
        }

        if (hasSource(source)) {
            if (context.length() > 0) {
                context.append(", ");
            }

            context.append("source \"").append(source).append("\"");
        }

        if (hasSpecificDate(specificDate)) {
            if (context.length() > 0) {
                context.append(", ");
            }

            context.append("specific date ").append(specificDate.trim());
        } else if (hasDateRange(dateRange)) {
            if (context.length() > 0) {
                context.append(", ");
            }

            context.append(dateRange.toLowerCase());
        }

        if (context.length() == 0) {
            return "current results";
        }

        return context.toString();
    }

    public boolean hasAnyFilter(String keyword, String source, String dateRange, String specificDate) {
        return hasKeyword(keyword)
                || hasSource(source)
                || hasDateRange(dateRange)
                || hasSpecificDate(specificDate);
    }

    public boolean hasKeyword(String keyword) {
        return keyword != null && !keyword.trim().isEmpty();
    }

    public boolean hasSource(String source) {
        return source != null
                && !source.trim().isEmpty()
                && !source.equals("All Sources");
    }

    public boolean hasDateRange(String dateRange) {
        return dateRange != null
                && !dateRange.trim().isEmpty()
                && !dateRange.equals("All Dates");
    }

    public boolean hasSpecificDate(String specificDate) {
        return specificDate != null && !specificDate.trim().isEmpty();
    }

    private ZonedDateTime parsePublishedDate(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return null;
        }

        try {
            return ZonedDateTime.parse(rawDate);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private void sortByScore(List<NewsArticle> articles) {
        articles.sort(Comparator.comparingInt(NewsArticle::getScore).reversed());
    }

    private String safeText(String text) {
        return text == null ? "" : text;
    }
}
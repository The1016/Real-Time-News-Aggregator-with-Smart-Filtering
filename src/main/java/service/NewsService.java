package service;

import model.NewsArticle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewsService {

    private final APIClient apiClient;

    public NewsService() {
        apiClient = new APIClient();
    }

    public List<NewsArticle> fetchHotTopics() throws Exception {
        return fetchTopHeadlines(APIConfig.DEFAULT_HOT_TOPIC_CATEGORY);
    }

    public List<NewsArticle> fetchTopHeadlines(String category) throws Exception {
        String safeCategory = encode(category.toLowerCase());

        String url = APIConfig.BASE_URL
                + "/top-headlines?country=" + APIConfig.DEFAULT_COUNTRY
                + "&category=" + safeCategory
                + "&pageSize=" + APIConfig.PAGE_SIZE;

        String json = apiClient.getJson(url);

        return parseArticles(json, null, true);
    }

    public List<NewsArticle> fetchByKeyword(String keyword) throws Exception {
        String safeKeyword = encode(keyword);

        String url = APIConfig.BASE_URL
                + "/everything?q=" + safeKeyword
                + "&sortBy=publishedAt"
                + "&pageSize=" + APIConfig.PAGE_SIZE;

        String json = apiClient.getJson(url);

        return parseArticles(json, keyword, false);
    }

    public List<NewsArticle> fetchByKeywordAndDate(String keyword, String fromDate, String toDate) throws Exception {
        String safeKeyword = encode(keyword);

        String url = APIConfig.BASE_URL
                + "/everything?q=" + safeKeyword
                + "&from=" + fromDate
                + "&to=" + toDate
                + "&sortBy=publishedAt"
                + "&pageSize=" + APIConfig.PAGE_SIZE;

        String json = apiClient.getJson(url);

        return parseArticles(json, keyword, false);
    }

    private List<NewsArticle> parseArticles(String json, String keyword, boolean topHeadlineMode) {
        List<NewsArticle> articlesList = new ArrayList<>();

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            if (root.has("status") && root.get("status").getAsString().equals("error")) {
                String message = getStringOrDefault(root, "message", "Unknown API error.");
                throw new RuntimeException(message);
            }

            JsonArray articles = root.getAsJsonArray("articles");

            if (articles == null) {
                return articlesList;
            }

            int position = 0;

            for (JsonElement element : articles) {
                JsonObject articleObj = element.getAsJsonObject();

                String title = getStringOrDefault(articleObj, "title", "Untitled");
                String author = getStringOrDefault(articleObj, "author", "Unknown Author");
                String description = getStringOrDefault(articleObj, "description", "No description available.");
                String url = getSafeString(articleObj, "url");
                String publishedAt = getSafeString(articleObj, "publishedAt");

                String sourceName = "Unknown Source";

                if (articleObj.has("source") && !articleObj.get("source").isJsonNull()) {
                    JsonObject sourceObj = articleObj.getAsJsonObject("source");
                    sourceName = getStringOrDefault(sourceObj, "name", "Unknown Source");
                }

                NewsArticle article = new NewsArticle(
                        title,
                        author,
                        sourceName,
                        description,
                        url,
                        publishedAt
                );

                article.setScore(calculateScore(article, keyword, topHeadlineMode, position));

                articlesList.add(article);
                position++;
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse news data. The server may have returned unexpected content.");
        }

        articlesList = removeDuplicates(articlesList);
        sortByScore(articlesList);

        return articlesList;
    }

    private int calculateScore(NewsArticle article, String keyword, boolean topHeadlineMode, int position) {
        int score = 0;

        if (topHeadlineMode) {
            score += 25;
        }

        score += Math.max(0, 25 - position);

        score += getRecencyScore(article.getPublishedAt());

        if (!isEmpty(article.getTitle()) && !article.getTitle().equals("Untitled")) {
            score += 10;
        }

        if (!isEmpty(article.getDescription()) && !article.getDescription().equals("No description available.")) {
            score += 10;
        }

        if (!isEmpty(article.getAuthor()) && !article.getAuthor().equals("Unknown Author")) {
            score += 5;
        }

        if (!isEmpty(article.getSource()) && !article.getSource().equals("Unknown Source")) {
            score += 5;
        }

        if (!isEmpty(article.getUrl())) {
            score += 5;
        }

        if (!isEmpty(keyword)) {
            String searchText = keyword.toLowerCase();
            String title = safeText(article.getTitle()).toLowerCase();
            String description = safeText(article.getDescription()).toLowerCase();
            String source = safeText(article.getSource()).toLowerCase();

            if (title.contains(searchText)) {
                score += 35;
            }

            if (description.contains(searchText)) {
                score += 20;
            }

            if (source.contains(searchText)) {
                score += 10;
            }
        }

        return score;
    }

    private int getRecencyScore(String rawDate) {
        if (isEmpty(rawDate)) {
            return 0;
        }

        try {
            ZonedDateTime publishedTime = ZonedDateTime.parse(rawDate);
            long hoursOld = Duration.between(publishedTime, ZonedDateTime.now()).toHours();

            if (hoursOld <= 6) {
                return 35;
            }

            if (hoursOld <= 24) {
                return 30;
            }

            if (hoursOld <= 72) {
                return 22;
            }

            if (hoursOld <= 168) {
                return 15;
            }

            if (hoursOld <= 720) {
                return 8;
            }

            return 3;

        } catch (DateTimeParseException ex) {
            return 0;
        }
    }

    private List<NewsArticle> removeDuplicates(List<NewsArticle> articles) {
        List<NewsArticle> uniqueArticles = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (NewsArticle article : articles) {
            String key = !isEmpty(article.getUrl())
                    ? article.getUrl().trim().toLowerCase()
                    : article.getTitle().trim().toLowerCase();

            if (!seen.contains(key)) {
                seen.add(key);
                uniqueArticles.add(article);
            }
        }

        return uniqueArticles;
    }

    private void sortByScore(List<NewsArticle> articles) {
        articles.sort(Comparator.comparingInt(NewsArticle::getScore).reversed());
    }

    private String encode(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    private String getSafeString(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return "";
        }

        return obj.get(key).getAsString();
    }

    private String getStringOrDefault(JsonObject obj, String key, String defaultValue) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return defaultValue;
        }

        String value = obj.get(key).getAsString().trim();

        return value.isEmpty() ? defaultValue : value;
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private String safeText(String text) {
        return text == null ? "" : text;
    }
}

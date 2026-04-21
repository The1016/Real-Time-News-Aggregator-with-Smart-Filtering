package service;

import model.NewsArticle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class NewsService{

    private final APIClient apiClient;

    public NewsService() {
        apiClient = new APIClient();
    }

    public List<NewsArticle> fetchTopHeadlines(String category) throws Exception {
        String url = APIConfig.BASE_URL + "/top-headlines?country=us&category=" + category;
        String json = apiClient.getJson(url);

        return parseArticles(json);
    }

    public List<NewsArticle> fetchByKeyword(String keyword) throws Exception {
        String url = APIConfig.BASE_URL + "/everything?q=" + keyword;
        String json = apiClient.getJson(url);

        return parseArticles(json);
    }

    private List<NewsArticle> parseArticles(String json) {
        List<NewsArticle> articlesList = new ArrayList<>();

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray articles = root.getAsJsonArray("articles");

            if (articles == null) {
                return articlesList;
            }

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

                articlesList.add(article);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse news data. The server may have returned unexpected content.");
        }

        return articlesList;
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
}
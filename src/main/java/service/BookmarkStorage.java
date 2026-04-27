package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Bookmark;
import model.NewsArticle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BookmarkStorage {

    private final Path bookmarkFilePath;
    private final Gson gson;

    public BookmarkStorage() {
        bookmarkFilePath = Path.of("data", "bookmarks.json");
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveBookmarks(List<Bookmark> bookmarks) throws IOException {
        Files.createDirectories(bookmarkFilePath.getParent());

        JsonArray bookmarkArray = new JsonArray();

        if (bookmarks != null) {
            for (Bookmark bookmark : bookmarks) {
                if (bookmark == null || bookmark.getArticle() == null) {
                    continue;
                }

                NewsArticle article = bookmark.getArticle();

                JsonObject articleObject = new JsonObject();
                articleObject.addProperty("title", safeText(article.getTitle()));
                articleObject.addProperty("author", safeText(article.getAuthor()));
                articleObject.addProperty("source", safeText(article.getSource()));
                articleObject.addProperty("description", safeText(article.getDescription()));
                articleObject.addProperty("url", safeText(article.getUrl()));
                articleObject.addProperty("publishedAt", safeText(article.getPublishedAt()));
                articleObject.addProperty("score", article.getScore());

                JsonObject bookmarkObject = new JsonObject();
                bookmarkObject.add("article", articleObject);
                bookmarkObject.addProperty("savedAt", safeText(bookmark.getSavedAt()));

                bookmarkArray.add(bookmarkObject);
            }
        }

        String json = gson.toJson(bookmarkArray);
        Files.writeString(bookmarkFilePath, json, StandardCharsets.UTF_8);
    }

    public List<Bookmark> loadBookmarks() {
        List<Bookmark> bookmarks = new ArrayList<>();

        try {
            if (!Files.exists(bookmarkFilePath)) {
                return bookmarks;
            }

            String json = Files.readString(bookmarkFilePath, StandardCharsets.UTF_8);

            if (json == null || json.trim().isEmpty()) {
                return bookmarks;
            }

            JsonArray bookmarkArray = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement element : bookmarkArray) {
                JsonObject bookmarkObject = element.getAsJsonObject();

                if (!bookmarkObject.has("article") || bookmarkObject.get("article").isJsonNull()) {
                    continue;
                }

                JsonObject articleObject = bookmarkObject.getAsJsonObject("article");

                String title = getString(articleObject, "title");
                String author = getString(articleObject, "author");
                String source = getString(articleObject, "source");
                String description = getString(articleObject, "description");
                String url = getString(articleObject, "url");
                String publishedAt = getString(articleObject, "publishedAt");
                int score = getInt(articleObject, "score");

                NewsArticle article = new NewsArticle(
                        title,
                        author,
                        source,
                        description,
                        url,
                        publishedAt,
                        score
                );

                String savedAt = getString(bookmarkObject, "savedAt");

                Bookmark bookmark = new Bookmark(article, savedAt);
                bookmarks.add(bookmark);
            }

        } catch (Exception ex) {
            System.out.println("[BOOKMARK STORAGE] Failed to load bookmarks: " + ex.getMessage());
        }

        return bookmarks;
    }

    private String getString(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return "";
        }

        return object.get(key).getAsString();
    }

    private int getInt(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return 0;
        }

        try {
            return object.get(key).getAsInt();
        } catch (Exception ex) {
            return 0;
        }
    }

    private String safeText(String text) {
        return text == null ? "" : text;
    }
}

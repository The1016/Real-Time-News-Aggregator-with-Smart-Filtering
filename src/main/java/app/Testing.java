package app;

import model.NewsArticle;
import service.NewsService;

import java.util.List;

import service.APIClient;
import service.APIConfig;
import com.google.gson.Gson;


/*
public class Testing {
    public static void main(String[] args) {
        APIClient client = new APIClient();

        String url = APIConfig.BASE_URL + "/top-headlines?country=us&category=technology";

        try {
            String json = client.getJson(url);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/




public class Testing {
    public static void main(String[] args) {
        NewsService newsService = new NewsService();

        try {
            List<NewsArticle> articles = newsService.fetchTopHeadlines("technology");

            for (NewsArticle article : articles) {
                System.out.println(article.getTitle());
                System.out.println(article.getSource());
                System.out.println(article.getPublishedAt());
                System.out.println("----------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//testing commit


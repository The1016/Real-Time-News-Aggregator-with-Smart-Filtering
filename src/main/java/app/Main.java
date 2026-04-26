package app;

import model.NewsArticle;
import service.NewsService;
import ui.LoadingScreen;
import ui.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoadingScreen loadingScreen = new LoadingScreen();
            loadingScreen.setVisible(true);

            NewsService newsService = new NewsService();

            new SwingWorker<List<NewsArticle>, Void>() {
                @Override
                protected List<NewsArticle> doInBackground() throws Exception {
                    return newsService.fetchHotTopics();
                }

                @Override
                protected void done() {
                    try {
                        List<NewsArticle> hotTopics = get();

                        loadingScreen.fadeOut(() -> {
                            MainFrame frame = new MainFrame(hotTopics);
                            frame.setVisible(true);
                        });

                    } catch (Exception ex) {
                        loadingScreen.dispose();

                        Throwable cause = ex.getCause();
                        String message = cause != null ? cause.getMessage() : ex.getMessage();

                        JOptionPane.showMessageDialog(
                                null,
                                "Failed to fetch today's hot topics.\n\n" + message,
                                "Loading Error",
                                JOptionPane.ERROR_MESSAGE
                        );

                        MainFrame frame = new MainFrame();
                        frame.setVisible(true);
                    }
                }
            }.execute();
        });
    }
}
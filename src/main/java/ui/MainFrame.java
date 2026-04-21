package ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import model.NewsArticle;
import service.NewsService;

public class MainFrame extends JFrame {

    private JComboBox<String> categoryComboBox;
    private JButton fetchButton;
    private JTextField searchField;
    private JButton searchButton;
    private JButton filterButton;
    private JButton refreshButton;
    private JTable articleTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    private final NewsService newsService;

    private List<NewsArticle> currentArticles;

    public MainFrame() {
        newsService = new NewsService();

        setTitle("News Aggregator Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();

        String[] categories = {"Business", "Technology", "Sports", "Health"};
        categoryComboBox = new JComboBox<>(categories);

        fetchButton = new JButton("Fetch News");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        filterButton = new JButton("Filter");
        refreshButton = new JButton("Refresh");

        topPanel.add(categoryComboBox);
        topPanel.add(fetchButton);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(filterButton);
        topPanel.add(refreshButton);

        String[] columnNames = {"Title", "Source", "Published Date"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        articleTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(articleTable);

        statusLabel = new JLabel("Ready — select a category and click Fetch News.");

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        fetchButton.addActionListener(e -> fetchByCategory());
        searchButton.addActionListener(e -> fetchByKeyword());
        filterButton.addActionListener(e -> filterCurrentArticles());
        refreshButton.addActionListener(e -> refreshApp());
    }

    private void fetchByCategory() {
        String category = (String) categoryComboBox.getSelectedItem();

        System.out.println("[FETCH] Category: " + category);

        setLoading(true);

        new SwingWorker<List<NewsArticle>, Void>() {
            @Override
            protected List<NewsArticle> doInBackground() throws Exception {
                return newsService.fetchTopHeadlines(category);
            }

            @Override
            protected void done() {
                setLoading(false);

                try {
                    currentArticles = get();
                    populateTable(currentArticles, "category \"" + category + "\"");
                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    showError(cause != null ? cause.getMessage() : ex.getMessage());
                }
            }
        }.execute();
    }

    private void fetchByKeyword() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            statusLabel.setText("Please enter a keyword to search.");
            return;
        }

        System.out.println("[SEARCH] Keyword: " + keyword);

        setLoading(true);

        new SwingWorker<List<NewsArticle>, Void>() {
            @Override
            protected List<NewsArticle> doInBackground() throws Exception {
                return newsService.fetchByKeyword(keyword);
            }

            @Override
            protected void done() {
                setLoading(false);

                try {
                    currentArticles = get();
                    populateTable(currentArticles, "keyword \"" + keyword + "\"");
                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    showError(cause != null ? cause.getMessage() : ex.getMessage());
                }
            }
        }.execute();
    }

    private void filterCurrentArticles() {
        String keyword = searchField.getText().trim().toLowerCase();
        String category = (String) categoryComboBox.getSelectedItem();

        System.out.println("[FILTER] Category: " + category + " | Keyword: " + keyword);

        if (currentArticles == null || currentArticles.isEmpty()) {
            statusLabel.setText("Please fetch news first before filtering.");
            return;
        }

        if (keyword.isEmpty()) {
            populateTable(currentArticles, "current results");
            return;
        }

        tableModel.setRowCount(0);

        int count = 0;

        for (NewsArticle article : currentArticles) {
            String title = article.getTitle() == null ? "" : article.getTitle().toLowerCase();
            String source = article.getSource() == null ? "" : article.getSource().toLowerCase();
            String date = article.getPublishedAt() == null ? "" : article.getPublishedAt().toLowerCase();

            if (title.contains(keyword) || source.contains(keyword) || date.contains(keyword)) {
                tableModel.addRow(new Object[] {
                        article.getTitle(),
                        article.getSource(),
                        article.getPublishedAt()
                });

                count++;
            }
        }

        statusLabel.setText("Filtered " + count + " articles using keyword \"" + keyword + "\".");
    }

    private void refreshApp() {
        System.out.println("[REFRESH] Refreshing...");

        searchField.setText("");
        categoryComboBox.setSelectedIndex(0);
        tableModel.setRowCount(0);
        currentArticles = null;

        statusLabel.setText("Refreshed. Select a category and click Fetch News.");
    }

    private void populateTable(List<NewsArticle> articles, String context) {
        tableModel.setRowCount(0);

        if (articles == null || articles.isEmpty()) {
            statusLabel.setText("No articles found for " + context + ".");
            return;
        }

        for (NewsArticle article : articles) {
            tableModel.addRow(new Object[] {
                    article.getTitle(),
                    article.getSource(),
                    article.getPublishedAt()
            });
        }

        statusLabel.setText("Showing " + articles.size() + " articles for " + context + ".");
    }

    private void setLoading(boolean loading) {
        fetchButton.setEnabled(!loading);
        searchButton.setEnabled(!loading);
        filterButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);

        if (loading) {
            statusLabel.setText("Loading...");
        }
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
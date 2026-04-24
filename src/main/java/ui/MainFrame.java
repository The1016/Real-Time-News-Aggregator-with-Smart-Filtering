package ui;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
import service.FilterEngine;
import service.NewsService;

public class MainFrame extends JFrame {

    private JComboBox<String> categoryComboBox;
    private JComboBox<String> sourceComboBox;
    private JButton fetchButton;
    private JTextField searchField;
    private JButton searchButton;
    private JButton filterButton;
    private JButton resetButton;
    private JButton refreshButton;
    private JTable articleTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;


    private JButton openButton;


    private final NewsService newsService;
    private final FilterEngine filterEngine;

    private List<NewsArticle> currentArticles;

    public MainFrame() {
        newsService = new NewsService();
        filterEngine = new FilterEngine();

        setTitle("News Aggregator Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] categories = {"Business", "Technology", "Sports", "Health"};
        categoryComboBox = new JComboBox<>(categories);

        fetchButton = new JButton("Fetch News");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        filterButton = new JButton("Filter");
        resetButton = new JButton("Reset Filter");
        refreshButton = new JButton("Refresh");
        openButton = new JButton("Open Article");

        sourceComboBox = new JComboBox<>(new String[]{"All Sources"});
        sourceComboBox.setPreferredSize(new Dimension(160, 24));

        // Row 1: fetch controls
        JPanel fetchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fetchPanel.add(categoryComboBox);
        fetchPanel.add(fetchButton);
        fetchPanel.add(searchField);
        fetchPanel.add(searchButton);
        fetchPanel.add(refreshButton);

        // Row 2: filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Source:"));
        filterPanel.add(sourceComboBox);
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        filterPanel.add(openButton);

        JPanel northContainer = new JPanel(new GridLayout(2, 1));
        northContainer.add(fetchPanel);
        northContainer.add(filterPanel);


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

        add(northContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        fetchButton.addActionListener(e -> fetchByCategory());
        searchButton.addActionListener(e -> fetchByKeyword());
        filterButton.addActionListener(e -> filterCurrentArticles());
        resetButton.addActionListener(e -> resetFilter());
        refreshButton.addActionListener(e -> refreshApp());

        openButton.addActionListener(e -> openSelectedArticle());

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
                    updateSourceDropdown(currentArticles);
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
                    updateSourceDropdown(currentArticles);
                    populateTable(currentArticles, "keyword \"" + keyword + "\"");
                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    showError(cause != null ? cause.getMessage() : ex.getMessage());
                }
            }
        }.execute();
    }

    private void filterCurrentArticles() {
        String keyword = searchField.getText().trim();
        String selectedSource = (String) sourceComboBox.getSelectedItem();
        boolean hasKeyword = !keyword.isEmpty();
        boolean hasSource = selectedSource != null && !selectedSource.equals("All Sources");

        System.out.println("[FILTER] Keyword: " + keyword + " | Source: " + selectedSource);

        if (currentArticles == null || currentArticles.isEmpty()) {
            statusLabel.setText("Please fetch news first before filtering.");
            return;
        }

        if (!hasKeyword && !hasSource) {
            populateTable(currentArticles, "current results");
            return;
        }

        List<NewsArticle> filtered = new ArrayList<>(currentArticles);

        if (hasKeyword) {
            filtered = filterEngine.filterByKeyword(filtered, keyword);
        }

        if (hasSource) {
            filtered = filterEngine.filterBySource(filtered, selectedSource);
        }

        String context = hasKeyword && hasSource
                ? "keyword \"" + keyword + "\" from \"" + selectedSource + "\""
                : hasKeyword ? "keyword \"" + keyword + "\""
                : "source \"" + selectedSource + "\"";

        populateTable(filtered, context);
    }

    private void resetFilter() {
        searchField.setText("");
        sourceComboBox.setSelectedIndex(0);

        if (currentArticles == null || currentArticles.isEmpty()) {
            statusLabel.setText("No articles loaded. Fetch news first.");
            return;
        }

        populateTable(currentArticles, "all fetched articles");
    }

    private void refreshApp() {
        System.out.println("[REFRESH] Refreshing...");

        searchField.setText("");
        categoryComboBox.setSelectedIndex(0);
        sourceComboBox.removeAllItems();
        sourceComboBox.addItem("All Sources");
        tableModel.setRowCount(0);
        currentArticles = null;

        statusLabel.setText("Refreshed. Select a category and click Fetch News.");
    }

    private void openSelectedArticle() {
        int selectedRow = articleTable.getSelectedRow();

        if (selectedRow == -1) {
            statusLabel.setText("Please select an article to open.");
            return;
        }

        if (currentArticles == null || selectedRow >= currentArticles.size()) {
            statusLabel.setText("Could not find the selected article.");
            return;
        }

        NewsArticle article = currentArticles.get(selectedRow);
        String url = article.getUrl();

        if (url == null || url.trim().isEmpty()) {
            showError("No URL available for this article.");
            return;
        }

        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            showError("Opening URLs is not supported on this system.");
            return;
        }

        try {
            Desktop.getDesktop().browse(new URI(url));
            statusLabel.setText("Opened: " + article.getTitle());
        } catch (Exception ex) {
            showError("Failed to open article: " + ex.getMessage());
        }
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
        resetButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
        sourceComboBox.setEnabled(!loading);

        openButton.setEnabled(!loading);



        if (loading) {
            statusLabel.setText("Loading...");
        }
    }

    private void updateSourceDropdown(List<NewsArticle> articles) {
        TreeSet<String> sources = new TreeSet<>();
        if (articles != null) {
            for (NewsArticle a : articles) {
                if (a.getSource() != null && !a.getSource().isBlank()) {
                    sources.add(a.getSource());
                }
            }
        }

        sourceComboBox.removeAllItems();
        sourceComboBox.addItem("All Sources");
        for (String source : sources) {
            sourceComboBox.addItem(source);
        }
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
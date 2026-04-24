package ui;

import filter.FilterEngine;
import model.NewsArticle;
import service.NewsService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URI;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class MainFrame extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(18, 18, 18);
    private static final Color PANEL_COLOR = new Color(28, 28, 28);
    private static final Color FIELD_COLOR = new Color(35, 35, 35);
    private static final Color BUTTON_COLOR = new Color(45, 45, 45);
    private static final Color BUTTON_HOVER_COLOR = new Color(58, 58, 58);
    private static final Color TEXT_COLOR = new Color(235, 255, 255);
    private static final Color MUTED_TEXT_COLOR = new Color(190, 210, 210);
    private static final Color GRID_COLOR = new Color(70, 130, 130);
    private static final Color SELECTION_COLOR = new Color(50, 90, 90);

    private JComboBox<String> categoryComboBox;
    private JComboBox<String> sourceComboBox;

    private JButton fetchButton;
    private JButton searchButton;
    private JButton filterButton;
    private JButton resetButton;
    private JButton refreshButton;
    private JButton openButton;

    private JTextField searchField;

    private JTable articleTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    private final NewsService newsService;
    private final FilterEngine filterEngine;

    private List<NewsArticle> currentArticles;
    private List<NewsArticle> displayedArticles;

    public MainFrame() {
        newsService = new NewsService();
        filterEngine = new FilterEngine();

        applyDarkTheme();

        setupWindow();
        setupTopControls();
        setupArticleTable();
        setupStatusBar();
        setupButtonActions();
    }

    private void applyDarkTheme() {
        UIManager.put("Panel.background", PANEL_COLOR);

        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 14));

        UIManager.put("Button.background", BUTTON_COLOR);
        UIManager.put("Button.foreground", TEXT_COLOR);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 13));
        UIManager.put("Button.focus", GRID_COLOR);

        UIManager.put("TextField.background", FIELD_COLOR);
        UIManager.put("TextField.foreground", TEXT_COLOR);
        UIManager.put("TextField.caretForeground", TEXT_COLOR);
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));

        UIManager.put("ComboBox.background", FIELD_COLOR);
        UIManager.put("ComboBox.foreground", TEXT_COLOR);
        UIManager.put("ComboBox.font", new Font("Arial", Font.PLAIN, 14));

        UIManager.put("Table.background", BACKGROUND_COLOR);
        UIManager.put("Table.foreground", TEXT_COLOR);
        UIManager.put("Table.gridColor", GRID_COLOR);
        UIManager.put("Table.selectionBackground", SELECTION_COLOR);
        UIManager.put("Table.selectionForeground", Color.WHITE);

        UIManager.put("TableHeader.background", FIELD_COLOR);
        UIManager.put("TableHeader.foreground", TEXT_COLOR);
        UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 16));

        UIManager.put("ScrollPane.background", BACKGROUND_COLOR);
        UIManager.put("Viewport.background", BACKGROUND_COLOR);

        UIManager.put("OptionPane.background", PANEL_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
    }

    private void setupWindow() {
        setTitle("News Aggregator Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
    }

    private void setupTopControls() {
        String[] categories = {"Business", "Technology", "Sports", "Health"};

        categoryComboBox = new JComboBox<>(categories);
        sourceComboBox = new JComboBox<>(new String[]{"All Sources"});

        fetchButton = new JButton("Fetch News");
        searchButton = new JButton("Search");
        filterButton = new JButton("Filter");
        resetButton = new JButton("Reset Filter");
        refreshButton = new JButton("Refresh");
        openButton = new JButton("Open Article");

        searchField = new JTextField(24);

        categoryComboBox.setPreferredSize(new Dimension(150, 30));
        sourceComboBox.setPreferredSize(new Dimension(180, 30));
        searchField.setPreferredSize(new Dimension(260, 30));

        styleComboBox(categoryComboBox);
        styleComboBox(sourceComboBox);
        styleTextField(searchField);

        styleButton(fetchButton);
        styleButton(searchButton);
        styleButton(filterButton);
        styleButton(resetButton);
        styleButton(refreshButton);
        styleButton(openButton);

        JPanel fetchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        fetchPanel.setBackground(PANEL_COLOR);
        fetchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR));

        JLabel categoryLabel = new JLabel("Category:");
        JLabel searchLabel = new JLabel("Search:");

        styleLabel(categoryLabel);
        styleLabel(searchLabel);

        fetchPanel.add(categoryLabel);
        fetchPanel.add(categoryComboBox);
        fetchPanel.add(fetchButton);
        fetchPanel.add(searchLabel);
        fetchPanel.add(searchField);
        fetchPanel.add(searchButton);
        fetchPanel.add(refreshButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR));

        JLabel sourceLabel = new JLabel("Source:");
        styleLabel(sourceLabel);

        filterPanel.add(sourceLabel);
        filterPanel.add(sourceComboBox);
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        filterPanel.add(openButton);

        JPanel topContainer = new JPanel(new GridLayout(2, 1));
        topContainer.setBackground(PANEL_COLOR);
        topContainer.add(fetchPanel);
        topContainer.add(filterPanel);

        add(topContainer, BorderLayout.NORTH);
    }

    private void setupArticleTable() {
        String[] columnNames = {"Title", "Source", "Published Date"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        articleTable = new JTable(tableModel);
        articleTable.setFont(new Font("Arial", Font.PLAIN, 16));
        articleTable.setRowHeight(72);
        articleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        articleTable.setBackground(BACKGROUND_COLOR);
        articleTable.setForeground(TEXT_COLOR);
        articleTable.setSelectionBackground(SELECTION_COLOR);
        articleTable.setSelectionForeground(Color.WHITE);

        articleTable.setShowGrid(true);
        articleTable.setGridColor(GRID_COLOR);
        articleTable.setIntercellSpacing(new Dimension(1, 1));

        articleTable.setFillsViewportHeight(true);
        articleTable.setFocusable(false);

        JTableHeader header = articleTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(FIELD_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GRID_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        articleTable.getColumnModel().getColumn(0).setCellRenderer(new WrappedTextCellRenderer());
        articleTable.getColumnModel().getColumn(1).setCellRenderer(new CenterCellRenderer());
        articleTable.getColumnModel().getColumn(2).setCellRenderer(new CenterCellRenderer());

        TableColumn titleColumn = articleTable.getColumnModel().getColumn(0);
        TableColumn sourceColumn = articleTable.getColumnModel().getColumn(1);
        TableColumn dateColumn = articleTable.getColumnModel().getColumn(2);

        titleColumn.setPreferredWidth(650);
        sourceColumn.setPreferredWidth(180);
        dateColumn.setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(articleTable);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupStatusBar() {
        statusLabel = new JLabel("Ready — select a category and click Fetch News.");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(MUTED_TEXT_COLOR);
        statusLabel.setBackground(PANEL_COLOR);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, GRID_COLOR),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        add(statusLabel, BorderLayout.SOUTH);
    }

    private void setupButtonActions() {
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
        String keyword = searchField.getText();
        String selectedSource = (String) sourceComboBox.getSelectedItem();

        System.out.println("[FILTER] Keyword: " + keyword + " | Source: " + selectedSource);

        if (currentArticles == null || currentArticles.isEmpty()) {
            statusLabel.setText("Please fetch news first before filtering.");
            return;
        }

        if (!filterEngine.hasAnyFilter(keyword, selectedSource)) {
            populateTable(currentArticles, "current results");
            return;
        }

        List<NewsArticle> filteredArticles = filterEngine.applyFilters(
                currentArticles,
                keyword,
                selectedSource
        );

        String context = filterEngine.buildFilterContext(keyword, selectedSource);
        populateTable(filteredArticles, context);
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
        displayedArticles = null;

        statusLabel.setText("Refreshed. Select a category and click Fetch News.");
    }

    private void openSelectedArticle() {
        int selectedRow = articleTable.getSelectedRow();

        if (selectedRow == -1) {
            statusLabel.setText("Please select an article to open.");
            return;
        }

        if (displayedArticles == null || selectedRow >= displayedArticles.size()) {
            statusLabel.setText("Could not find the selected article.");
            return;
        }

        NewsArticle article = displayedArticles.get(selectedRow);
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
        displayedArticles = articles;

        tableModel.setRowCount(0);

        if (articles == null || articles.isEmpty()) {
            statusLabel.setText("No articles found for " + context + ".");
            return;
        }

        for (NewsArticle article : articles) {
            tableModel.addRow(new Object[]{
                    article.getTitle(),
                    article.getSource(),
                    formatPublishedDate(article.getPublishedAt())
            });
        }

        statusLabel.setText("Showing " + articles.size() + " articles for " + context + ".");
    }

    private void updateSourceDropdown(List<NewsArticle> articles) {
        sourceComboBox.removeAllItems();
        sourceComboBox.addItem("All Sources");

        for (String source : filterEngine.getAvailableSources(articles)) {
            sourceComboBox.addItem(source);
        }
    }

    private void setLoading(boolean loading) {
        fetchButton.setEnabled(!loading);
        searchButton.setEnabled(!loading);
        filterButton.setEnabled(!loading);
        resetButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
        openButton.setEnabled(!loading);

        searchField.setEnabled(!loading);

        categoryComboBox.setEnabled(true);
        sourceComboBox.setEnabled(true);

        categoryComboBox.setBackground(FIELD_COLOR);
        categoryComboBox.setForeground(TEXT_COLOR);

        sourceComboBox.setBackground(FIELD_COLOR);
        sourceComboBox.setForeground(TEXT_COLOR);

        if (loading) {
            statusLabel.setText("Loading...");
        }
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void styleLabel(JLabel label) {
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(FIELD_COLOR);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(TEXT_COLOR);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRID_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(FIELD_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));
    }

    private void styleButton(JButton button) {
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRID_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON_HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                button.setBackground(BUTTON_COLOR);
            }
        });
    }

    private static class WrappedTextCellRenderer extends DefaultTableCellRenderer {

        private final JTextArea textArea;

        public WrappedTextCellRenderer() {
            textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setOpaque(true);
            textArea.setFont(new Font("Arial", Font.PLAIN, 16));
            textArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            textArea.setText(value == null ? "" : value.toString());

            if (isSelected) {
                textArea.setBackground(table.getSelectionBackground());
                textArea.setForeground(table.getSelectionForeground());
            } else {
                textArea.setBackground(table.getBackground());
                textArea.setForeground(table.getForeground());
            }

            return textArea;
        }
    }

    private static class CenterCellRenderer extends DefaultTableCellRenderer {

        public CenterCellRenderer() {
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.PLAIN, 15));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
                component.setForeground(table.getSelectionForeground());
            } else {
                component.setBackground(table.getBackground());
                component.setForeground(table.getForeground());
            }

            return component;
        }
    }
    private String formatPublishedDate(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return "Unknown date";
        }

        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(rawDate);

            int day = dateTime.getDayOfMonth();
            String month = dateTime.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
            int year = dateTime.getYear();

            return day + getDaySuffix(day) + " " + month + ", " + year;

        } catch (DateTimeParseException ex) {
            return rawDate;
        }
    }

    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }

        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }


}
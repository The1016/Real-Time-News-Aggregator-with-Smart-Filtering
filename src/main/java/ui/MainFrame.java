package ui;

import filter.FilterEngine;
import model.Bookmark;
import model.NewsArticle;
import service.APIConfig;
import service.BookmarkStorage;
import service.NewsService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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
    private JComboBox<String> dateRangeComboBox;
    private JComboBox<String> bookmarkDateRangeComboBox;

    private JButton fetchButton;
    private JButton searchButton;
    private JButton filterButton;
    private JButton resetButton;
    private JButton refreshButton;
    private JButton openButton;
    private JButton addBookmarkButton;
    private JButton viewBookmarksButton;
    private JButton backToNewsButton;
    private JButton openBookmarkedArticleButton;
    private JButton removeBookmarkButton;
    private JButton applyDateFilterButton;
    private JButton applyBookmarkDateFilterButton;
    private JButton resetBookmarkDateFilterButton;
    private JButton chooseDateButton;
    private JButton chooseBookmarkDateButton;

    private JTextField searchField;
    private JTextField specificDateField;
    private JTextField bookmarkSpecificDateField;

    private JTable articleTable;
    private JTable bookmarkTable;

    private DefaultTableModel tableModel;
    private DefaultTableModel bookmarkTableModel;

    private JLabel statusLabel;
    private JLabel bookmarkStatusLabel;

    private final NewsService newsService;
    private final FilterEngine filterEngine;
    private final BookmarkStorage bookmarkStorage;

    private List<NewsArticle> currentArticles;
    private List<NewsArticle> displayedArticles;
    private List<Bookmark> bookmarks;
    private List<Bookmark> displayedBookmarks;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel newsPage;
    private JPanel bookmarksPage;

    public MainFrame() {
        newsService = new NewsService();
        filterEngine = new FilterEngine();
        bookmarkStorage = new BookmarkStorage();
        bookmarks = bookmarkStorage.loadBookmarks();
        displayedBookmarks = bookmarks;

        applyDarkTheme();

        setupWindow();
        setupPages();
        setupButtonActions();

        fetchHotTopics();
    }

    public MainFrame(List<NewsArticle> startingArticles) {
        newsService = new NewsService();
        filterEngine = new FilterEngine();
        bookmarkStorage = new BookmarkStorage();
        bookmarks = bookmarkStorage.loadBookmarks();
        displayedBookmarks = bookmarks;

        applyDarkTheme();

        setupWindow();
        setupPages();
        setupButtonActions();

        currentArticles = startingArticles;
        updateSourceDropdown(currentArticles);
        populateTable(currentArticles, "today's hot topics");
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
        getContentPane().setBackground(BACKGROUND_COLOR);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
    }

    private void setupPages() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        newsPage = new JPanel(new BorderLayout());
        newsPage.setBackground(BACKGROUND_COLOR);

        bookmarksPage = new JPanel(new BorderLayout());
        bookmarksPage.setBackground(BACKGROUND_COLOR);

        setupTopControls();
        setupArticleTable();
        setupStatusBar();
        setupBookmarksPage();

        mainPanel.add(newsPage, "NEWS_PAGE");
        mainPanel.add(bookmarksPage, "BOOKMARKS_PAGE");

        add(mainPanel);

        cardLayout.show(mainPanel, "NEWS_PAGE");
    }

    private void setupTopControls() {
        categoryComboBox = new JComboBox<>(APIConfig.CATEGORIES);
        categoryComboBox.setSelectedItem("General");

        sourceComboBox = new JComboBox<>(new String[]{"All Sources"});

        dateRangeComboBox = new JComboBox<>(new String[]{
                "All Dates",
                "Today",
                "Last 24 Hours",
                "Last 3 Days",
                "Last 1 Week"
        });

        fetchButton = new JButton("Fetch News");
        searchButton = new JButton("Search");
        filterButton = new JButton("Filter");
        resetButton = new JButton("Reset Filter");
        refreshButton = new JButton("Refresh");
        openButton = new JButton("Open Article");
        addBookmarkButton = new JButton("Add Bookmark");
        viewBookmarksButton = new JButton("Bookmarks");
        applyDateFilterButton = new JButton("Apply Date Filter");
        chooseDateButton = new JButton("Choose Date");

        searchField = new JTextField(24);

        specificDateField = new JTextField(10);
        specificDateField.setEditable(false);
        specificDateField.setToolTipText("Click Choose Date to select a date");

        categoryComboBox.setPreferredSize(new Dimension(150, 30));
        sourceComboBox.setPreferredSize(new Dimension(180, 30));
        dateRangeComboBox.setPreferredSize(new Dimension(150, 30));
        searchField.setPreferredSize(new Dimension(260, 30));
        specificDateField.setPreferredSize(new Dimension(120, 30));

        styleComboBox(categoryComboBox);
        styleComboBox(sourceComboBox);
        styleComboBox(dateRangeComboBox);

        styleTextField(searchField);
        styleTextField(specificDateField);

        styleButton(fetchButton);
        styleButton(searchButton);
        styleButton(filterButton);
        styleButton(resetButton);
        styleButton(refreshButton);
        styleButton(openButton);
        styleButton(addBookmarkButton);
        styleButton(viewBookmarksButton);
        styleButton(applyDateFilterButton);
        styleButton(chooseDateButton);

        JPanel fetchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        fetchPanel.setBackground(PANEL_COLOR);

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

        JLabel sourceLabel = new JLabel("Source:");
        styleLabel(sourceLabel);

        filterPanel.add(sourceLabel);
        filterPanel.add(sourceComboBox);
        filterPanel.add(filterButton);
        filterPanel.add(resetButton);
        filterPanel.add(openButton);
        filterPanel.add(addBookmarkButton);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        datePanel.setBackground(PANEL_COLOR);

        JLabel dateRangeLabel = new JLabel("Date Range:");
        JLabel specificDateLabel = new JLabel("Specific Date:");

        styleLabel(dateRangeLabel);
        styleLabel(specificDateLabel);

        datePanel.add(dateRangeLabel);
        datePanel.add(dateRangeComboBox);
        datePanel.add(specificDateLabel);
        datePanel.add(specificDateField);
        datePanel.add(chooseDateButton);
        datePanel.add(applyDateFilterButton);

        JPanel topLeftContainer = new JPanel(new GridLayout(3, 1));
        topLeftContainer.setBackground(PANEL_COLOR);
        topLeftContainer.add(fetchPanel);
        topLeftContainer.add(filterPanel);
        topLeftContainer.add(datePanel);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        topRightPanel.setBackground(PANEL_COLOR);
        topRightPanel.add(viewBookmarksButton);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(PANEL_COLOR);

        topContainer.add(topLeftContainer, BorderLayout.CENTER);
        topContainer.add(topRightPanel, BorderLayout.EAST);

        topContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR));

        newsPage.add(topContainer, BorderLayout.NORTH);
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();

        verticalBar.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        horizontalBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 12));

        verticalBar.setBackground(BACKGROUND_COLOR);
        horizontalBar.setBackground(BACKGROUND_COLOR);

        verticalBar.setUnitIncrement(16);
        horizontalBar.setUnitIncrement(16);

        verticalBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = GRID_COLOR;
                this.trackColor = FIELD_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createInvisibleScrollButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createInvisibleScrollButton();
            }
        });

        horizontalBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = GRID_COLOR;
                this.trackColor = FIELD_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createInvisibleScrollButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createInvisibleScrollButton();
            }
        });
    }

    private JButton createInvisibleScrollButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    private void setupArticleTable() {
        String[] columnNames = {"Title", "Key Points", "Source", "Published Date"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        articleTable = new JTable(tableModel);
        articleTable.setFont(new Font("Arial", Font.PLAIN, 16));
        articleTable.setRowHeight(92);
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
        articleTable.getColumnModel().getColumn(1).setCellRenderer(new WrappedTextCellRenderer());
        articleTable.getColumnModel().getColumn(2).setCellRenderer(new CenterCellRenderer());
        articleTable.getColumnModel().getColumn(3).setCellRenderer(new CenterCellRenderer());

        TableColumn titleColumn = articleTable.getColumnModel().getColumn(0);
        TableColumn keyPointsColumn = articleTable.getColumnModel().getColumn(1);
        TableColumn sourceColumn = articleTable.getColumnModel().getColumn(2);
        TableColumn dateColumn = articleTable.getColumnModel().getColumn(3);

        titleColumn.setPreferredWidth(420);
        keyPointsColumn.setPreferredWidth(520);
        sourceColumn.setPreferredWidth(170);
        dateColumn.setPreferredWidth(170);

        JScrollPane scrollPane = new JScrollPane(articleTable);
        styleScrollPane(scrollPane);

        newsPage.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupStatusBar() {
        statusLabel = new JLabel("Loading today's hot topics...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(MUTED_TEXT_COLOR);
        statusLabel.setBackground(PANEL_COLOR);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, GRID_COLOR),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        newsPage.add(statusLabel, BorderLayout.SOUTH);
    }

    private void setupBookmarksPage() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR));

        JLabel titleLabel = new JLabel("Bookmarked Articles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        openBookmarkedArticleButton = new JButton("Open Article");
        removeBookmarkButton = new JButton("Remove Bookmark");
        backToNewsButton = new JButton("Back to News");

        styleButton(openBookmarkedArticleButton);
        styleButton(removeBookmarkButton);
        styleButton(backToNewsButton);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        titleRow.setBackground(PANEL_COLOR);
        titleRow.add(titleLabel);
        titleRow.add(openBookmarkedArticleButton);
        titleRow.add(removeBookmarkButton);

        bookmarkDateRangeComboBox = new JComboBox<>(new String[]{
                "All Dates",
                "Today",
                "Last 24 Hours",
                "Last 3 Days",
                "Last 1 Week"
        });

        bookmarkSpecificDateField = new JTextField(10);
        bookmarkSpecificDateField.setEditable(false);
        bookmarkSpecificDateField.setToolTipText("Click Choose Date to select a date");

        chooseBookmarkDateButton = new JButton("Choose Date");
        applyBookmarkDateFilterButton = new JButton("Apply Date Filter");
        resetBookmarkDateFilterButton = new JButton("Reset Date Filter");

        bookmarkDateRangeComboBox.setPreferredSize(new Dimension(150, 30));
        bookmarkSpecificDateField.setPreferredSize(new Dimension(120, 30));

        styleComboBox(bookmarkDateRangeComboBox);
        styleTextField(bookmarkSpecificDateField);
        styleButton(chooseBookmarkDateButton);
        styleButton(applyBookmarkDateFilterButton);
        styleButton(resetBookmarkDateFilterButton);

        JLabel bookmarkDateRangeLabel = new JLabel("Date Range:");
        JLabel bookmarkSpecificDateLabel = new JLabel("Specific Date:");

        styleLabel(bookmarkDateRangeLabel);
        styleLabel(bookmarkSpecificDateLabel);

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        dateRow.setBackground(PANEL_COLOR);
        dateRow.add(bookmarkDateRangeLabel);
        dateRow.add(bookmarkDateRangeComboBox);
        dateRow.add(bookmarkSpecificDateLabel);
        dateRow.add(bookmarkSpecificDateField);
        dateRow.add(chooseBookmarkDateButton);
        dateRow.add(applyBookmarkDateFilterButton);
        dateRow.add(resetBookmarkDateFilterButton);

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setBackground(PANEL_COLOR);
        leftPanel.add(titleRow);
        leftPanel.add(dateRow);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        rightPanel.setBackground(PANEL_COLOR);
        rightPanel.add(backToNewsButton);

        topPanel.add(leftPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        String[] bookmarkColumns = {"Title", "Key Points", "Source", "Published Date", "Saved At"};

        bookmarkTableModel = new DefaultTableModel(bookmarkColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookmarkTable = new JTable(bookmarkTableModel);
        bookmarkTable.setFont(new Font("Arial", Font.PLAIN, 16));
        bookmarkTable.setRowHeight(92);
        bookmarkTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        bookmarkTable.setBackground(BACKGROUND_COLOR);
        bookmarkTable.setForeground(TEXT_COLOR);
        bookmarkTable.setSelectionBackground(SELECTION_COLOR);
        bookmarkTable.setSelectionForeground(Color.WHITE);

        bookmarkTable.setShowGrid(true);
        bookmarkTable.setGridColor(GRID_COLOR);
        bookmarkTable.setIntercellSpacing(new Dimension(1, 1));
        bookmarkTable.setFillsViewportHeight(true);
        bookmarkTable.setFocusable(false);

        JTableHeader header = bookmarkTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(FIELD_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GRID_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        bookmarkTable.getColumnModel().getColumn(0).setCellRenderer(new WrappedTextCellRenderer());
        bookmarkTable.getColumnModel().getColumn(1).setCellRenderer(new WrappedTextCellRenderer());
        bookmarkTable.getColumnModel().getColumn(2).setCellRenderer(new CenterCellRenderer());
        bookmarkTable.getColumnModel().getColumn(3).setCellRenderer(new CenterCellRenderer());
        bookmarkTable.getColumnModel().getColumn(4).setCellRenderer(new CenterCellRenderer());

        bookmarkTable.getColumnModel().getColumn(0).setPreferredWidth(380);
        bookmarkTable.getColumnModel().getColumn(1).setPreferredWidth(480);
        bookmarkTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        bookmarkTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        bookmarkTable.getColumnModel().getColumn(4).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(bookmarkTable);
        styleScrollPane(scrollPane);

        bookmarkStatusLabel = new JLabel("No bookmarks added yet.");
        bookmarkStatusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bookmarkStatusLabel.setForeground(MUTED_TEXT_COLOR);
        bookmarkStatusLabel.setBackground(PANEL_COLOR);
        bookmarkStatusLabel.setOpaque(true);
        bookmarkStatusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, GRID_COLOR),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        bookmarksPage.add(topPanel, BorderLayout.NORTH);
        bookmarksPage.add(scrollPane, BorderLayout.CENTER);
        bookmarksPage.add(bookmarkStatusLabel, BorderLayout.SOUTH);
    }

    private void setupButtonActions() {
        fetchButton.addActionListener(e -> fetchByCategory());
        searchButton.addActionListener(e -> fetchByKeyword());
        filterButton.addActionListener(e -> filterCurrentArticles());
        applyDateFilterButton.addActionListener(e -> filterCurrentArticles());
        resetButton.addActionListener(e -> resetFilter());
        refreshButton.addActionListener(e -> refreshApp());
        openButton.addActionListener(e -> openSelectedArticle());

        chooseDateButton.addActionListener(e -> showCalendarPopup(specificDateField, chooseDateButton));

        addBookmarkButton.addActionListener(e -> addSelectedArticleToBookmarks());

        viewBookmarksButton.addActionListener(e -> {
            refreshBookmarkTable();
            cardLayout.show(mainPanel, "BOOKMARKS_PAGE");
        });

        backToNewsButton.addActionListener(e -> cardLayout.show(mainPanel, "NEWS_PAGE"));

        openBookmarkedArticleButton.addActionListener(e -> openSelectedBookmarkedArticle());
        removeBookmarkButton.addActionListener(e -> removeSelectedBookmark());

        chooseBookmarkDateButton.addActionListener(e ->
                showCalendarPopup(bookmarkSpecificDateField, chooseBookmarkDateButton)
        );

        applyBookmarkDateFilterButton.addActionListener(e -> filterBookmarksByDate());

        resetBookmarkDateFilterButton.addActionListener(e -> {
            bookmarkDateRangeComboBox.setSelectedIndex(0);
            bookmarkSpecificDateField.setText("");
            refreshBookmarkTable();
        });
    }

    private void showCalendarPopup(JTextField targetField, JButton anchorButton) {
        JPopupMenu calendarPopup = new JPopupMenu();
        calendarPopup.setBackground(PANEL_COLOR);
        calendarPopup.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));

        LocalDate today = LocalDate.now();

        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(PANEL_COLOR);
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setForeground(TEXT_COLOR);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JButton previousButton = new JButton("<");
        JButton nextButton = new JButton(">");

        styleButton(previousButton);
        styleButton(nextButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.add(previousButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 4, 4));
        daysPanel.setBackground(PANEL_COLOR);

        final YearMonth[] currentMonth = {YearMonth.from(today)};

        Runnable refreshCalendar = new Runnable() {
            @Override
            public void run() {
                daysPanel.removeAll();

                monthLabel.setText(
                        currentMonth[0].getMonth().toString() + " " + currentMonth[0].getYear()
                );

                String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

                for (String dayName : dayNames) {
                    JLabel dayLabel = new JLabel(dayName, JLabel.CENTER);
                    dayLabel.setForeground(MUTED_TEXT_COLOR);
                    dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
                    daysPanel.add(dayLabel);
                }

                LocalDate firstDayOfMonth = currentMonth[0].atDay(1);
                int firstDayPosition = firstDayOfMonth.getDayOfWeek().getValue() % 7;

                for (int i = 0; i < firstDayPosition; i++) {
                    JLabel emptyLabel = new JLabel("");
                    daysPanel.add(emptyLabel);
                }

                int daysInMonth = currentMonth[0].lengthOfMonth();

                for (int day = 1; day <= daysInMonth; day++) {
                    LocalDate selectedDate = currentMonth[0].atDay(day);

                    JButton dayButton = new JButton(String.valueOf(day));
                    dayButton.setFocusPainted(false);
                    dayButton.setFont(new Font("Arial", Font.PLAIN, 12));
                    dayButton.setBackground(FIELD_COLOR);
                    dayButton.setForeground(TEXT_COLOR);
                    dayButton.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));

                    if (selectedDate.equals(today)) {
                        dayButton.setBackground(SELECTION_COLOR);
                    }

                    dayButton.addActionListener(e -> {
                        targetField.setText(selectedDate.toString());
                        calendarPopup.setVisible(false);
                    });

                    daysPanel.add(dayButton);
                }

                daysPanel.revalidate();
                daysPanel.repaint();
            }
        };

        previousButton.addActionListener(e -> {
            currentMonth[0] = currentMonth[0].minusMonths(1);
            refreshCalendar.run();
        });

        nextButton.addActionListener(e -> {
            currentMonth[0] = currentMonth[0].plusMonths(1);
            refreshCalendar.run();
        });

        refreshCalendar.run();

        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(daysPanel, BorderLayout.CENTER);

        calendarPopup.add(calendarPanel);
        calendarPopup.show(anchorButton, 0, anchorButton.getHeight());
    }

    private void fetchHotTopics() {
        System.out.println("[DEFAULT] Fetching today's hot topics...");

        setLoading(true);

        new SwingWorker<List<NewsArticle>, Void>() {
            @Override
            protected List<NewsArticle> doInBackground() throws Exception {
                return newsService.fetchHotTopics();
            }

            @Override
            protected void done() {
                setLoading(false);

                try {
                    currentArticles = get();
                    updateSourceDropdown(currentArticles);
                    populateTable(currentArticles, "today's hot topics");
                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    showError(cause != null ? cause.getMessage() : ex.getMessage());
                }
            }
        }.execute();
    }

    private void fetchByCategory() {
        String category = (String) categoryComboBox.getSelectedItem();

        if (category == null || category.trim().isEmpty()) {
            statusLabel.setText("Please select a category.");
            return;
        }

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
        String selectedDateRange = (String) dateRangeComboBox.getSelectedItem();
        String specificDate = specificDateField.getText();

        System.out.println("[FILTER] Keyword: " + keyword
                + " | Source: " + selectedSource
                + " | Date Range: " + selectedDateRange
                + " | Specific Date: " + specificDate);

        if (currentArticles == null || currentArticles.isEmpty()) {
            statusLabel.setText("Please fetch news first before filtering.");
            return;
        }

        if (!filterEngine.hasAnyFilter(keyword, selectedSource, selectedDateRange, specificDate)) {
            populateTable(currentArticles, "current results");
            return;
        }

        List<NewsArticle> filteredArticles = filterEngine.applyFilters(
                currentArticles,
                keyword,
                selectedSource,
                selectedDateRange,
                specificDate
        );

        String context = filterEngine.buildFilterContext(
                keyword,
                selectedSource,
                selectedDateRange,
                specificDate
        );

        populateTable(filteredArticles, context);
    }

    private void resetFilter() {
        searchField.setText("");
        specificDateField.setText("");
        sourceComboBox.setSelectedIndex(0);
        dateRangeComboBox.setSelectedIndex(0);

        if (currentArticles == null || currentArticles.isEmpty()) {
            statusLabel.setText("No articles loaded. Fetching today's hot topics again.");
            fetchHotTopics();
            return;
        }

        populateTable(currentArticles, "all fetched articles");
    }

    private void refreshApp() {
        System.out.println("[REFRESH] Refreshing...");

        searchField.setText("");
        specificDateField.setText("");
        categoryComboBox.setSelectedItem("General");
        dateRangeComboBox.setSelectedIndex(0);

        sourceComboBox.removeAllItems();
        sourceComboBox.addItem("All Sources");

        tableModel.setRowCount(0);

        currentArticles = null;
        displayedArticles = null;

        cardLayout.show(mainPanel, "NEWS_PAGE");

        fetchHotTopics();
    }

    private void removeSelectedBookmark() {
        int selectedRow = bookmarkTable.getSelectedRow();

        if (selectedRow == -1) {
            bookmarkStatusLabel.setText("Please select a bookmark to remove.");
            return;
        }

        if (displayedBookmarks == null || selectedRow >= displayedBookmarks.size()) {
            bookmarkStatusLabel.setText("Could not find the selected bookmark.");
            return;
        }

        Bookmark removedBookmark = displayedBookmarks.get(selectedRow);

        bookmarks.remove(removedBookmark);

        saveBookmarksToFile();
        refreshBookmarkTable();

        if (removedBookmark != null && removedBookmark.getArticle() != null) {
            bookmarkStatusLabel.setText("Removed bookmark: " + removedBookmark.getArticle().getTitle());
        } else {
            bookmarkStatusLabel.setText("Bookmark removed.");
        }
    }

    private void saveBookmarksToFile() {
        try {
            bookmarkStorage.saveBookmarks(bookmarks);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bookmark was added, but could not be saved locally: " + ex.getMessage(),
                    "Bookmark Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openSelectedBookmarkedArticle() {
        int selectedRow = bookmarkTable.getSelectedRow();

        if (selectedRow == -1) {
            bookmarkStatusLabel.setText("Please select a bookmarked article to open.");
            return;
        }

        if (displayedBookmarks == null || selectedRow >= displayedBookmarks.size()) {
            bookmarkStatusLabel.setText("Could not find the selected bookmark.");
            return;
        }

        Bookmark selectedBookmark = displayedBookmarks.get(selectedRow);
        NewsArticle article = selectedBookmark.getArticle();

        if (article == null) {
            bookmarkStatusLabel.setText("This bookmark does not contain a valid article.");
            return;
        }

        String url = article.getUrl();

        if (url == null || url.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No URL available for this bookmarked article.",
                    "Missing URL",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Opening URLs is not supported on this system.",
                    "Unsupported Action",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Desktop.getDesktop().browse(new URI(url));
            bookmarkStatusLabel.setText("Opened: " + article.getTitle());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to open article: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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

    private void addSelectedArticleToBookmarks() {
        int selectedRow = articleTable.getSelectedRow();

        if (selectedRow == -1) {
            statusLabel.setText("Please select an article before bookmarking.");
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an article first.",
                    "No Article Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (displayedArticles == null || selectedRow >= displayedArticles.size()) {
            statusLabel.setText("Could not find the selected article.");
            return;
        }

        NewsArticle selectedArticle = displayedArticles.get(selectedRow);

        if (isAlreadyBookmarked(selectedArticle)) {
            statusLabel.setText("This article is already bookmarked.");
            JOptionPane.showMessageDialog(
                    this,
                    "This article is already bookmarked.",
                    "Duplicate Bookmark",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String savedAt = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a")
        );

        Bookmark bookmark = new Bookmark(selectedArticle, savedAt);
        bookmarks.add(bookmark);

        saveBookmarksToFile();

        statusLabel.setText("Bookmarked: " + selectedArticle.getTitle());
    }

    private boolean isAlreadyBookmarked(NewsArticle article) {
        if (article == null) {
            return false;
        }

        String articleUrl = article.getUrl();

        for (Bookmark bookmark : bookmarks) {
            NewsArticle savedArticle = bookmark.getArticle();

            if (savedArticle == null) {
                continue;
            }

            String savedUrl = savedArticle.getUrl();

            if (articleUrl != null
                    && savedUrl != null
                    && articleUrl.equalsIgnoreCase(savedUrl)) {
                return true;
            }

            if ((articleUrl == null || articleUrl.isBlank())
                    && article.getTitle() != null
                    && savedArticle.getTitle() != null
                    && article.getTitle().equalsIgnoreCase(savedArticle.getTitle())) {
                return true;
            }
        }

        return false;
    }

    private void refreshBookmarkTable() {
        displayedBookmarks = bookmarks;
        loadBookmarksIntoTable(displayedBookmarks);

        if (displayedBookmarks.isEmpty()) {
            bookmarkStatusLabel.setText("No bookmarks added yet.");
        } else {
            bookmarkStatusLabel.setText("Showing " + displayedBookmarks.size() + " bookmarked articles.");
        }
    }

    private void filterBookmarksByDate() {
        if (bookmarks == null || bookmarks.isEmpty()) {
            bookmarkStatusLabel.setText("No bookmarks available to filter.");
            return;
        }

        String selectedDateRange = (String) bookmarkDateRangeComboBox.getSelectedItem();
        String specificDate = bookmarkSpecificDateField.getText();

        List<NewsArticle> bookmarkArticles = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            if (bookmark != null && bookmark.getArticle() != null) {
                bookmarkArticles.add(bookmark.getArticle());
            }
        }

        List<NewsArticle> filteredArticles;

        if (filterEngine.hasSpecificDate(specificDate)) {
            filteredArticles = filterEngine.filterBySpecificDate(bookmarkArticles, specificDate);
        } else if (filterEngine.hasDateRange(selectedDateRange)) {
            filteredArticles = filterEngine.filterByDateRange(bookmarkArticles, selectedDateRange);
        } else {
            refreshBookmarkTable();
            return;
        }

        displayedBookmarks = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            if (bookmark == null || bookmark.getArticle() == null) {
                continue;
            }

            for (NewsArticle filteredArticle : filteredArticles) {
                if (isSameArticle(bookmark.getArticle(), filteredArticle)) {
                    displayedBookmarks.add(bookmark);
                    break;
                }
            }
        }

        loadBookmarksIntoTable(displayedBookmarks);

        bookmarkStatusLabel.setText(
                "Showing " + displayedBookmarks.size() + " bookmarked articles after date filtering."
        );
    }

    private void loadBookmarksIntoTable(List<Bookmark> bookmarksToShow) {
        bookmarkTableModel.setRowCount(0);

        if (bookmarksToShow == null) {
            return;
        }

        for (Bookmark bookmark : bookmarksToShow) {
            NewsArticle article = bookmark.getArticle();

            if (article == null) {
                continue;
            }

            bookmarkTableModel.addRow(new Object[]{
                    article.getTitle(),
                    buildKeyPoints(article),
                    article.getSource(),
                    formatPublishedDate(article.getPublishedAt()),
                    bookmark.getSavedAt()
            });
        }
    }

    private boolean isSameArticle(NewsArticle firstArticle, NewsArticle secondArticle) {
        if (firstArticle == null || secondArticle == null) {
            return false;
        }

        String firstUrl = firstArticle.getUrl();
        String secondUrl = secondArticle.getUrl();

        if (firstUrl != null
                && secondUrl != null
                && firstUrl.equalsIgnoreCase(secondUrl)) {
            return true;
        }

        String firstTitle = firstArticle.getTitle();
        String secondTitle = secondArticle.getTitle();

        return firstTitle != null
                && secondTitle != null
                && firstTitle.equalsIgnoreCase(secondTitle);
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
                    buildKeyPoints(article),
                    article.getSource(),
                    formatPublishedDate(article.getPublishedAt())
            });
        }

        statusLabel.setText("Showing " + articles.size() + " articles for " + context + ", ranked by relevance.");
    }

    private String buildKeyPoints(NewsArticle article) {
        String description = article.getDescription();

        if (description == null
                || description.trim().isEmpty()
                || description.equalsIgnoreCase("No description available.")) {
            return "• No short preview available. Open the article to read more.";
        }

        description = description.trim().replaceAll("\\s+", " ");

        if (description.length() > 230) {
            description = description.substring(0, 230).trim() + "...";
        }

        return "• " + description;
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
        addBookmarkButton.setEnabled(!loading);
        viewBookmarksButton.setEnabled(!loading);

        if (applyDateFilterButton != null) {
            applyDateFilterButton.setEnabled(!loading);
        }

        if (chooseDateButton != null) {
            chooseDateButton.setEnabled(!loading);
        }

        if (openBookmarkedArticleButton != null) {
            openBookmarkedArticleButton.setEnabled(!loading);
        }

        if (removeBookmarkButton != null) {
            removeBookmarkButton.setEnabled(!loading);
        }

        if (applyBookmarkDateFilterButton != null) {
            applyBookmarkDateFilterButton.setEnabled(!loading);
        }

        if (resetBookmarkDateFilterButton != null) {
            resetBookmarkDateFilterButton.setEnabled(!loading);
        }

        if (chooseBookmarkDateButton != null) {
            chooseBookmarkDateButton.setEnabled(!loading);
        }

        searchField.setEnabled(!loading);
        specificDateField.setEnabled(!loading);

        categoryComboBox.setEnabled(true);
        sourceComboBox.setEnabled(true);
        dateRangeComboBox.setEnabled(true);

        categoryComboBox.setBackground(FIELD_COLOR);
        categoryComboBox.setForeground(TEXT_COLOR);

        sourceComboBox.setBackground(FIELD_COLOR);
        sourceComboBox.setForeground(TEXT_COLOR);

        dateRangeComboBox.setBackground(FIELD_COLOR);
        dateRangeComboBox.setForeground(TEXT_COLOR);

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
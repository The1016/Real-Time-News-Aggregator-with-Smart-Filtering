package ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    private JComboBox<String> categoryComboBox;
    private JButton fetchButton;
    private JTextField searchField;
    private JButton searchButton;
    private JTable articleTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public MainFrame() {
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

        topPanel.add(categoryComboBox);
        topPanel.add(fetchButton);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        String[] columnNames = {"Title", "Source", "Published Date"};
        tableModel = new DefaultTableModel(columnNames, 0);

        tableModel.addRow(new Object[]{"Sample headline 1", "BBC", "2026-03-18"});
        tableModel.addRow(new Object[]{"Sample headline 2", "Reuters", "2026-03-18"});

        articleTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(articleTable);

        statusLabel = new JLabel("Ready");

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
}
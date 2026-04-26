package ui;

import javax.swing.JWindow;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class LoadingScreen extends JWindow {

    private final JPanel mainPanel;
    private float opacityValue = 1.0f;

    public LoadingScreen() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(18, 18, 18));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 130), 2));

        JLabel titleLabel = new JLabel("News Aggregator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(235, 255, 255));

        JLabel loadingLabel = new JLabel("Fetching today's hot topics...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        loadingLabel.setForeground(new Color(190, 210, 210));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(new Color(35, 35, 35));
        progressBar.setForeground(new Color(70, 130, 130));
        progressBar.setBorderPainted(false);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(loadingLabel, BorderLayout.CENTER);
        mainPanel.add(progressBar, BorderLayout.SOUTH);

        add(mainPanel);

        setSize(new Dimension(420, 220));
        setLocationRelativeTo(null);

        try {
            setOpacity(opacityValue);
        } catch (UnsupportedOperationException ignored) {
        }
    }

    public void fadeOut(Runnable afterFade) {
        Timer timer = new Timer(35, null);

        timer.addActionListener(e -> {
            opacityValue -= 0.05f;

            if (opacityValue <= 0) {
                timer.stop();
                setVisible(false);
                dispose();
                afterFade.run();
                return;
            }

            try {
                setOpacity(opacityValue);
            } catch (UnsupportedOperationException ex) {
                timer.stop();
                setVisible(false);
                dispose();
                afterFade.run();
            }
        });

        timer.start();
    }
}
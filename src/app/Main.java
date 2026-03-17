package app;

import javax.swing.SwingUtilities;
import ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        //"()-> this is a Lambda expression. It means to run the code later."
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
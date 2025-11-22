import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    protected Main mainApp;

    public MenuPanel(Main mainApp) {
        this.mainApp = mainApp;

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // ========== JUDUL ==========
        JLabel title = new JLabel("Gluttony Fall", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(new Color(255, 180, 0));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        // ========== TOMBOL ==========
        JButton startButton = new JButton("Mulai Game");
        JButton leaderButton = new JButton("Leaderboard");

        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        leaderButton.setFont(new Font("Arial", Font.BOLD, 18));

        startButton.setBackground(new Color(255, 180, 0));
        leaderButton.setBackground(new Color(255, 180, 0));

        startButton.setFocusPainted(false);
        leaderButton.setFocusPainted(false);

        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        leaderButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        startButton.addActionListener(e -> mainApp.showNameInput());

        leaderButton.addActionListener(e -> mainApp.showLeaderboard());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 20));

        buttonPanel.add(startButton);
        buttonPanel.add(leaderButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(30, 30, 30));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 100, 80, 100));
        centerPanel.add(buttonPanel);

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}

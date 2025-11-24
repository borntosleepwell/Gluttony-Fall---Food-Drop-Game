import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LeaderboardPanel leaderboardPanel; // <-- Simpan referensi LeaderboardPanel

    public Main() {
        setTitle("Gluttony Fall");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi LeaderboardPanel dan simpan referensinya
        leaderboardPanel = new LeaderboardPanel(this);

        mainPanel.add(new MenuPanel(this), "Menu");
        mainPanel.add(leaderboardPanel, "Leaderboard"); // <-- Tambahkan referensi yang disimpan
        mainPanel.add(new NameInputPanel(this), "NameInput");

        add(mainPanel);
        setVisible(true);
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "Menu");
    }

    public void showLeaderboard() {
        // Panggil refreshLeaderboard sebelum menampilkan panel
        // agar data terbaru dari database dimuat.
        leaderboardPanel.refreshLeaderboard();
        cardLayout.show(mainPanel, "Leaderboard");
    }

    public void showNameInput() {
        cardLayout.show(mainPanel, "NameInput");
    }

    public void showGame(String username) {
        // Pastikan GamePanel baru dibuat setiap kali game dimulai
        GamePanel game = new GamePanel(this, username);
        mainPanel.add(game, "Game");
        cardLayout.show(mainPanel, "Game");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
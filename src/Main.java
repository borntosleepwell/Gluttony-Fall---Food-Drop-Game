import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Main() {
        setTitle("Gluttony Fall");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new MenuPanel(this), "Menu");
        mainPanel.add(new LeaderboardPanel(this), "Leaderboard");
        mainPanel.add(new NameInputPanel(this), "NameInput");

        add(mainPanel);
        setVisible(true);
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "Menu");
    }

    public void showLeaderboard() {
        cardLayout.show(mainPanel, "Leaderboard");
    }

    public void showNameInput() {
        cardLayout.show(mainPanel, "NameInput");
    }

    public void showGame(String username) {
        GamePanel game = new GamePanel(this, username);
        mainPanel.add(game, "Game");
        cardLayout.show(mainPanel, "Game");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}

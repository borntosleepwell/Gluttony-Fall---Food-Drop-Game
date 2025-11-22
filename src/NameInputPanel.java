import javax.swing.*;
import java.awt.*;

public class NameInputPanel extends JPanel {

    protected Main mainApp;

    public NameInputPanel(Main mainApp) {
        this.mainApp = mainApp;

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // ===== JUDUL =====
        JLabel title = new JLabel("Masukkan Nama", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(255, 180, 0));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        // ===== TEXT FIELD =====
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Consolas", Font.PLAIN, 20));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(new Color(50, 50, 50));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(new Color(255, 180, 0));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(255, 180, 0)));

        // ===== TOMBOL MULAI =====
        JButton startButton = new JButton("Mulai");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBackground(new Color(255, 180, 0));
        startButton.setFocusPainted(false);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ===== TOMBOL KEMBALI =====
        JButton backButton = new JButton("« Kembali");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 180, 0));
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ===== AKSI TOMBOL =====
        startButton.addActionListener(e -> {
            String nama = nameField.getText().trim();

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Nama tidak boleh kosong!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Pindah ke game dengan nama pemain
            mainApp.showGame(nama);
        });

        backButton.addActionListener(e -> mainApp.showMenu());

        // ===== PANEL TENGAH =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1, 0, 20));
        centerPanel.setBackground(new Color(30, 30, 30));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 120, 60, 120));

        centerPanel.add(nameField);
        centerPanel.add(startButton);
        centerPanel.add(backButton);

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}

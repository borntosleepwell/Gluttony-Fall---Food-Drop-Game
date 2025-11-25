import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class MenuPanel extends JPanel {

    protected Main mainApp;
    private Image backgroundImage;
    private ImageIcon titleIcon;
    private Font pixelFont; 
    public MenuPanel(Main mainApp) {
        this.mainApp = mainApp;

        // 1. LOAD CUSTOM FONT DULU
        loadCustomFont();

        // 2. Load & Resize Gambar
        try {
            backgroundImage = new ImageIcon("assets/background2.png").getImage();
            ImageIcon originalTitle = new ImageIcon("assets/title.png");
            if (originalTitle.getIconWidth() > 0) {
                Image scaledTitleImg = originalTitle.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
                titleIcon = new ImageIcon(scaledTitleImg);
            }
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar.");
        }

        // 3. Setup Layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        
        // ================== JUDUL ==================
        JLabel titleLabel = new JLabel();
        if (titleIcon != null) {
            titleLabel.setIcon(titleIcon);
        } else {
            titleLabel.setText("GLUTTONY FALL");
            titleLabel.setForeground(Color.ORANGE);
            // GUNAKAN CUSTOM FONT (Ukuran besar 48f)
            titleLabel.setFont(pixelFont.deriveFont(48f)); 
        }

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0); 
        add(titleLabel, gbc);

        // ================== CONTAINER TRANSPARAN ==================
        JPanel buttonContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 150)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        buttonContainer.setOpaque(false);
        buttonContainer.setLayout(new GridLayout(2, 1, 0, 20)); 
        buttonContainer.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // ================== TOMBOL DENGAN CUSTOM FONT ==================
        JButton startButton = createPixelStyleButton("START");
        JButton leaderButton = createPixelStyleButton("LEADERBOARD");

        startButton.addActionListener(e -> mainApp.showNameInput());
        leaderButton.addActionListener(e -> mainApp.showLeaderboard());

        buttonContainer.add(startButton);
        buttonContainer.add(leaderButton);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0); 
        add(buttonContainer, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null && backgroundImage.getWidth(null) > 0) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(20, 20, 20));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // ==========================================================
    // METHOD BARU: LOAD FONT DARI FILE .TTF
    // ==========================================================
    private void loadCustomFont() {
        try {
            File fontFile = new File("assets/PixelifySans-Medium.ttf");
            
            if (fontFile.exists()) {
                // Buat font dasar dari file
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                
                // Registrasi ke sistem grafis (Opsional, tapi bagus untuk kompatibilitas)
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(baseFont);
                
                // Set default size ke 20 (nanti bisa diubah pakai deriveFont)
                pixelFont = baseFont.deriveFont(20f);
            } else {
                System.out.println("File font tidak ditemukan! Menggunakan Monospaced.");
                pixelFont = new Font("Monospaced", Font.BOLD, 20);
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            System.out.println("Gagal load font. Menggunakan Monospaced.");
            pixelFont = new Font("Monospaced", Font.BOLD, 20);
        }
    }

    // --- Helper: Membuat Tombol ---
    private JButton createPixelStyleButton(String text) {
        JButton btn = new JButton(text);
        
        btn.setPreferredSize(new Dimension(220, 55));
        
        // --- PENGGUNAAN CUSTOM FONT ---
        // deriveFont(float size) digunakan untuk mengubah ukuran font custom
        btn.setFont(pixelFont.deriveFont(Font.BOLD, 20f)); 
        
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(220, 120, 0)); 
        
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 160, 0)); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(220, 120, 0)); 
            }
        });

        return btn;
    }
}
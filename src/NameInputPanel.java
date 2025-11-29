import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.text.*;

public class NameInputPanel extends JPanel {

    protected Main mainApp;
    private Image backgroundImage;
    private Image scrollImage;
    private Font pixelFont;

    public NameInputPanel(Main mainApp) {
        this.mainApp = mainApp;
        
        // 1. Load Aset
        loadAssets(); 

        // 2. Setup Layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); 
        gbc.gridx = 0;

        // ===== 1. JUDUL =====
        JLabel titleLabel = new JLabel("MASUKKAN NAMA");
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setFont(pixelFont.deriveFont(48f)); 
        
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); 
        add(titleLabel, gbc);

        // ===== 2. INPUT FIELD (DENGAN GAMBAR KERTAS) =====
        
        // Panel khusus untuk menggambar kertas
        JPanel scrollPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (scrollImage != null) {
                    g.drawImage(scrollImage, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        scrollPanel.setOpaque(false);
        scrollPanel.setPreferredSize(new Dimension(400, 200)); // Ukuran Kertas
        scrollPanel.setLayout(new GridBagLayout()); // Agar textfield di tengah kertas

        // TextField Transparan di atas Kertas
        JTextField nameField = new JTextField();
        nameField.setOpaque(false); // Transparan
        nameField.setBorder(null);  // Hapus kotak border bawaan
        nameField.setHorizontalAlignment(JTextField.CENTER);
        
        // Font Coklat Tua (seperti tinta)
        nameField.setFont(pixelFont.deriveFont(32f)); 
        nameField.setForeground(new Color(60, 40, 20)); 
        nameField.setCaretColor(new Color(60, 40, 20)); // Kursor kedip warna coklat
        
        nameField.setPreferredSize(new Dimension(400, 50)); // Area ketik

        // Fitur limit text input
        int maxChars = 10; 

        // Ambil dokumen dari Text Field dan pasang Filter
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new DocumentFilter() {
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int newLength = currentLength - length + text.length();

                // Cek panjang. Jika <= 10, masukkan teks nama player
                if (newLength <= maxChars) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                int newLength = fb.getDocument().getLength() + string.length();
                
                if (newLength <= maxChars) {
                    super.insertString(fb, offset, string, attr);
                }
            }
        });

        scrollPanel.add(nameField); // Masukkan textfield ke kertas

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        add(scrollPanel, gbc);

        // ===== 3. TOMBOL
        JPanel buttonContainer = new JPanel(new GridLayout(1, 2, 20, 0)); // Sebelahan
        buttonContainer.setOpaque(false);

        JButton btnStart = createPixelStyleButton("MULAI");
        JButton btnBack = createPixelStyleButton("KEMBALI");

        // Aksi Tombol
        btnStart.addActionListener(e -> {
            String nama = nameField.getText().trim();
            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                mainApp.showGame(nama);
            }
        });

        btnBack.addActionListener(e -> mainApp.showMenu());

        buttonContainer.add(btnStart);
        buttonContainer.add(btnBack);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(buttonContainer, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Gambar Background Dungeon
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(20, 20, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Load aset yang diperlikan
    private void loadAssets() {
        try {
            // Background
            backgroundImage = new ImageIcon("assets/dungeon_bg.png").getImage();
            
            // Gambar Kertas Scroll (Pastikan file ini ada!)
            scrollImage = new ImageIcon("assets/paper.png").getImage();

            // Font Pixel custom
            File fontFile = new File("assets/PixelifySans-Medium.ttf");
            if (fontFile.exists()) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(baseFont);
                pixelFont = baseFont.deriveFont(20f);
            } else {
                pixelFont = new Font("Monospaced", Font.BOLD, 20);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pixelFont = new Font("Monospaced", Font.BOLD, 20);
        }
    }

    // ==========================================================
    // HELPER: TOMBOL STYLE MENU (CODE-BASED)
    // ==========================================================
    private JButton createPixelStyleButton(String text) {
        JButton btn = new JButton(text);
        
        btn.setPreferredSize(new Dimension(180, 55)); 
        
        // Font Custom
        btn.setFont(pixelFont.deriveFont(Font.BOLD, 24f)); 
        
        // Warna Dasar (Oranye Gelap) & Teks (Putih)
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(220, 120, 0)); 
        
        // Hapus dekorasi default
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efek Hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 160, 0)); // Lebih terang
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(220, 120, 0)); // Kembali normal
            }
        });

        return btn;
    }
}
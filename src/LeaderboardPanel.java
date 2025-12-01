import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class LeaderboardPanel extends JPanel {

    protected Main mainApp;
    private JTable table;
    private DefaultTableModel model;
    
    // Properti baru untuk latar belakang
    private Image backgroundImage; 
    private Font pixelFont; 

    public LeaderboardPanel(Main mainApp) {
        this.mainApp = mainApp;
        
        loadAssets(); // Panggil metode loadAssets()

        setLayout(new BorderLayout());
        setOpaque(false); // Penting: Agar paintComponent bisa menggambar latar
        // setBackground(new Color(30, 30, 30)); // Hapus atau ganti dengan setOpaque(false)

        JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER);
        title.setFont(pixelFont.deriveFont(Font.BOLD, 36f)); // Gunakan pixelFont dan ubah ukuran
        title.setForeground(new Color(255, 180, 0));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Username", "Score", "Time Used"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        
        // Atur warna agar kontras dengan latar dungeon gelap
        table.setBackground(new Color(0, 0, 0, 150)); // Latar tabel lebih gelap
        table.setForeground(Color.WHITE);
        table.setFont(pixelFont.deriveFont(18f)); // Gunakan pixelFont
        table.setRowHeight(35); // Tinggikan baris

        JTableHeader header = table.getTableHeader();
        header.setFont(pixelFont.deriveFont(20f)); // Gunakan pixelFont
        header.setBackground(new Color(255, 180, 0));
        header.setForeground(Color.BLACK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setOpaque(true); // Pastikan Opaque
                    // --- PENGATURAN BARU: RATA TENGAH ---
                    label.setHorizontalAlignment(JLabel.CENTER); // Pastikan Opaque
                }
                // Atur warna latar belakang untuk setiap cell
                c.setBackground(new Color(0, 0, 0, 150)); // 2. Hitam Transparan (Alpha 180)
                c.setForeground(Color.WHITE);
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(false); // **PENTING: Agar viewport transparan**
        scroll.getViewport().setBackground(new Color(0, 0, 0, 150)); // 3. Hitam Transparan
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        scroll.setOpaque(false); // 4. Set JScrollPane itu sendiri transparan

        add(scroll, BorderLayout.CENTER);

        // --- Tombol Kembali ---
        // Panggil helper function dari NameInputPanel untuk konsistensi style
        JButton backButton = createPixelStyleButton("« KEMBALI"); 
        backButton.addActionListener(e -> mainApp.showMenu());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false); // Set transparan
        bottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        bottom.add(backButton);

        add(bottom, BorderLayout.SOUTH);

        loadLeaderboard();
    }
    
    // --- Metode loadAssets() baru (Salinan dari NameInputPanel) ---
    private void loadAssets() {
        try {
            // Background Dungeon
            backgroundImage = new ImageIcon("assets/dungeon_bg.png").getImage();
            
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

    // --- Override paintComponent untuk menggambar latar belakang ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Latar belakang default jika gambar gagal dimuat
            g.setColor(new Color(20, 20, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // ==========================================================
    // HELPER: TOMBOL STYLE MENU (CODE-BASED) - Salinan dari NameInputPanel
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
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(255, 160, 0)); // Lebih terang
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(220, 120, 0)); // Kembali normal
            }
        });

        return btn;
    }

    // =============================
    // REFRESH LEADERBOARD (PUBLIK)
    // =============================
    public void refreshLeaderboard() {
        loadLeaderboard();
    }
    
    // =============================
    // LOAD DATA FROM DATABASE
    // =============================
    private void loadLeaderboard() {
        model.setRowCount(0); // clear

        // KoneksiDatabase.getTopScores() mengembalikan ArrayList<String[]>
        // dengan format: [0] username, [1] score, [2] time_used, [3] created_at
        ArrayList<String[]> data = KoneksiDatabase.getTopScores();

        for (String[] row : data) {
            // Karena tabel hanya 3 kolom (Username, Score, Time Used), kita ambil 3 data pertama
            model.addRow(new Object[]{
                row[0],
                row[1],
                row[2]
            });
        }
    }
}
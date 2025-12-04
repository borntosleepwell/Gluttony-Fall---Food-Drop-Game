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
    
    private Image backgroundImage; 
    private Font pixelFont; 

    public LeaderboardPanel(Main mainApp) {
        this.mainApp = mainApp;
        
        loadAssets();

        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER);
        title.setFont(pixelFont.deriveFont(Font.BOLD, 36f));
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
        
        table.setBackground(new Color(0, 0, 0, 150));
        table.setForeground(Color.WHITE);
        table.setFont(pixelFont.deriveFont(18f));
        table.setRowHeight(35);

        JTableHeader header = table.getTableHeader();
        header.setFont(pixelFont.deriveFont(20f));
        header.setBackground(new Color(255, 180, 0));
        header.setForeground(Color.BLACK);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setOpaque(true);
                    label.setHorizontalAlignment(JLabel.CENTER);
                }
                c.setBackground(new Color(0, 0, 0, 150));
                c.setForeground(Color.WHITE);
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(new Color(0, 0, 0, 150));
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        scroll.setOpaque(false);

        add(scroll, BorderLayout.CENTER);

        JButton backButton = createPixelStyleButton("« KEMBALI"); 
        backButton.addActionListener(e -> mainApp.showMenu());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        bottom.add(backButton);

        add(bottom, BorderLayout.SOUTH);

        loadLeaderboard();
    }
    
    private void loadAssets() {
        try {
            backgroundImage = new ImageIcon("assets/dungeon_bg.png").getImage();
            
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(20, 20, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private JButton createPixelStyleButton(String text) {
        JButton btn = new JButton(text);
        
        btn.setPreferredSize(new Dimension(180, 55)); 
        
        btn.setFont(pixelFont.deriveFont(Font.BOLD, 24f)); 
        
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(220, 120, 0)); 
        
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(255, 160, 0));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(220, 120, 0));
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
        model.setRowCount(0);
        ArrayList<String[]> data = KoneksiDatabase.getTopScores();

        for (String[] row : data) {
            model.addRow(new Object[]{
                row[0],
                row[1],
                row[2]
            });
        }
    }
}
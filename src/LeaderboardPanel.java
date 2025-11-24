import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class LeaderboardPanel extends JPanel {

    protected Main mainApp;
    private JTable table;
    private DefaultTableModel model;

    public LeaderboardPanel(Main mainApp) {
        this.mainApp = mainApp;

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // ====== TITLE ======
        JLabel title = new JLabel("Leaderboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(255, 180, 0));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ====== TABLE ======
        // Removed tanggal column
        String[] columns = {"Username", "Score", "Time Used"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setBackground(new Color(40, 40, 40));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Consolas", Font.PLAIN, 14));
        table.setRowHeight(28);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(255, 180, 0));
        header.setForeground(Color.BLACK);

        // Center align data
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(30, 30, 30));
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(scroll, BorderLayout.CENTER);

        // ====== BACK BUTTON ======
        JButton backButton = new JButton("« Kembali");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setBackground(new Color(255, 180, 0));
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainApp.showMenu());

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(30, 30, 30));
        bottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        bottom.add(backButton);

        add(bottom, BorderLayout.SOUTH);

        // Load leaderboard di constructor tetap dilakukan untuk inisialisasi awal
        loadLeaderboard();
    }

    // =============================
    // REFRESH LEADERBOARD (PUBLIK)
    // Dipanggil dari Main.java sebelum menampilkan panel
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
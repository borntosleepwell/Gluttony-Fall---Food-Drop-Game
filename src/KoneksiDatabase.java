import java.sql.*;
import java.util.ArrayList;

public class KoneksiDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/gluttony_fall";
    private static final String USER = "root";
    private static final String PASS = "";

    // =====================
    // CONNECT DATABASE
    // =====================
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Gagal koneksi database!");
            e.printStackTrace();
            return null;
        }
    }

    // =====================
    // GET OR CREATE USER
    // =====================
    public static int getOrCreateUser(String username) {
        int userId = -1;

        try (Connection conn = getConnection()) {

            if (conn == null) return -1;

            // Cek user sudah ada
            String checkQuery = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement check = conn.prepareStatement(checkQuery)) {
                check.setString(1, username);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }

            // Jika tidak ada → buat baru
            String insertQuery = "INSERT INTO users(username) VALUES(?)";
            try (PreparedStatement insert = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, username);
                insert.executeUpdate();

                try (ResultSet generated = insert.getGeneratedKeys()) {
                    if (generated.next()) {
                        userId = generated.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

    // =====================
    // SAVE SCORE
    // =====================
    public static void saveScore(String username, int score, int timeUsed) {
        int userId = getOrCreateUser(username);

        if (userId == -1) {
            System.out.println("User tidak ditemukan & gagal dibuat.");
            return;
        }

        try (Connection conn = getConnection()) {

            if (conn == null) return;

            String sql = "INSERT INTO scores(user_id, score, time_used) VALUES(?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ps.setInt(2, score);
                ps.setInt(3, timeUsed);

                ps.executeUpdate();
                System.out.println("Skor berhasil disimpan.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =====================
    // GET TOP 10 LEADERBOARD
    // =====================
    public static ArrayList<String[]> getTopScores() {
        ArrayList<String[]> list = new ArrayList<>();

        String query =
            "SELECT u.username, s.score, s.time_used, s.created_at " +
            "FROM scores s JOIN users u ON s.user_id = u.id " +
            "ORDER BY s.score DESC, s.time_used ASC " +
            "LIMIT 10";

        try (Connection conn = getConnection()) {

            if (conn == null) return list;

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(query)) {

                while (rs.next()) {
                    list.add(new String[]{
                        rs.getString("username"),
                        rs.getString("score"),
                        rs.getString("time_used"),
                        rs.getString("created_at")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}

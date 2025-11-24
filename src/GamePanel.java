import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel {

    private static final int MAX_OBJECTS = 25;   // batas objek di layar
    private static final int FOOD_SIZE   = 48;   // ukuran gambar makanan
    private static final int OBJ_SIZE    = 48;   // ukuran bomb & boost juga

    private Main mainApp;
    private String username;
    private Clip backgroundClip;
    private long gameStartTime;
    private Player player; 
    private Image backgroundImg;
    private ArrayList<GameObject> objects; // makanan, bom, time boost
    private Timer gameLoop;
    private Timer spawnTimer;
    private Timer countdownTimer;
    private Image[] foodImages;
    private Random random = new Random();

    private int score = 0;
    private int timeLeft = 60;

    private JLabel scoreLabel;
    private JLabel timerLabel;

    private boolean gameRunning = false;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public GamePanel(Main mainApp, String username) {
        this.mainApp = mainApp;
        this.username = username;

        loadFoodImages();
        
        // Load background
        backgroundImg = new ImageIcon("assets/Background.png").getImage();

        setLayout(null);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setBounds(20, 10, 200, 30);
        add(scoreLabel);

        timerLabel = new JLabel("Time: 60");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setBounds(360, 10, 200, 30);
        add(timerLabel);

        // Player
        player = new Player();

        // List objek jatuh
        objects = new ArrayList<>();

        // Countdown sebelum game dimulai
        new CountdownPanel(this, this::startGame);
    }

    private void playBackgroundMusic() {
        try {
            File file = new File("assets/sound.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); 
        } catch (Exception e) {
            System.out.println("Gagal memutar musik: " + e.getMessage());
        }
    }

    // ============================================================
    // START GAME
    // ============================================================
    private void startGame() {
        gameRunning = true;
        playBackgroundMusic();
        gameStartTime = System.currentTimeMillis();
        // Loop game: ~60 FPS
        gameLoop = new Timer(16, e -> updateGame());
        gameLoop.start();

        // Spawn objek tiap 1 detik
        spawnTimer = new Timer(1000, e -> spawnObjects());
        spawnTimer.start();

        // Hitung mundur waktu
        countdownTimer = new Timer(1000, e -> {
            if (!gameRunning) return;

            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);

            if (timeLeft <= 0) {
                endGame();
            }
        });
        countdownTimer.start();

        // Listener mouse (gerakin player)
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                player.updatePosition(e.getX(), getWidth());
            }
        });
    }

    // ============================================================
    // UPDATE GAME
    // ============================================================
    private void updateGame() {
        if (!gameRunning) return;

        for (int i = 0; i < objects.size(); i++) {
            GameObject obj = objects.get(i);
            obj.update();

            // keluar layar bawah → buang
            if (obj.y > getHeight()) {
                objects.remove(i);
                i--;
                continue;
            }

            // tabrakan dengan player
            if (player.isColliding(obj)) {
                handleCollision(obj);
                objects.remove(i);
                i--;
            }
        }

        repaint();
    }

    // ============================================================
    // LOAD GAMBAR MAKANAN  
    // ============================================================
    private void loadFoodImages() {
        File folder = new File("assets/food");
        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".png"));

        if (files == null || files.length == 0) {
            foodImages = new Image[0];
            System.out.println("Tidak menemukan gambar makanan di assets/food");
            return;
        }

        foodImages = new Image[files.length];
        for (int i = 0; i < files.length; i++) {
            Image raw = new ImageIcon(files[i].getPath()).getImage();
            // scale sekali di awal, jadi saat draw tidak berat
            foodImages[i] = raw.getScaledInstance(FOOD_SIZE, FOOD_SIZE, Image.SCALE_SMOOTH);
        }
    }

    // ============================================================
    // HANDLE COLLISION
    // ============================================================
    private void handleCollision(GameObject obj) {
        if (obj instanceof Food) {
            score += 10;
        } else if (obj instanceof TimeBoost) {
            timeLeft += 10;
        } else if (obj instanceof Bomb) {
            timeLeft -= 10;
            if (timeLeft < 0) timeLeft = 0;
        }

        scoreLabel.setText("Score: " + score);
        timerLabel.setText("Time: " + timeLeft);

        if (timeLeft <= 0) {
            endGame();
        }
    }

    // ============================================================
    // SPAWN OBJECTS
    // ============================================================
    private void spawnObjects() {
        if (objects.size() >= MAX_OBJECTS) return;

        int type = random.nextInt(10); // 0-9

        GameObject obj;

        if (type < 7) obj = new Food();          // 70%
        else if (type == 7) obj = new TimeBoost(); // 10%
        else obj = new Bomb();                    // 20%

        objects.add(obj);
    }


    // ============================================================
    // PAINT
    // ============================================================
    @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // ===== Gambar background dulu =====
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);

            // ===== Gambar player =====
            player.draw(g);

            // ===== Gambar objek jatuh =====
            for (GameObject obj : objects) {
                obj.draw(g);
            }
        }
    
    private void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }

    // ============================================================
    // END GAME + RESULT SCREEN
    // ============================================================
    private void endGame() {
        if (!gameRunning) return;
        gameRunning = false;
        if (gameLoop != null) gameLoop.stop();
        if (spawnTimer != null) spawnTimer.stop();
        if (countdownTimer != null) countdownTimer.stop();

        long timeEnd = System.currentTimeMillis();
        int timeUsed = (int) ((timeEnd - gameStartTime) / 1000); 
        if (timeUsed < 0) timeUsed = 0;
        stopBackgroundMusic();

        // Simpan ke database
        KoneksiDatabase.saveScore(username, score, timeUsed);

        // ===== GANTI TAMPILAN JADI RESULT SCREEN =====
        removeAll();
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        JLabel title = new JLabel("Game Over!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(new Color(255, 180, 0));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(20, 20, 20));

        JLabel nameLabel = new JLabel("Player : " + username, SwingConstants.CENTER);
        JLabel scoreLabelR = new JLabel("Score  : " + score, SwingConstants.CENTER);
        JLabel timeLabelR = new JLabel("Waktu Bermain : " + timeUsed + " detik", SwingConstants.CENTER);

        Font f = new Font("Arial", Font.BOLD, 22);
        Color c = Color.WHITE;
        for (JLabel l : new JLabel[]{nameLabel, scoreLabelR, timeLabelR}) {
            l.setFont(f);
            l.setForeground(c);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(l);
            center.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(20, 20, 20));
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        JButton playAgain = new JButton("Main Lagi");
        JButton menuBtn   = new JButton("Menu Utama");
        JButton leaderBtn = new JButton("Leaderboard");

        JButton[] buttons = {playAgain, menuBtn, leaderBtn};
        for (JButton b : buttons) {
            b.setFont(new Font("Arial", Font.BOLD, 18));
            b.setBackground(new Color(255, 180, 0));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            bottom.add(b);
        }

        playAgain.addActionListener(e -> mainApp.showGame(username));
        menuBtn.addActionListener(e -> mainApp.showMenu());
        leaderBtn.addActionListener(e -> mainApp.showLeaderboard());

        add(bottom, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // ============================================================
    // ================   INNER CLASSES   ==========================
    // ============================================================

    // ---------- SUPERCLASS GAME OBJECT ----------
    abstract class GameObject {
        int x, y, speed;
        Image img;

        public abstract void update();
        public abstract void draw(Graphics g);
    }

    // ---------- FOOD (+10) ----------
    class Food extends GameObject {

        double angle = 0;        // sudut rotasi
        double rotationSpeed;    // kecepatan muter

        public Food() {
            if (foodImages.length == 0) {
                // fallback kalau gambar tidak ada
                img = null;
            } else {
                img = foodImages[random.nextInt(foodImages.length)];
            }

            int w = getWidth();
            if (w <= 0) w = 500; // fallback

            x = random.nextInt(w - FOOD_SIZE);
            y = -FOOD_SIZE;
            speed = 4 + random.nextInt(3); // 4–6

            rotationSpeed = (Math.random() * 0.05) + 0.01; // lebih lambat, ringan
        }

        public void update() {
            y += speed;
            angle += rotationSpeed;
        }

        public void draw(Graphics g) {
            if (img == null) return;

            Graphics2D g2 = (Graphics2D) g;
            int size = FOOD_SIZE;

            g2.rotate(angle, x + size / 2.0, y + size / 2.0);
            g2.drawImage(img, x, y, size, size, null);
            g2.rotate(-angle, x + size / 2.0, y + size / 2.0);
        }
    }

    // ---------- TIME BOOST (+10 TIME) ----------
    class TimeBoost extends GameObject {

        double angle = 0;
        double rotationSpeed;

        public TimeBoost() {
            img = new ImageIcon("assets/Boost.png").getImage()
                    .getScaledInstance(OBJ_SIZE, OBJ_SIZE, Image.SCALE_SMOOTH);

            int w = getWidth();
            if (w <= 0) w = 500;

            x = random.nextInt(w - OBJ_SIZE);
            y = -OBJ_SIZE;
            speed = 4;

            rotationSpeed = (Math.random() * 0.04) + 0.01;
        }

        public void update() {
            y += speed;
            angle += rotationSpeed;
        }

        public void draw(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int size = OBJ_SIZE;

            g2.rotate(angle, x + size / 2.0, y + size / 2.0);
            g2.drawImage(img, x, y, size, size, null);
            g2.rotate(-angle, x + size / 2.0, y + size / 2.0);
        }
    }

    // ---------- BOMB (-10 TIME) ----------
    class Bomb extends GameObject {

        double angle = 0;
        double rotationSpeed;

        public Bomb() {
            img = new ImageIcon("assets/Bom.png").getImage()
                    .getScaledInstance(OBJ_SIZE, OBJ_SIZE, Image.SCALE_SMOOTH);

            int w = getWidth();
            if (w <= 0) w = 500;

            x = random.nextInt(w - OBJ_SIZE);
            y = -OBJ_SIZE;
            speed = 5 + random.nextInt(3); // 5–7

            rotationSpeed = (Math.random() * 0.06) + 0.02;  // boleh sedikit lebih cepat
        }

        public void update() {
            y += speed;
            angle += rotationSpeed;
        }

        public void draw(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int size = OBJ_SIZE;

            g2.rotate(angle, x + size / 2.0, y + size / 2.0);
            g2.drawImage(img, x, y, size, size, null);
            g2.rotate(-angle, x + size / 2.0, y + size / 2.0);
        }
    }

    // ---------- PLAYER ----------
    class Player {
        int x = 200, y = 550;
        Image idle  = new ImageIcon("assets/Atas.png").getImage();
        Image left  = new ImageIcon("assets/Kiri.png").getImage();
        Image right = new ImageIcon("assets/Kanan.png").getImage();

        Image currentImg = idle;

        long lastMoveTime = 0;

        // Timer idle hanya 1x dibuat
        Timer idleTimer;

        public Player() {
            idleTimer = new Timer(100, e -> {
                long now = System.currentTimeMillis();
                if (now - lastMoveTime >= 500) {
                    currentImg = idle;
                }
            });
            idleTimer.start();
        }

        void updatePosition(int mouseX, int panelWidth) {
            int oldX = x;
            x = mouseX - 40;

            if (x < oldX) currentImg = left;
            else if (x > oldX) currentImg = right;

            lastMoveTime = System.currentTimeMillis();
        }

        boolean isColliding(GameObject obj) {
            Rectangle p = new Rectangle(x, y, 80, 80);
            Rectangle o = new Rectangle(obj.x, obj.y, 40, 40);
            return p.intersects(o);
        }

        void draw(Graphics g) {
            g.drawImage(currentImg, x, y, 80, 80, null);
        }
    }


    // ---------- COUNTDOWN PANEL ----------
    class CountdownPanel {
        public CountdownPanel(JPanel parent, Runnable onFinish) {
            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 100));
            label.setForeground(Color.WHITE);
            label.setBounds(0, 200, 500, 200);

            parent.add(label);
            parent.repaint();

            new Thread(() -> {
                try {
                    for (int i = 3; i >= 1; i--) {
                        label.setText("" + i);
                        Thread.sleep(700);
                    }
                    label.setText("GO!");
                    Thread.sleep(700);
                    parent.remove(label);
                    parent.repaint();
                    onFinish.run();
                } catch (Exception ignored) {}
            }).start();
        }
    }
}

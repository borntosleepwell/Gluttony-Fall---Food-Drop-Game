import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel {

    static final int MAX_OBJECTS = 25;   
    
    public static double speedMultiplier = 1.0;
    private Main mainApp;
    private Font pixelFont; 
    private String username;
    private Clip backgroundClip;
    private long gameStartTime;
    private Player player; 
    private Image backgroundImg;
    private ArrayList<GameObject> objects; 
    private Timer gameLoop;
    private Timer spawnTimer;
    private Timer countdownTimer;
    private Random random = new Random();

    private int score = 0;
    private int timeLeft = 60;

    private JLabel scoreLabel;
    private JLabel timerLabel;

    private boolean gameRunning = false;

    public GamePanel(Main mainApp, String username) {
        this.mainApp = mainApp;
        this.username = username;

        loadCustomFont();   

        backgroundImg = new ImageIcon("assets/Background.png").getImage();

        setLayout(null);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(pixelFont.deriveFont(18)); 
        scoreLabel.setBounds(20, 10, 200, 30);
        add(scoreLabel);

        timerLabel = new JLabel("Time: 60");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(pixelFont.deriveFont(18)); 
        timerLabel.setBounds(360, 10, 200, 30);
        add(timerLabel);

        player = new Player();
        objects = new ArrayList<>();

        SwingUtilities.invokeLater(() -> {
            player.centerPlayer(GamePanel.this.getWidth());
            repaint();
        });

        SwingUtilities.invokeLater(() -> showReadyCountdown(this::startGame));
            
    }

    private void loadCustomFont() {
        try {
            File fontFile = new File("assets/PixelifySans-Medium.ttf");
            
            if (fontFile.exists()) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(baseFont);
                
                pixelFont = baseFont.deriveFont(Font.PLAIN, 20f);
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
    
    private void showReadyCountdown(Runnable onFinish) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(pixelFont.deriveFont(100f));
        label.setForeground(Color.WHITE);
        label.setBounds(0, 200, getWidth(), 200);

        add(label);
        repaint();

        new Thread(() -> {
            try {
                for (int i = 3; i >= 1; i--) {
                    label.setText("" + i);
                    Thread.sleep(700);
                }
                label.setText("GO!");
                Thread.sleep(700);

                remove(label);
                repaint();
                onFinish.run();

            } catch (Exception ignored) {}
        }).start();
    }

    private void startGame() {
        gameRunning = true;
        playBackgroundMusic();
        gameStartTime = System.currentTimeMillis();
        gameLoop = new Timer(16, e -> updateGame());
        gameLoop.start();

        spawnTimer = new Timer(1000, e -> spawnObjects());
        spawnTimer.start();

        countdownTimer = new Timer(1000, e -> {
            if (!gameRunning) return;

            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);

            if (timeLeft <= 0) {
                endGame();
            }
        });
        countdownTimer.start();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                player.updatePosition(e.getX(), getWidth());
            }
        });
    }

    private double getSpeedMultiplier() {
        if (!gameRunning) return 1.0;  

        long elapsedMs  = System.currentTimeMillis() - gameStartTime;
        long elapsedSec = elapsedMs / 1000;

        long minuteIndex = elapsedSec / 60; 

        return 1.0 + 0.5 * minuteIndex;
    }

    private void updateGame() {
        if (!gameRunning) return;

        speedMultiplier = getSpeedMultiplier();

        for (int i = 0; i < objects.size(); i++) {
            GameObject obj = objects.get(i);
            obj.update();

            if (obj.y > getHeight()) {
                objects.remove(i);
                i--;
                continue;
            }

            if (player.isColliding(obj)) {
                handleCollision(obj);
                objects.remove(i);
                i--;
            }
        }

        repaint();
    }

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

    private void spawnObjects() {
        if (objects.size() >= MAX_OBJECTS) return;

        int panelWidth = getWidth();
        if (panelWidth <= 0) return;

        int type = random.nextInt(10); 

        GameObject obj;

        if (type < 7) {
            obj = new Food(panelWidth);     
        } else if (type == 7) {
            obj = new TimeBoost(panelWidth);           
        } else {
            obj = new Bomb(panelWidth);               
        }

        objects.add(obj);
    }



    @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);

            player.draw(g);

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

        KoneksiDatabase.saveScore(username, score, timeUsed);

        removeAll();
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        JLabel title = new JLabel("Game Over!", SwingConstants.CENTER);
        
        title.setFont(pixelFont.deriveFont(36f));
        title.setForeground(new Color(255, 180, 0));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel nameLabel = new JLabel("Player : " + username, SwingConstants.CENTER);
        JLabel scoreLabelR = new JLabel("Score  : " + score, SwingConstants.CENTER);
        JLabel timeLabelR = new JLabel("Waktu Bermain : " + timeUsed + " detik", SwingConstants.CENTER);

         timerLabel.setFont(pixelFont.deriveFont(18)); 

        Font f = pixelFont.deriveFont(18);
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
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        JButton playAgain = new JButton("Main Lagi");
        JButton menuBtn   = new JButton("Menu Utama");
        JButton leaderBtn = new JButton("Leaderboard");

        JButton[] buttons = {playAgain, menuBtn, leaderBtn};
        for (JButton b : buttons) {
            b.setFont(pixelFont.deriveFont(18));
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
}

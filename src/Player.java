import javax.swing.*;
import java.awt.*;

public class Player {

    public static final int PLAYER_WIDTH  = 250;
    public static final int PLAYER_HEIGHT = 250;

    int x = 200, y = 450;

    Image idle  = new ImageIcon("assets/Atas.png").getImage();
    Image left  = new ImageIcon("assets/Kiri.png").getImage();
    Image right = new ImageIcon("assets/Kanan.png").getImage();

    Image currentImg = idle;

    long lastMoveTime = 0;
    Timer idleTimer;

    public Player() {
        idleTimer = new Timer(100, e -> {
            long now = System.currentTimeMillis();
            if (now - lastMoveTime >= 100) {
                currentImg = idle;
            }
        });
        idleTimer.start();
    }

    public void centerPlayer(int panelWidth) {
        x = (panelWidth / 2) - (PLAYER_WIDTH / 2);
    }

    public void updatePosition(int mouseX, int panelWidth) {
        int oldX = x;

        // Player tepat di bawah cursor
        x = mouseX - (PLAYER_WIDTH / 2);

        // batas kiri & kanan
        if (x < 0) x = -50;
        if (x + 200 > panelWidth) {
            x = panelWidth - 200;
        }

        // ganti sprite arah
        if (x < oldX) currentImg = left;
        else if (x > oldX) currentImg = right;

        lastMoveTime = System.currentTimeMillis();
    }

    public boolean isColliding(GameObject obj) {
        int hitW = PLAYER_WIDTH / 2;
        int hitH = PLAYER_HEIGHT / 2;

        int hitX = x + (PLAYER_WIDTH / 4);
        int hitY = y + (PLAYER_HEIGHT / 3);

        Rectangle p = new Rectangle(hitX, hitY, hitW, hitH);
        Rectangle o = new Rectangle(obj.x, obj.y, 40, 40);

        return p.intersects(o);
    }

    public void draw(Graphics g) {
        g.drawImage(currentImg, x, y, PLAYER_WIDTH, PLAYER_HEIGHT, null);
    }
}

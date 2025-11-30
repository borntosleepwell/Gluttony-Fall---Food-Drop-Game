import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TimeBoost extends GameObject {

    private static final Random random = new Random();
    private static final int SIZE = 48;

    private double angle = 0;
    private double rotationSpeed;

    public TimeBoost(int panelWidth) {
        img = new ImageIcon("assets/Boost.png").getImage()
                .getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);

        if (panelWidth <= SIZE) {
            x = 0;
        } else {
            x = random.nextInt(panelWidth - SIZE);
        }

        y = -SIZE;
        speed = 6; 

        rotationSpeed = (Math.random() * 0.04) + 0.01;
    }

    @Override
    public void update() {
        double m = GamePanel.speedMultiplier;
        int dy = (int) Math.max(1, Math.round(speed * m));
        y += dy;
        angle += rotationSpeed;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(angle, x + SIZE / 2.0, y + SIZE / 2.0);
        g2.drawImage(img, x, y, SIZE, SIZE, null);
        g2.rotate(-angle, x + SIZE / 2.0, y + SIZE / 2.0);
    }
}

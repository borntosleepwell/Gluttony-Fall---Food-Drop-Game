import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class Food extends GameObject {

    public static final int FOOD_SIZE = 45;
    private static Image[] foodImages;
    private static final Random random = new Random();
    double angle = 0;
    double rotationSpeed;

    static {
        loadImages();
    }

    private static void loadImages() {
        File folder = new File("assets/food");
        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".png"));

        if (files == null || files.length == 0) {
            foodImages = new Image[0];
            System.out.println("Tidak ada gambar food");
            return;
        }

        foodImages = new Image[files.length];

        for (int i = 0; i < files.length; i++) {
            Image raw = new ImageIcon(files[i].getPath()).getImage();
            foodImages[i] = raw.getScaledInstance(FOOD_SIZE, FOOD_SIZE, Image.SCALE_SMOOTH);
        }

        System.out.println("Food images loaded: " + foodImages.length);
    }

    public Food(int panelWidth) {
        if (foodImages.length == 0) img = null;
        else img = foodImages[random.nextInt(foodImages.length)];

        x = random.nextInt(panelWidth - FOOD_SIZE);
        y = -FOOD_SIZE;

        speed = 4; 
        rotationSpeed = (Math.random() * 0.05) + 0.01;
    }

    @Override
    public void update() {
        double m = GamePanel.speedMultiplier;
        y += (int) Math.max(1, Math.round(speed * m));
        angle += rotationSpeed;
    }

    @Override
    public void draw(Graphics g) {
        if (img == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(angle, x + FOOD_SIZE/2.0, y + FOOD_SIZE/2.0);
        g2.drawImage(img, x, y, FOOD_SIZE, FOOD_SIZE, null);
        g2.rotate(-angle, x + FOOD_SIZE/2.0, y + FOOD_SIZE/2.0);
    }
}

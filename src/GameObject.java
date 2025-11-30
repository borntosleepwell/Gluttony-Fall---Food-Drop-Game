import java.awt.*;

public abstract class GameObject {
    public int x, y, speed;
    public Image img;

    public abstract void update();
    public abstract void draw(Graphics g);
}

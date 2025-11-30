import javax.swing.*;
import java.awt.*;

public class CountdownPanel {

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

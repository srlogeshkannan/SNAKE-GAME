import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel implements ActionListener {

    private final int TILE_SIZE = 25;
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (TILE_SIZE * TILE_SIZE);
    private final int DELAY = 100;

    private final int x[] = new int[GAME_UNITS];
    private final int y[] = new int[GAME_UNITS];

    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;

    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;

    public GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // Grid lines (optional)
            /*
            for (int i = 0; i < SCREEN_HEIGHT / TILE_SIZE; i++) {
                g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * TILE_SIZE, SCREEN_WIDTH, i * TILE_SIZE);
            }
            */

            g.setColor(Color.red);
            g.fillOval(appleX, appleY, TILE_SIZE, TILE_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
                }
            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten,
                    (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH / TILE_SIZE)) * TILE_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT / TILE_SIZE)) * TILE_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U': y[0] = y[0] - TILE_SIZE; break;
            case 'D': y[0] = y[0] + TILE_SIZE; break;
            case 'L': x[0] = x[0] - TILE_SIZE; break;
            case 'R': x[0] = x[0] + TILE_SIZE; break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        // check if head touches walls
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Score
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, 
                (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 3);

        // Game Over text
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", 
                (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
            }
        }
    }
}
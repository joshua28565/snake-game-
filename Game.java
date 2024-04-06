import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.Random;
import java.util.Timer;

public class Game extends JPanel {
    private Timer timer;
    private Snake snake;
    private Point cherry;
    private int points = 0;
    private int best = 0;
    private GameStatus status;
    private boolean didLoadCherryImage = true;

    private static Font FONT_M = new Font("Haettenschweiler", Font.PLAIN, 30);// font and size for score
    private static Font FONT_M_ITALIC = new Font("Haettenschweiler", Font.ITALIC, 40); // font and size for the press
                                                                                       // chuchu
    private static Font FONT_L = new Font("Gill sans Ultra bold", Font.PLAIN, 80); // font and sixe of game over
    private static Font FONT_XL = new Font("Gill sans Ultra bold", Font.PLAIN, 80); // font and size of the title of the
                                                                                    // game
    private static int WIDTH = 830; // width of the frame
    private static int HEIGHT = 640; // height of the frame
    private static int DELAY = 40; // speed of the snake
    
    

    Color colors[] = { // contaner of the color 
            Color.BLUE,
            Color.RED,
            Color.CYAN,
            Color.YELLOW,
            Color.MAGENTA,
            Color.GREEN
           

    };

    // newUpdate1
    Random random = new Random();

    // user defined method 
    public Game() {

        didLoadCherryImage = false;

        addKeyListener(new KeyListener());
        setFocusable(true); // if false it can't be played
        setBackground(Color.ORANGE); // color of bg color

        snake = new Snake(WIDTH / 2, HEIGHT / 2);
        status = GameStatus.NOT_STARTED;
        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        render(g);

        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        snake.move();

        if (cherry != null && snake.getHead().intersects(cherry, 10)) { // speed of food to locate itself to another
                                                                        // location
            snake.addTail();

            cherry = null; // the food will be randomly located
            if (points %5 == 0) {
                points += 3;
            } else {
                points++; // if the snake ate the food it will become longer
            }
        }

        if (cherry == null) {
            spawnCherry();
        }

        checkForGameOver();
    }

    private void reset() {
        points = 0;
        cherry = null;
        snake = new Snake(WIDTH / 2, HEIGHT / 2); // divide the width and height of the snake
        setStatus(GameStatus.RUNNING);
    }

    private void setStatus(GameStatus newStatus) {
        switch (newStatus) {
            case RUNNING:
                timer = new Timer();
                timer.schedule(new GameLoop(), 5, DELAY); // delay of loop of the game
                break;
            case PAUSED:
                timer.cancel();
                

            case GAME_OVER:
                timer.cancel();
                best = points > best ? points : best;
                break;
            case NOT_STARTED:
                break;
            default:
                break;
        }

        status = newStatus;
    }

    private void togglePause() {
        setStatus(status == GameStatus.PAUSED ? GameStatus.RUNNING : GameStatus.PAUSED);
    }

    private void checkForGameOver() {
        Point head = snake.getHead();
        boolean hitBoundary = head.getX() <= 20
                || head.getX() >= WIDTH + 10
                || head.getY() <= 40
                || head.getY() >= HEIGHT + 30;

        boolean ateItself = false;

        for (Point t : snake.getTail()) {
            ateItself = ateItself || head.equals(t);
        }

        if (hitBoundary || ateItself) {
            setStatus(GameStatus.GAME_OVER);
        }
    }

    public void drawCenteredString(Graphics g, String text, Font font, int y) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (WIDTH - metrics.stringWidth(text)) / 2;

        g.setFont(font);
        g.drawString(text, x, y);
    }

    private void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        Color rgbColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)); // rgb nung food
        g2d.setColor(new Color(0, 0, 0)); // color of start and color of score & high score
        g2d.setFont(FONT_M);

        if (status == GameStatus.NOT_STARTED) {
            drawCenteredString(g2d, " MEDUSA.IO", FONT_XL, 220);
            drawCenteredString(g2d, "GAME", FONT_XL, 320);
            drawCenteredString(g2d, "( Press a random key to begin )", FONT_M_ITALIC, 420); // vertical

            return;
        }

        Point p = snake.getHead();

        g2d.drawString("SCORE: " + String.format("%03d", points), 20, 30);
        g2d.drawString("SCORE TO BEAT: " + String.format("%03d", best),670, 30);

        if (cherry != null) {
            if (didLoadCherryImage) {

            } else {

                // newUpdate2 | 
                if (points %5 == 0) {
                    g2d.setColor(rgbColor); // color random
                    g2d.fillOval(cherry.getX(), cherry.getY(), 25, 25); // food size
                    
                } else {
                    g2d.setColor(Color.BLACK); // food color
                    g2d.fillOval(cherry.getX(), cherry.getY(), 25, 25); // food size
                    g2d.setColor(new Color(0, 0, 0));
                }
            }
        }
        
        if (status == GameStatus.GAME_OVER) { 
            g2d.setColor(Color.BLACK); 
            drawCenteredString(g2d, "    (Press  enter  to  start  again)", FONT_M_ITALIC, 420);
            drawCenteredString(g2d, "    (Press  backspace  to  exit the game)", FONT_M_ITALIC, 460);
            drawCenteredString(g2d, " Game Over! ", FONT_L, 310);// vertical
        }

        if (status == GameStatus.PAUSED) { // press p to pause
            g2d.drawString("Paused", 320, 300); // width and height
        }

        g2d.setColor(new Color(0, 102, 0)); // snake color
        g2d.fillRect(p.getX(), p.getY(), 25, 25); // size of head

        for (int i = 0, size = snake.getTail().size(); i < size; i++) { // increaseness of snake
            Point t = snake.getTail().get(i);

            g2d.fillRect(t.getX(), t.getY(), 25, 25); // size of tail
        }

        g2d.setColor(new Color(0, 0, 0)); // border color
        g2d.setStroke(new BasicStroke(8)); // thickness
        g2d.drawRect(28, 40, WIDTH, HEIGHT); // border
    }

    public void spawnCherry() {
        cherry = new Point((new Random()).nextInt(WIDTH - 60) + 20,
                (new Random()).nextInt(HEIGHT - 60) + 40);
    }

    private class KeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (status == GameStatus.RUNNING) { // key directions
                switch (key) {
                    case KeyEvent.VK_LEFT:
                        snake.turn(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        snake.turn(Direction.RIGHT);
                        break;
                    case KeyEvent.VK_UP:
                        snake.turn(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        snake.turn(Direction.DOWN);
                        break;
                }
            }
            // this is calling method 
            if (status == GameStatus.NOT_STARTED) { // To start the game 
                setStatus(GameStatus.RUNNING);
            }

            if (status == GameStatus.GAME_OVER && key == KeyEvent.VK_ENTER) { //press enter to reset the game 
                reset();
            }
            if (status == GameStatus.GAME_OVER && key == KeyEvent.VK_BACK_SPACE) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Are you sure you want to exit?"); //press backsapce to exit the game
                System.exit(0);
            }

            if (key == KeyEvent.VK_P) { //press P to pause the the game
                togglePause();
            }
        }
    }

    private class GameLoop extends java.util.TimerTask {
        public void run() {
            update();
            repaint();
        }
    }
}
// this is the GUI of the game
import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main extends JFrame {
    public Main() {
        initUI();
    }

    private void initUI() {
        add(new Game());

        setTitle("SNAKE GAME");
        setSize(900, 750); // main frame size

        setLocationRelativeTo(null);
        setResizable(false); // if true the frame can be maximize or minimize
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Main ex = new Main();
            ex.setVisible(true); // if false the frame of the will not appear
        });
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int frameWidth = 360;
    int frameHeight = 640;

    int playerStartPosX = frameWidth / 8;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;
    Player player;

    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    ArrayList<Pipe> pipes;

    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;

    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;
    int score = 0;
    boolean gameOver = false;
    JLabel scoreLabel;

    public FlappyBird() {
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<>();

        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipesCooldown.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        add(scoreLabel);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);
        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }

        // Menampilkan skor saat bermain
        scoreLabel.setText("Score: " + score);

        // Menampilkan total skor saat game over
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
            g.drawString("Game Over", frameWidth / 2 - 120, frameHeight / 2 - 20);
            g.drawString("Score: " + score, frameWidth / 2 - 100, frameHeight / 2 + 20);
        }
    }

    public void move() {
        if (!gameOver) {
            player.setPosY(player.getPosY() + player.getVelocityY());
            player.setPosY(Math.max(player.getPosY(), 0));
            player.setVelocityY(player.getVelocityY() + gravity);

            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

                // Deteksi tabrakan antara burung dan pipa
                if (player.getBounds().intersects(pipe.getBounds()) || player.getPosY() >= frameHeight - player.getHeight()) {
                    gameOver = true;
                    gameLoop.stop();
                    pipesCooldown.stop();
                }

                // Deteksi jika pipa sudah lewat burung
                if (pipe.getPosX() + pipe.getWidth() < player.getPosX() && !pipe.isPassed()) {
                    pipe.setPassed(true);
                    if (i % 2 == 0) { // Setiap kali pipa dilewati dua kali
                        score++; // Tambah skor
                    }
//                    i++; // Loncat ke pipa berikutnya (pipa bawah)
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
        move();
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setVelocityY(-10);
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame(); // Restart permainan jika tombol "R" ditekan setelah game over
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void placePipes() {
        int randomPipePosY = (int) (pipeStartPosY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPipePosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPipePosY + pipeHeight + openingSpace, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    // Memulai ulang permainan
    private void restartGame() {
        player.setPosY(playerStartPosY);
        pipes.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        pipesCooldown.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

package Zombie_Zombie;
//board which include all sprites

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Board extends JPanel implements ActionListener, Constants {
    private Timer timer, zTimer;
    private Men men;
    private Missile missile;
    private ArrayList<Zombie.DroppedMissile> droppedMissiles = new ArrayList<Zombie.DroppedMissile>();
    private Missile.Explosion explosion;
    private ArrayList<Zombie> zombies;
    private boolean ingame, started;   //game current state
    private Random random = new Random();
    private Image background;
    private int level = 1;
    private int count = 0;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());

        setFocusable(true);
        setBackground(Color.GRAY);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        //animation timer
        timer = new Timer(DELAY, this);
    }

    public void initGame() {
        MAdapter mAdapter = new MAdapter();
        addMouseListener(mAdapter);
        addMouseMotionListener(mAdapter);

        background = new ImageIcon(getClass().getResource("images/background.png")).getImage();
        ingame = true;  //game state
        started = true;
        Sound.MAIN_THEME.loop();
        Sound.ZOMBIE.loop();

        //sprites initiation
        men = new Men(B_WIDTH/2, B_HEIGHT/2);
        zombies = new ArrayList<Zombie>();
        initZombie();
        zombieTimer();
        timer.start();
    }

    public void zombieTimer() {
        zTimer = new Timer(ZOMBIES_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initZombie();
            }
        });
        zTimer.start();
    }

    public void initZombie() {
        //randomly initiate zombies
        int randomChoice, randomX, randomY;
        for (int i = 0; i < NUM_ZOMBIES + 3 * level; i++) {
            randomChoice = random.nextInt(4);
            switch (randomChoice) {
                case 0 : randomX = 0;
                    randomY = random.nextInt(B_HEIGHT);
                    break;
                case 1 : randomX = B_WIDTH;
                    randomY = random.nextInt(B_HEIGHT);
                    break;
                case 2 : randomX = random.nextInt(B_WIDTH);
                    randomY = 0;
                    break;
                case 3 : randomX = random.nextInt(B_WIDTH);
                    randomY = B_HEIGHT;
                    break;
                default: randomX = 0;
                    randomY = 0;
                    break;
            }
            zombies.add(new Zombie(randomX, randomY, men));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //draw init screen
        if (!started)
            g.drawImage(new ImageIcon(getClass().getResource("images/start.png")).getImage(),
                B_WIDTH/2 - 150, B_HEIGHT/2 - 100, this);


        if (timer.isRunning()) {
            drawObjects(g);
        } else if (started) {
            drawGameOver(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics g) {
        //draw background
        g.drawImage(background, 0, 0, this);

        //draw zombie
        for (Zombie a : zombies) {
            if (a.isVisible()) {
                g.drawImage(a.getImage(), a.getX(), a.getY(), this);
            }
        }

        //draw dropped missile
        for (Zombie.DroppedMissile dm : droppedMissiles) {
            if (dm.isVisible()) {
                g.drawImage(dm.getImage(), dm.getX(), dm.getY(), this);
            }
        }

        //draw men
        if (men.isVisible()) {
            g.drawImage(men.getImage(), men.getX(), men.getY(), this);
        }

        //draw missile
        if (missile != null && missile.isVisible()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.rotate(missile.getAngle(), missile.getX() + (missile.width / 2),
                    missile.getY() + (missile.height / 2));
            g2d.drawImage(missile.getImage(), missile.getX(), missile.getY(), this);
            g2d.dispose();
        }

        //draw explosion
        if (explosion != null && explosion.isVisible()) {
            g.drawImage(explosion.getImage(), explosion.getX() - explosion.getImage().getWidth(null)/2,
                    explosion.getY() - explosion.getImage().getHeight(null)/2, this);
        }

        //write score
        g.setColor(Color.BLACK);
        String msg = "Level: " + level +
                "   Zombies left: " + (level * 10 + (level - 1) * 10 - Zombie.getNumDeadZombies()) +
                "   Missiles left: " + men.getNumMissile();
        Font small = new Font("Courier", Font.BOLD, 16);
        FontMetrics fm = getFontMetrics(small);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2, 45);
    }

    private void drawGameOver(Graphics g) {
        String msg = "Game Over";
        Font big = new Font("Helvetica", Font.BOLD, 60);
        FontMetrics fm = getFontMetrics(big);

        g.setColor(new Color(0x61240B));
        g.setFont(big);
        g.drawString(msg, (B_WIDTH - fm.stringWidth(msg)) / 2,
                B_HEIGHT / 3);

        msg = "You have reached the " + level + " level";
        Font small = new Font("Courier", Font.BOLD, 20);
        FontMetrics fm1 = getFontMetrics(small);

        g.setColor(new Color(0x000000));
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - fm1.stringWidth(msg)) / 2,
                B_HEIGHT / 2);
        msg = Zombie.getNumDeadZombies() + " zombies killed";
        g.drawString(msg, (B_WIDTH - fm1.stringWidth(msg)) / 2,
                B_HEIGHT / 2 + 25);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        inGame();
        changeLevel();

        updateMen();
        if (missile != null) updateMissile();
        updateZombies();

        checkCollisions();
        repaint();
    }

    private void inGame() {
        if (!ingame) {
            count++;
            if (count > 150) {
                timer.stop();
                zTimer.stop();
                Sound.ZOMBIE.stop();
            }
        }
    }

    private void changeLevel() {
        if (level * 10 + (level - 1) * 10 - Zombie.getNumDeadZombies() <= 0) {
            level++;
            Sound.LEVEL.play();
        }
    }

    private void updateMen() {
        if (men.isAlive()) {
            men.move();
        } else {
            ingame = false;
        }
    }

    private void updateMissile() {
        if (missile.isVisible()) {
            missile.move();
        } else {
            explosion = missile.getExplosion();
            Sound.MISSILE_FLY.stop();
            Sound.EXPLOSION.play();
            missile = null;
        }
    }

    private void updateZombies() {
        for (int i = 0; i < zombies.size(); i++) {
            Zombie a = zombies.get(i);
            if (!a.isAlive()) {
                a.deadSliding(explosion.getX(), explosion.getY());
            } else if (a.isVisible() && a.isAlive() && men.isAlive()) {
                a.move();
            }
        }
    }

    public void checkCollisions() {
        //zombies and men
        Rectangle r1 = men.getBounds();
        for (Zombie zombie : zombies) {
            if (!zombie.isAlive) continue;
            Rectangle r2 = zombie.getBounds();
            if (r1.intersects(r2)) {
                if (men.isAlive()) Sound.SCREAM.play();
                men.setAlive(false);
                men.setImage(8);
            }
        }

        //men and dropped misiles
        for (int i = 0; i < droppedMissiles.size(); i++) {
            Zombie.DroppedMissile dm = droppedMissiles.get(i);
            Rectangle r2 = dm.getBounds();
            if (r1.intersects(r2)) {
                men.addNumMissile();
                droppedMissiles.remove(dm);
            }
        }

        //zombies and explosion
        if (explosion != null && explosion.isVisible()) {
            Rectangle r2 = explosion.getBounds();
            for (Zombie zombie : zombies) {
                Rectangle r3 = zombie.getBounds();
                if (r2.intersects(r3)) {
                    if (zombie.isDropMissile() && zombie.isAlive())
                        droppedMissiles.add(zombie.getDroppedMissile());
                    if (zombie.isAlive()) Zombie.growNumDeadZombies();
                    zombie.setAlive(false);
                    zombie.refreshCount();
                }
            }
        }

        //zombies and zombies
        for (int i = 0; i < zombies.size() - 1; i++) {
            Rectangle r2 = zombies.get(i).getBounds();

            for (int j = i + 1; j < zombies.size(); j++) {
                Rectangle r3 = zombies.get(j).getBounds();

                if (r2.intersects(r3)) zombies.get(i).stay();
                else zombies.get(i).setTemporary();
            }
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if (started)
                men.keyReleased(e);
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (!started) initGame();
            if (started)
                men.keyPressed(e);
        }
    }

    private class MAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseEvent.BUTTON1 &&
                    missile == null && men.isAlive && men.getNumMissile() > 0) {
                men.mousePressed(mouseEvent);
                missile = men.getMissile();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            men.mouseMoved(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //cursor changing
            setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                    new ImageIcon(getClass().getResource("images/cursor.png")).getImage(),
                    new Point(getX() + 15, getY() + 15),"custom cursor"));
        }
    }
}

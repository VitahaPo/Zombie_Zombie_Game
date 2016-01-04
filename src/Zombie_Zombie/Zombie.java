package Zombie_Zombie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class Zombie extends Sprite implements Constants{
    private static int numDeadZombies = 0;
    private double dx, dy;
    private Men men;
    private double mx, my; //men relative coordinates
    private double cos_angle, sin_angle;
    private int tempX, tempY;
    private Timer timer;
    private int count;
    private Random random = new Random();

    public Zombie(int x, int y, Men men) {
        super(x, y);
        this.men = men;
        initZombie();
    }

    private void initZombie() {
        loadImage("images/zombie1.png", "images/zombie2.png", "images/zombie_dead.png");
        setImage(0);
        getImageDimensions();
        imageChanging();
    }

    private void imageChanging() {
        timer = new javax.swing.Timer(ZOMBIES_ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //image of dead zombie
                if (!isAlive) {
                    setImage(2);
                    timer.stop();
                    return;
                }

                //Alive zombie
                if (image == images.get(0)) setImage(1);
                else setImage(0);
            }
        });
        timer.start();
    }

    public void move() {
        //relative men coordinates
        mx = men.getX() - getX();
        my = men.getY() - getY();

        //angle calculation
        cos_angle = mx / Math.sqrt(my * my + mx * mx);
        sin_angle = my / Math.sqrt(my * my + mx * mx);

        //zombie coordinates update
        x += ZOMBIE_SPEED * cos_angle;
        y += ZOMBIE_SPEED * sin_angle;
    }

    //when dead zombies slide to the end of screen
    public void deadSliding(int exp_x, int exp_y) {
        //relative men coordinates
        mx = exp_x - getX();
        my = exp_y - getY();

        //angle calculation
        cos_angle = mx / Math.sqrt(my * my + mx * mx);
        sin_angle = my / Math.sqrt(my * my + mx * mx);

        //zombie speed update
        count++;
        if (count < 100) {
            dx = -DEAD_ZOMBIE_SPEED * cos_angle / Math.pow(1.05, count);
        }
        else dx = 0;
        if (count < 100) {
            dy = -DEAD_ZOMBIE_SPEED * sin_angle / Math.pow(1.05, count);
        }
        else dy = 0;

        //border limit
        if (x <= 0) {
            x = 0;
            dx = 0;
            dy = 0;
        }
        if (y <= 0) {
            y = 0;
            dy = 0;
            dx = 0;
        }
        if (y >= (B_HEIGHT - height)) {
            y = B_HEIGHT - height;
            dy = 0;
            dx = 0;
        }
        if (x >= (B_WIDTH - width)) {
            x = B_WIDTH - width;
             dx = 0;
             dy = 0;
        }

        //zombie coordinates update
        x += dx;
        y += dy;
    }

    //save present position
    public void setTemporary() {
        tempX = getX();
        tempY = getY();
    }

    //stay on the present position
    public void stay() {
        x = tempX;
        y = tempY;
    }

    //refresh stopping count if zombie body explode again
    public void refreshCount() {
        count = 0;
    }

    //check to randomly drop missile from dead zombie
    public boolean isDropMissile() {
        int koreanRandom = random.nextInt(RANDOM_CHANCE);
        if (koreanRandom == 1)
            return true;
        else return false;
    }

    public DroppedMissile getDroppedMissile() {
        return new DroppedMissile(getX(), getY());
    }

    public class DroppedMissile extends Sprite {
        //this class describe missile which
        // drops from zombie when it die

        public DroppedMissile(int x, int y) {
            super(x, y);
            initDroppedMissile();
        }

        private void initDroppedMissile() {
            loadImage("images/weapon.png");
            setImage(0);
            getImageDimensions();
        }
    }

    public static int getNumDeadZombies() {
        return numDeadZombies;
    }

    public static void growNumDeadZombies() {
        Zombie.numDeadZombies++;
    }
}

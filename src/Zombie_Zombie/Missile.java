package Zombie_Zombie;

import java.awt.*;
import java.util.Random;

public class Missile extends Sprite implements Constants {
    private double missile_dest_X, missile_dest_Y; //missile destination in relative coordinates
    private double cos_angle, sin_angle;  //angle between missile and x axis
    private double angle;
    private double relative_X, relative_Y;
    private Random random = new Random();
    private Explosion explosion;

    public Missile(int x, int y, double missile_dest_X, double missile_dest_Y) {
        super(x, y);
        this.missile_dest_X = missile_dest_X - x;
        this.missile_dest_Y = missile_dest_Y - y;
        relative_X = x;
        relative_Y = y;
        trigonometry();
        initMissile();
    }

    private void trigonometry() {
        //standard trigonometry evaluations
        cos_angle = missile_dest_X / Math.sqrt(missile_dest_Y * missile_dest_Y + missile_dest_X * missile_dest_X);
        sin_angle = missile_dest_Y / Math.sqrt(missile_dest_Y * missile_dest_Y + missile_dest_X * missile_dest_X);
    }

    private void initMissile() {
        loadImage("images/missile.png");
        setImage(0);
        getImageDimensions();
    }

    public double getAngle() {
        angle = Math.acos(cos_angle) * (sin_angle > 0 ? 1 : -1);
        return angle;
    }

    public void move() {
        //coordinate calculation
        x += MISSILE_SPEED * cos_angle * random.nextDouble();   //missile shaking
        y += MISSILE_SPEED * sin_angle * random.nextDouble();

        //final destination
        if (Math.abs(x - relative_X) >= Math.abs(missile_dest_X) &&
                Math.abs(y - relative_Y) >= Math.abs(missile_dest_Y)) {
            explosion = new Explosion(getX(), getY());
            explosion.startIt();
            visible = false;
        }

        //border
        else if (x > B_WIDTH || y > B_HEIGHT || x < 0 || y < 0) {
            explosion = new Explosion(getX(), getY());
            explosion.startIt();
            visible = false;
        }
    }

    public Explosion getExplosion() {
        return explosion;
    }

    public class Explosion extends Sprite implements Runnable {
        private Thread thread;

        public Explosion(int x, int y) {
            super(x, y);
            initExplosion();
        }

        private void initExplosion() {
            loadImage("images/explosion1.png", "images/explosion2.png", "images/explosion3.png",
                    "images/explosion4.png", "images/explosion5.png");
            setImage(0);
            getImageDimensions();
            thread = new Thread(this);
        }

        @Override
        public Rectangle getBounds() {
            return new Rectangle(this.getX() - EXPLOSION_RANGE/2,
                    this.getY() - EXPLOSION_RANGE/2,
                    EXPLOSION_RANGE, EXPLOSION_RANGE);
        }

        public void startIt() {
            thread.start();
        }

        public void run() {
            //time of explosion and image changing
            try {
                for (int i = 1; i < images.size(); i++) {
                    Thread.sleep(EXPLOSION_DELAY/images.size());
                    setImage(i);
                }
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            visible = false;
        }
    }
}
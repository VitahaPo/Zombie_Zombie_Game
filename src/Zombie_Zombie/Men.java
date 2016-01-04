package Zombie_Zombie;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Men extends Sprite implements Constants {
    private double dx, dy;
    private double angle;
    private double acceleration_x, acceletation_y;
    private int countX, countY;           //need for stopping
    private Missile missile;
    private double missile_dest_X, missile_dest_Y;      //missile destination coordinates
    private int numMissile = INITIAL_MISSILE_NUM;

    public Men(int x, int y) {
        super(x, y);
        initMen();
    }

    private void initMen() {
        loadImage("images/men1.png", "images/men2.png", "images/men3.png",
                "images/men4.png", "images/men5.png", "images/men6.png",
                "images/men7.png", "images/men8.png", "images/men_dead.png");
        setImage(0);
        getImageDimensions();
    }

    private void imageChanging() {
        if (angle >= -Math.PI / 8 && angle < Math.PI / 8) setImage(4);
        else if (angle >= Math.PI / 8 && angle < 3 * Math.PI / 8) setImage(5);
        else if (angle >= 3 * Math.PI / 8 && angle < 5 * Math.PI / 8) setImage(6);
        else if (angle >= 5 * Math.PI / 8 && angle < 7 * Math.PI / 8) setImage(7);
        else if (angle >= 7 * Math.PI / 8 || angle < -7 * Math.PI / 8) setImage(0);
        else if (angle >= -7 * Math.PI / 8 && angle < -5 * Math.PI / 8) setImage(1);
        else if (angle >= -5 * Math.PI / 8 && angle < -3 * Math.PI / 8) setImage(2);
        else if (angle >= -3 * Math.PI / 8 && angle < -Math.PI / 8) setImage(3);
    }

    public double getCursorAngle() {
        //calculate current angle betwen cursor and X axis
        double cos_angle = missile_dest_X / Math.sqrt(missile_dest_Y * missile_dest_Y + missile_dest_X * missile_dest_X);
        double sin_angle = missile_dest_Y / Math.sqrt(missile_dest_Y * missile_dest_Y + missile_dest_X * missile_dest_X);
        return angle = Math.acos(cos_angle) * (sin_angle > 0 ? 1 : -1);
    }

    public void move() {
        imageChanging();

        //speed calculation
        dx += acceleration_x;
        dy += acceletation_y;

        /*  to do: create better coordinate definition
        //angle calculation
        cos_angle = dx / Math.sqrt(dy * dy + dx * dx);
        sin_angle = dy / Math.sqrt(dy * dy + dx * dx);
        */

        //coordinate calculation
        x += dx;
        y += dy;

        //speed limit
        dx = Math.min(Math.max(dx, -MEN_MAX_SPEED), MEN_MAX_SPEED);
        dy = Math.min(Math.max(dy, -MEN_MAX_SPEED), MEN_MAX_SPEED);

        //chosing image
        angle = getCursorAngle();
        imageChanging();

        //stopping
        stopping();

        //border limit
        if (x < 0 || y < 0 || y > (B_HEIGHT - height) || x > (B_WIDTH - width)) {
            isAlive = false;
            setImage(8);
        }
    }

    private void stopping() {
        if (acceleration_x == 0) {
            countX++;
            if (countX < 100 && dx != 0) {
                dx /= 1.03;
            }
            else dx = 0;
        }
        if (acceletation_y == 0) {
            countY++;
            if (countY < 100 && dy != 0) {
                dy /= 1.03;
            }
            else dy = 0;
        }
    }

    public int getNumMissile() {
        return numMissile;
    }

    public void addNumMissile() {
        numMissile++;
        Sound.PICK_WEAPON.play();
    }

    public void subNumMissile() {
        numMissile--;
    }
    public Missile getMissile() {
        return missile;
    }

    //men fire the missile
    public void fire(double xx, double yy) {
        subNumMissile();
        Sound.MISSILE.play();
        Sound.MISSILE_FLY.loop();
        missile = new Missile(getX() + width/2, getY() + height/2, xx, yy );
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            acceleration_x = -MEN_ACCELERATION;
        }
        if (key == KeyEvent.VK_RIGHT) {
            acceleration_x = MEN_ACCELERATION;
        }
        if (key == KeyEvent.VK_UP) {
            acceletation_y = -MEN_ACCELERATION;
        }
        if (key == KeyEvent.VK_DOWN) {
            acceletation_y = MEN_ACCELERATION;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            acceleration_x = 0;
            countX = 0;
        }
        if (key == KeyEvent.VK_RIGHT) {
            acceleration_x = 0;
            countX = 0;
        }
        if (key == KeyEvent.VK_UP) {
            acceletation_y = 0;
            countY = 0;
        }
        if (key == KeyEvent.VK_DOWN) {
            acceletation_y = 0;
            countY = 0;
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        fire(mouseEvent.getX(), mouseEvent.getY());
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        missile_dest_X = mouseEvent.getX() - x;
        missile_dest_Y = mouseEvent.getY() - y;
    }
}

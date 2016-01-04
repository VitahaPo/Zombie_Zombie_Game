package Zombie_Zombie;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Sprite {
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected boolean visible;
    protected boolean isAlive = true;
    protected ArrayList<Image> images;
    protected Image image;

    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
        visible = true;
    }

    protected void loadImage(String... imageNames) {
        images = new ArrayList<Image>();
        for (String imageName : imageNames) {
            ImageIcon ii = new ImageIcon(getClass().getResource(imageName));
            images.add(ii.getImage());
        }
    }

    protected void getImageDimensions() {
        width = image.getWidth(null);
        height = image.getHeight(null);
    }

    public void setImage(int i) {
        image = images.get(i);
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean vis) {
        visible = vis;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(Boolean alive) {
        isAlive = alive;
    }

    //rectangle for collision detection
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), width, height);
    }
}

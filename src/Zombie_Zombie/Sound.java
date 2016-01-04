package Zombie_Zombie;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
    public static final AudioClip MISSILE = Applet.newAudioClip(Sound.class.getResource("sounds/missile.wav"));
    public static final AudioClip MISSILE_FLY = Applet.newAudioClip(Sound.class.getResource("sounds/missile_fly.wav"));
    public static final AudioClip ZOMBIE = Applet.newAudioClip(Sound.class.getResource("sounds/zombie.wav"));
    public static final AudioClip EXPLOSION = Applet.newAudioClip(Sound.class.getResource("sounds/Explosion.wav"));
    public static final AudioClip LEVEL = Applet.newAudioClip(Sound.class.getResource("sounds/level.wav"));
    public static final AudioClip PICK_WEAPON = Applet.newAudioClip(Sound.class.getResource("sounds/pick_weapon.wav"));
    public static final AudioClip SCREAM = Applet.newAudioClip(Sound.class.getResource("sounds/scream.wav"));
    public static final AudioClip MAIN_THEME = Applet.newAudioClip(Sound.class.getResource("sounds/main.wav"));

}
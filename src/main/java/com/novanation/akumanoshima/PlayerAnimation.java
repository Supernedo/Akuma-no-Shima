package com.novanation.akumanoshima;

import java.awt.Graphics2D;
import java.awt.Image;

public class PlayerAnimation {

    private final Animation animation;
    private final Player player;

    private final int ANIMATION_LENGTH = 7;
    private final double SPEED_SCALE = 3.5;

    public PlayerAnimation(Player player)
    {
        this.player = player;
        animation = new Animation(true);

        // Dynamically load the animation frames
        for (int i = 1; i <= ANIMATION_LENGTH; i++)
        {
            Image frame = ImageManager.loadImage("/gfx/animations/player/walk/player_" + i + ".png");
            animation.addFrame(frame, (int) (1000 / (ANIMATION_LENGTH * SPEED_SCALE))); // Average is about 2 steps a second
        }
    }

    public void start() { animation.start(); }
    public void stop() { animation.stop(); }

    public boolean isStillActive() { return animation.isStillActive(); }
    
    public void draw(Graphics2D g2)
    {
        if (!animation.isStillActive()) return;

        animation.update();
        g2.drawImage(animation.getImage(), (int) player.getX(), (int) player.getY(), (int) player.getWidth(), player.getHeight(), null);
    }
}


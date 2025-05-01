package com.novanation.akumanoshima;

import java.awt.Graphics2D;
import java.awt.Image;

public class MenuAnimation {

    private final Animation animation;
    private final GameWindow window;

    private final int ANIMATION_LENGTH = 5;
    private final double SPEED_SCALE = 1;

    public MenuAnimation(GameWindow window)
    {
        animation = new Animation(true);
        this.window = window;

        // Dynamically load the animation frames

        for (int i = ANIMATION_LENGTH; i >= 1; i--)
        {
            Image frame = ImageManager.loadImage("/gfx/animations/menu_portal/island_image_" + i + ".png");
            animation.addFrame(frame, (int) (1000 / (ANIMATION_LENGTH * SPEED_SCALE)));
        }
    }

    public void start() { animation.start(); }
    public void stop() { animation.stop(); }

    public boolean isStillActive() { return animation.isStillActive(); }
    
    public void draw(Graphics2D g2)
    {
        if (!animation.isStillActive()) return;

        animation.update();
        g2.drawImage(animation.getImage(), 0, -20, (int) window.getWidth(), window.getHeight(), null);
    }
}


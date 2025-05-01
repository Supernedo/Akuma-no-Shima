package com.novanation.akumanoshima;


import java.awt.Graphics2D;
import java.util.ArrayList;

public class BackgroundManager {

    private ArrayList<String> bgImages = new ArrayList<>();

    // a move amount of 0 would make a background stationary
    private int moveAmount[] = {1, 2, 3, 4, 5};  // applied to moveSize // 2, 6, 24

    private Background[] backgrounds;
    private int numBackgrounds;

    private GamePanel panel;
    private final GameWindow window;

    public BackgroundManager(GamePanel panel, GameWindow window, WorldType world) {
        this.panel = panel;
        this.window = window;

        switch (world)
        {
            case FOREST -> setupForest();
            case VOLCANIC -> setupVolcanic();
            case BLIZZARD -> setupBlizzard();
            case END -> setupEnd();
        }

        numBackgrounds = bgImages.size();
        backgrounds = new Background[numBackgrounds];

        for (int i = 0; i < numBackgrounds; i++) {
            backgrounds[i] = new Background(panel, bgImages.get(i), moveAmount[i]);
        }
    }

    private void setupForest()
    {
        for (int i = 1; i <= 5; i++)
            bgImages.add("/gfx/images/forest/forest_night_" + i + ".png");
    }

    private void setupVolcanic()
    {
        for (int i = 1; i <= 5; i++)
        {
            if (i == 5)
                bgImages.add("/gfx/images/volcanic/volcanic_" + i + ".gif");
            else
                bgImages.add("/gfx/images/volcanic/volcanic_" + i + ".png");
        } 
    }

    private void setupBlizzard()
    {
        for (int i = 1; i <= 5; i++)
        {
            if (i == 5)
                bgImages.add("/gfx/images/blizzard/blizzard_" + i + ".gif");
            else
                bgImages.add("/gfx/images/blizzard/blizzard_" + i + ".png");
        } 
    }
    
    private void setupEnd()
    {
        bgImages.add("/gfx/images/end/end_scene.png");
    }

    public void move(int direction) {
        for (int i = 0; i < numBackgrounds; i++) {
            backgrounds[i].move(direction);
        }
    }

    // The draw method draws the backgrounds on the screen. The
    // backgrounds are drawn from the back to the front.
    public void draw(Graphics2D g2) {
        for (int i = 0; i < numBackgrounds; i++) {
            backgrounds[i].draw(g2, window);
        }
    }
}

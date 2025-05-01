package com.novanation.akumanoshima;

import java.awt.Image;

public class LevelManager {
    
    private static int totalEnemies;
    private static int remainingEnemies;
    private static int previousEnemies;

    private static boolean isClear = false;
    private static boolean isFinal = false;
    private static long startTime;

    private static boolean setup = false;

    // Images
    private static Image levelClearImage;
    private static Image gameWinImage;

    private static int imgClearX;
    private static int imgClearY;

    private static int imgWinX;
    private static int imgWinY;

    public static void setupToast(GamePanel panel)
    {
        levelClearImage = ImageManager.loadImage("/gfx/images/ui/level_clear_image.png");
        gameWinImage = ImageManager.loadImage("/gfx/images/ui/game_victory.png");

        imgClearX = (panel.getWidth() - levelClearImage.getWidth(null)) / 2;
        imgClearY = (panel.getHeight() - levelClearImage.getHeight(null)) / 2;

        imgWinX = (panel.getWidth() - gameWinImage.getWidth(null)) / 2;
        imgWinY = (panel.getHeight() - gameWinImage.getHeight(null)) / 2;

        totalEnemies = 0;
        remainingEnemies = 0;
        previousEnemies = 0;

        setup = true;
    }

    public static boolean isSetUp() { return setup; }

    public static void setTotalEnemies(int enemyCount) { totalEnemies = enemyCount; previousEnemies = totalEnemies; }

    public static void update() 
    {
        remainingEnemies = EnemyManager.getRemainingEnemies();
    
        if (totalEnemies == 0) return; // Don't process if no enemies have been set yet
    
        // Only update previous enemies count if we have enemies to track
        if (remainingEnemies == 0 && previousEnemies == 0)
            return;
    
        previousEnemies = remainingEnemies;
    
        if (!isClear && remainingEnemies <= 0 && totalEnemies > 0) {
            // Progress to next level
            if (EnemyManager.isFinal())
                isFinal = true;
            else
                isClear = true;
    
            startTime = System.currentTimeMillis();
        }
    }

    public static int getImgWinX() { return imgWinX; }
    public static int getImgWinY() { return imgWinY; }

    public static int getImgClearX() { return imgClearX; }
    public static int getImgClearY() { return imgClearY; }

    public static Image getClearImage() { return levelClearImage; }
    public static Image getWinImage() { return gameWinImage; }

    public static boolean showLevelClear() { return isClear; }
    public static boolean isFinalLevel() { return isFinal; }

    public static void setLevelClear(boolean levelClear) { isClear = levelClear; }
    public static void setFinal(boolean finalLevel) { isFinal = finalLevel; }

    public static long levelClearStartTime () { return startTime; }
    public static void setSetup(boolean set) { setup = set; }
}

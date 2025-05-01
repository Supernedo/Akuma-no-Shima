package com.novanation.akumanoshima;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class EnemyManager {
    
    private static HashMap<String, Entity> enemies = new HashMap<>();
    private static HashMap<String, Entity> enemiesAlive = new HashMap<>();
    private static int worldWidth;

    private static Player player;

    // Difficulty Presets

    // Enemy Count
    private static final int LOWER_EASY = 5;
    private static final int UPPER_EASY = 15;

    private static final int LOWER_NORMAL = 20;
    private static final int UPPER_NORMAL = 30;

    private static final int LOWER_HARD = 40;
    private static final int UPPER_HARD = 70;

    private static final double MINION_SCALE = 0.2f;

    private static final int EXPLOSION_FRAMES = 250;
    private static int frameCount = 0;

    // Enemy Health
    private static final int HEALTH_EASY = 1;
    private static final int HEALTH_NORMAL = 2;
    private static final int HEALTH_HARD = 4;

    // Boss Fight
    private static boolean isFinalLevel = false;
    private static Difficulty gameDifficulty;

    public static boolean isFinal() { return isFinalLevel; }
    public static void setPlayer(Player newPlayer) { player = newPlayer; }

    public static void summonMinions()
    {
        Random random = new Random();

        int enemyCount = 0;
        int hellHoundCount = 0;

        switch (gameDifficulty) // TODO: Properly utilize this
        {
            case EASY -> {
                enemyCount = random.nextInt((int) (LOWER_EASY * MINION_SCALE), (int) (UPPER_EASY * MINION_SCALE)) + 1;
                hellHoundCount = random.nextInt(2); // [0..1]
            }
            case NORMAL -> {
                enemyCount = random.nextInt((int) (LOWER_NORMAL * MINION_SCALE), (int) (UPPER_NORMAL * MINION_SCALE)) + 1;
                hellHoundCount = random.nextInt(2, 4); // [2..3]
            }
            case HARD -> {
                enemyCount = random.nextInt((int) (LOWER_HARD * MINION_SCALE), (int) (UPPER_HARD * MINION_SCALE)) + 1;
                hellHoundCount = random.nextInt(5, 8); // [5..7]
            }
        }

        for (int i = 0; i < enemyCount; i++)
        {
            int enemyWidth;
            int enemyHeight;
            
            GamePanel panel = player.getPanel();

            if (i < hellHoundCount)
            {
                enemyWidth = panel.getScaledHeight();
                enemyHeight = panel.getScaledWidth();

                EnemyHellhound hellhound = new EnemyHellhound(enemyWidth, enemyHeight, panel.getWidth() - enemyWidth - (i * 10), 50, "Enemy " + i, panel);
                enemies.put(hellhound.getID(), hellhound);
            }
            else
            {
                enemyWidth = panel.getScaledWidth() + 40;
                enemyHeight = panel.getScaledHeight();

                EnemyOni oni = new EnemyOni(enemyWidth, enemyHeight, panel.getWidth() - enemyWidth - (i * 64), 50, "Enemy " + i, panel);
                enemies.put(oni.getID(), oni);
            }
        }

        LevelManager.setTotalEnemies(enemyCount + 1); // To accommodate for Maou
    }

    public static void generateEnemies(Difficulty difficulty, boolean isFinal, GamePanel panel)
    {
        enemies.clear();
        enemiesAlive.clear();

        isFinalLevel = isFinal;
        Random random = new Random();

        gameDifficulty = difficulty;

        int enemyCount = 0;
        int hellHoundCount = 0;

        switch (difficulty) // TODO: Properly utilize this
        {
            case EASY -> {
                enemyCount = random.nextInt(LOWER_EASY, UPPER_EASY) + 1;
            }
            case NORMAL -> {
                enemyCount = random.nextInt(LOWER_NORMAL, UPPER_NORMAL) + 1;
                hellHoundCount = random.nextInt(0, 3); // [0..2]
            }
            case HARD -> {
                enemyCount = random.nextInt(LOWER_HARD, UPPER_HARD) + 1;
                hellHoundCount = random.nextInt(4, 7); // [4..6]
            }
        }

        if (isFinal)
        {
            double widthScale = (double) panel.getGameWindow().getConfig().getResolutionWidth() / panel.getGameWindow().getWidth();
            double heightScale = (double) panel.getGameWindow().getConfig().getResolutionHeight() / panel.getGameWindow().getHeight();

            int bossHeight = (int) (340 * heightScale);
            int bossWidth = (int) (280 * widthScale);

            EnemyMaou demonLord = new EnemyMaou(bossWidth, bossHeight, panel.getWidth() - bossWidth - 30, 50, "The Demon Lord", panel);
            enemies.put(demonLord.getEnemyID(), demonLord);

            LevelManager.setTotalEnemies(1);
            return; // Only the demon lord will be initially created
        }

        LevelManager.setTotalEnemies(enemyCount);

        List<String> enemyTypes = new ArrayList<>();

        // Fill the list with the correct number of each type
        for (int i = 0; i < hellHoundCount; i++) {
            enemyTypes.add("HELLHOUND");
        }
        for (int i = 0; i < enemyCount - hellHoundCount; i++) {
            enemyTypes.add("ONI");
        }

        // Shuffle the spawn order
        Collections.shuffle(enemyTypes);

        int segmentWidth = worldWidth / enemyCount;
        Set<Integer> usedXPositions = new HashSet<>();

        for (int i = 0; i < enemyCount; i++)
        {
            String type = enemyTypes.get(i);

            int enemyWidth = type.equals("HELLHOUND") ? panel.getScaledHeight() : panel.getScaledWidth() + 40;
            int enemyHeight = type.equals("HELLHOUND") ? panel.getScaledWidth() : panel.getScaledHeight();

            int segmentStart = i * segmentWidth;
            int segmentEnd = segmentStart + segmentWidth;

            segmentStart = Math.max(segmentStart, 1000); // To prevent the enemies from spawning in front the player
            segmentEnd = Math.min(segmentEnd, worldWidth);

            int x; // TODO: Presently, multiple enemies can spawn on top of each other
            int attempts = 0;
            do
            {
                x = (int) (Math.random() * (segmentEnd - segmentStart)) + segmentStart;
                x = Math.max(1000, Math.min(worldWidth, x));

                if (++attempts > UPPER_HARD) break; // Prevent looping infinitely
            } while (usedXPositions.contains(x));

            usedXPositions.add(x);
            int y = getYPosition();

            if (type.equals("HELLHOUND")) {
                EnemyHellhound hellHound = new EnemyHellhound(enemyWidth, enemyHeight, x, y, "Enemy " + i, panel);
                enemies.put("Enemy " + i, hellHound);
            } else {
                EnemyOni oni = new EnemyOni(enemyWidth, enemyHeight, x, y, "Enemy " + i, panel);
                enemies.put("Enemy " + i, oni);
            }
        }
    }

    public static int getHealthBase()
    {
        if (player == null) return 0;

        switch (player.getPanel().getGameWindow().getConfig().getDifficulty())
        {
            case EASY -> {return HEALTH_EASY;}
            case NORMAL -> {return HEALTH_NORMAL;}
            case HARD -> {return HEALTH_HARD;}
        }

        return HEALTH_NORMAL;
    }

    public static void moveProjectileWithWorld(int worldSpeed)
    {
        if (enemies == null) return;

        for (Map.Entry<String, Entity> entry : enemies.entrySet())
        {
            Entity entity = entry.getValue();

            if (entity instanceof EnemyOni enemyOni)
                enemyOni.moveWithWorld(worldSpeed);
        }
    }

    public static HashMap<String, Entity> getEnemies() { return enemies; }

    public static void killAllEntities()
    {
        for (Entity enemy : enemies.values())
           enemy.setID("DESTROYED");

        LevelManager.update();
    }

    public static void destroyEntity(Entity enemy)
    {
        if (enemy == null) return;

        enemy.setID("DESTROYED");
        enemiesAlive.remove(enemy.getID());

        if (enemy instanceof EnemyMaou enemyMaou)
        {
            enemyMaou.setDefeated(true);
            killAllEntities();
        }
        
        LevelManager.update();
    }

    private static int getYPosition()
    {
        // TODO: Get height using their x location with the tile and getting the y pos of the tile
        return 50;
    }

    public static void setWorldWidth(int worldLimit) { worldWidth = worldLimit; }

    public static HashMap<String, Entity> getAliveList()
    {
        for (Entity entity : enemies.values())
            if ("DESTROYED".equals(entity.getID()))
                enemiesAlive.put(entity.getID(), entity);

        return enemiesAlive;
    }

    public static int getRemainingEnemies() {
        if (enemies == null || enemies.values() == null) return 0;
        
        return (int) enemies.values().stream()
            .filter(entity -> !"DESTROYED".equals(entity.getID()))
            .count();
    }

    public static void move(int direction) {
        if (enemies == null) return;
        ArrayList<Entity> enemyList = new ArrayList<>(enemies.values());
        
        for (Entity enemy : enemyList) {
            enemy.move(direction);
            enemy.setWorldX(enemy.getWorldX() - direction);
        }
    }

    public static void draw(Graphics2D g2) {
        if (enemies == null) return;

        // Create a copy of the enemies collection to avoid concurrent modification
        ArrayList<Entity> enemyList = new ArrayList<>(enemies.values());

        for (Entity enemy : enemyList) {
            if (!"DESTROYED".equals(enemy.getID())) {
                enemy.draw(g2);
            } else if (enemy instanceof EnemyMaou enemyMaou) {
                frameCount++;
                if (frameCount < EXPLOSION_FRAMES) {
                    enemyMaou.draw(g2);
                }
            }
        }

        // Testing purposes
            // g2.setColor(Color.BLUE);
            // if (enemy.getCurrentChunk() != null)
            //     g2.fill(enemy.getCurrentChunk().getChunkBounds());

            // g2.setColor(new Color(255, 255, 255, 128));
            // if (enemy.getEntityBounds() != null)
            //     g2.fill(enemy.getEntityBounds());
    }
    

    public static Collection<Entity> getAllEnemies() {
    return enemies.values();
}

    public static Player getPlayer() {
        return player;
    }

    public static void clearEnemies() {
        enemies.clear();
    }
}

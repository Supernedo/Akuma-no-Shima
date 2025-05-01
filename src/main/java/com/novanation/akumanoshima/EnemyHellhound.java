package com.novanation.akumanoshima;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

public class EnemyHellhound implements Entity {

    private final Health health;

    // Movement variables
    private int dy;
    private int dx;
    private int worldX;

    private double vy;

    // Location Variables
    private double xPos;
    private double yPos;

    // Design Variables
    private final int width;
    private final int height;

    private Chunk currentChunk;
    private boolean onGround;

    private Rectangle2D.Double entityBounds;

    private String enemyID;
    private final Image hellhoundImage;

    private Player targetPlayer;
    private GamePanel panel;

    private static final double MOVE_SPEED = 2.0; // Faster than Oni
    private static final int CONTACT_DAMAGE = 15;
    private static final int DAMAGE_COOLDOWN = 1000; // 1 second cooldown
    private long lastDamageTime = 0;

    public EnemyHellhound(int width, int height, int xPos, int yPos, String enemyID, GamePanel panel)
    {
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
        this.enemyID = enemyID;

        this.worldX = xPos;
        this.panel = panel;

        health = new Health(false);

        health.setMaxHealth(EnemyManager.getHealthBase() - 1);
        health.setCurrentHealth(health.getMaxHealth());

        hellhoundImage = ImageManager.loadImage("/gfx/characters/char_hellhound.png");
    }

    @Override
    public int getWorldX() { return worldX; }
    @Override
    public void setWorldX(int x) { this.worldX = x; }

    @Override
    public void setID(String id) { this.enemyID = id; }
    @Override
    public String getID() { return enemyID; }

    @Override
    public void update() {
        int tileLength = WorldGeneration.getTileLength();
        currentChunk = WorldGeneration.getChunk(((worldX) / tileLength) * tileLength);

        if ("DESTROYED".equals(enemyID))
            entityBounds = null;
        else
            entityBounds = new Rectangle2D.Double(xPos, yPos - 2, width, height);

        if (targetPlayer == null)
            targetPlayer = panel.getPlayerEntity();

        Chunk newChunk = WorldGeneration.getChunk((worldX + tileLength) / tileLength * tileLength);
        determineChunkTile(newChunk);
    }

    private void determineChunkTile(Chunk newChunk)
    {
        if (newChunk != null && newChunk.getTileType() == TileType.TERTIARY)
        {
            // TODO: Update enemy behaviour
            switch (newChunk.getWorldType())
            {
                case FOREST -> {
                }
                case VOLCANIC -> {
                }
                case BLIZZARD -> {
                }
                case END -> {}
            }
        }
    }

    @Override
    public int getHeight() { return height; }
    @Override
    public double getX() { return xPos; }
    @Override
    public double getY() { return yPos; }
    @Override
    public Health getHealth() { return health; }

    @Override
    public void setWorldPos(int xPos) { 
        this.worldX = xPos;
        // Update screen position relative to world offset
        this.xPos = this.worldX + panel.getWorldOffsetX();
    }

    @Override
    public void draw(Graphics2D g2) { g2.drawImage(hellhoundImage, (int) xPos, (int) yPos, width, height, null); drawHealthBar(g2); }

    @Override
    public Chunk getNextChunk() { return null; } // TODO: add checks
    @Override
    public Chunk getPreviousChunk() { return null; } // TODO: add checks

    public void drawHealthBar(Graphics2D g2) {
        int healthBarWidth = 50;
        int healthBarHeight = 5;
        double healthBarX = xPos + (width - healthBarWidth) / 2;
        double healthBarY = yPos - 10;
        
        // Draw background (red)
        g2.setColor(Color.RED);
        g2.fillRect((int) healthBarX, (int) healthBarY, healthBarWidth, healthBarHeight);
        
        // Draw remaining health (green)
        g2.setColor(Color.GREEN);
        int currentHealthWidth = (int)((health.getCurrentHealth() / (float) health.getMaxHealth()) * healthBarWidth);
        g2.fillRect((int) healthBarX, (int) healthBarY, currentHealthWidth, healthBarHeight);

        g2.setColor(Color.WHITE);
        String healthText = health.getCurrentHealth() + "/" + health.getMaxHealth();
        g2.drawString(healthText, (int)healthBarX, (int)healthBarY - 2);
    }

    // Movement
    @Override
    public void move(int direction) { 
        xPos += direction; 
        worldX += direction;
    }
    @Override
    public void moveY(double dx) { yPos += dx; }
    @Override
    public double getDY() { return dy; }
    @Override
    public void setY(double newY) { yPos = newY; }
    @Override
    public double getVelocityY() { return vy; }
    @Override
    public void setVelocityY(double vy) { this.vy = vy; }

    @Override
    public boolean isGrounded() { return onGround; }
    @Override
    public void setGrounded(boolean grounded) { onGround = grounded; }

    // Shape
    @Override
    public Rectangle2D.Double getEntityBounds() { return entityBounds; }
    @Override
    public Chunk getCurrentChunk() { return currentChunk; }

    // Actions
    @Override
    public void jump() { }
    @Override
    public void performAction()
    {
        if (targetPlayer == null) return;
        
        // Calculate distance to player using world coordinates
        double dx = targetPlayer.getWorldX() - worldX;
        double dy = targetPlayer.getY() - yPos;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Always move towards player
        double dirX = dx / distance;
        
        // Update positions
        worldX += dirX * MOVE_SPEED;
        xPos = worldX + panel.getWorldOffsetX();

        // Check for collision with player
        if (entityBounds != null && targetPlayer.getEntityBounds() != null) {
            if (entityBounds.intersects(targetPlayer.getEntityBounds())) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastDamageTime >= DAMAGE_COOLDOWN) {
                    // targetPlayer.getHealth().dealDamage(CONTACT_DAMAGE, true, targetPlayer);
                    lastDamageTime = currentTime;
                }
            }
        }
     }
}

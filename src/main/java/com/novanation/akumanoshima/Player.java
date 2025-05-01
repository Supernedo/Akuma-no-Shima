package com.novanation.akumanoshima;


import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Player implements Entity {
    
    // Location
    private double x;
    private double y;
    private int worldX;

    private int dx = 0;
    private int dy = 0;

    private String id;

    //private double health;

    // Physics
    private double timeElapsed = 0;
    private double startY;
    private boolean isJumping;
    private boolean canJump;
    private boolean onGround;

    private double vy;

    private Health health;
   
    // Shape and Collisions
    private int width;
    private int height;

    private Chunk currentChunk;
    private Chunk previousChunk;
    private Chunk nextChunk;
    
    private Rectangle2D.Double chunk;
    Rectangle2D.Double playerBounds;
    private final Image playerImage;

    private boolean inLiquid = false;
    private boolean onIce = false;

    // Game Panel
    private final GamePanel panel;

    //Perks
    private List<Perk> activePerks = new ArrayList<>();
    private double moveSpeedMultiplier = 1.0;
    private Weapon currentWeapon;

    public PlayerAnimation playerAnimation;

    public Player(GamePanel panel, int x, int y, int width, int height)
    {
        this.width = width;
        this.height = height;

        this.x = x;
        this.y = y;

        this.panel = panel;

        isJumping = false;
        playerImage = ImageManager.loadImage("/gfx/characters/char_noroi.png");
        health = new Health(true);
        playerBounds = new Rectangle2D.Double(x, y, width, height);

        this.playerAnimation = new PlayerAnimation(this);
    }

    @Override
    public void setID(String id) { this.id = id; }
    @Override
    public String getID() { return id; }

    // Directional mutators
    public void setDX(int newDX) { dx = newDX; }
    public void setDY(int newDY) { dy = newDY; }
    // Directional accessors
    public int getDX() { return dx; }
    @Override
    public double getDY() { return dy; }

    // Locational Accessors
    @Override
    public double getX() { return x; }
    @Override
    public double getY() { return y; }
    @Override
    public int getWorldX() { return worldX; }
   
    // Locational Mutators
    @Override
    public void setWorldPos(int xPos) { worldX = xPos; }

    @Override
    public void setWorldX(int x) {}

    // Shape Mutators
    public void setWidth(int newWidth) { width = newWidth; }
    public void setHeight(int newHeight) { height = newHeight; }
    // Shape Accessors
    public int getWidth() { return width; }
    @Override
    public int getHeight() { return height; }

    // Physics Accessors
    public boolean isJumping() { return !isGrounded(); }
    public boolean canJump() { return canJump; }

    @Override
    public Rectangle2D.Double getEntityBounds() { return playerBounds; }
    @Override
    public Chunk getNextChunk() { return nextChunk; }

    @Override
    public void moveY(double dx) { y += dx; }

    public void setX(double newX) { x = newX; }
    @Override
    public void setY(double newY) { y = newY; }

    @Override
    public boolean isGrounded() { return onGround; }
    @Override
    public void setGrounded(boolean grounded) { onGround = grounded; }

    @Override
    public double getVelocityY() { return vy; }
    @Override
    public void setVelocityY(double vy) { this.vy = vy; }

    @Override
    public Health getHealth() { return health; }

    // Panel Dimension Accessors
    public Dimension getPanelDimensions() { return panel.getSize(); }

    public GamePanel getPanel() { return panel; }

    //Perk Accessors
    public Weapon getWeapon() { return currentWeapon; }
    public void setCurrentWeapon(Weapon weapon) { currentWeapon = weapon; }

    public void applyPerk(Perk perk) {
        if (!activePerks.contains(perk)) {
            activePerks.add(perk);
            perk.applyEffect(this);
        }
    }

    public void removePerk(Perk perk) {
        if (activePerks.contains(perk)) {
            activePerks.remove(perk);
            perk.removeEffect(this);
        }
    }

    public void setMoveSpeedMultiplier(double multiplier) {
        this.moveSpeedMultiplier = multiplier;
    }

    public double getMoveSpeedMultiplier() {
        return moveSpeedMultiplier;
    }

    @Override
    public void update()
    {
        int tileLength = WorldGeneration.getTileLength();
        currentChunk = WorldGeneration.getChunk((((int) worldX) / tileLength) * tileLength);

        if (width < 0)
            playerBounds.setRect(x - Math.abs(width) + 5, y - 2, Math.abs(width), height);
        else
            playerBounds.setRect(x, y - 2, Math.abs(width), height);

        if (currentChunk != null)
            determineChunkTile(currentChunk);

        // Chunk newChunk = WorldGeneration.getChunk((((int) worldX + (tileLength * (width / Math.abs(width)))) / tileLength) * tileLength);
        // determineChunkTile(newChunk);
    }

    public PlayerAnimation getPlayerAnimation() { return playerAnimation; }

    @Override
    public void move(int direction)
    {
        dx = (int)(direction * moveSpeedMultiplier);

        if (x + dx > 0 && x + dx < panel.getWidth() - Math.abs(width))
            x += dx;
    }

    @Override
    public Chunk getCurrentChunk() { return currentChunk; }
    @Override
    public Chunk getPreviousChunk() { return previousChunk; }

    public void stopMoving() { dx = 0; }

    public boolean isColliding(int dx)
    {
        int tileLength = WorldGeneration.getTileLength();

        if (dx > 0)
        {
            nextChunk = WorldGeneration.getChunk((((int) worldX - (tileLength * (Math.abs(width) / width))) / tileLength) * tileLength);
            if (nextChunk != null && nextChunk.getTileType() != TileType.TERTIARY) // Next chunk is not air and exists
                return getCollision(nextChunk);
        }
        else if (dx < 0)
        {
            previousChunk = WorldGeneration.getChunk((((int) worldX + (tileLength * (Math.abs(width) / width))) / tileLength) * tileLength);
            if (previousChunk != null && previousChunk.getTileType() != TileType.TERTIARY) // Previous chunk is not air and exists
                return getCollision(previousChunk);
        }

        return false;
    }

    private void determineChunkTile(Chunk newChunk)
    {
        if (newChunk != null && newChunk.getTileType() == TileType.TERTIARY)
        {
            Rectangle2D.Double chunkBounds = getCurrentChunk() != null
            ? getCurrentChunk().getChunkBounds()
            : null;

            if (chunkBounds != null) // Actually over the tertiary tile
            {
                switch (newChunk.getWorldType())
                {
                    case FOREST -> {
                        inLiquid = true;
                    }
                    case VOLCANIC -> {
                        inLiquid = true;
                        health.dealDamage(1, true, this);
                    }
                    case BLIZZARD -> {
                        onIce = true;
                    }
                    case END -> {}
                }
            }
            
        }
        else
        {
            inLiquid = false;
            onIce = false;
        }
    }

    public boolean getInLiquid() { return inLiquid; }
    public boolean getOnIce() { return onIce; }

    private boolean getCollision(Chunk newChunk)
    {
        // Check collision
        chunk = newChunk.getChunkBounds();
        return playerBounds.intersects(chunk) || chunk.contains(playerBounds);
    }

    public int getCollisionDirection()
    {
        if (playerBounds.getX() < chunk.getX()) // Player is on the left
            return 0;
        return 1; // Otherwise, player is on the right
    }

    @Override
    public void jump()
    {
        if (onGround)
        {
            setVelocityY(-16);
            onGround = false;
        }
    }

    @Override
    public void performAction() { }

    @Override
    public void draw(Graphics2D g2)
    {
        if (playerAnimation != null && !playerAnimation.isStillActive())
            g2.drawImage(playerImage, (int) x, (int) y, width, height, null);
    }
}

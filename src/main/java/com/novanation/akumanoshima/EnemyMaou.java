package com.novanation.akumanoshima;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class EnemyMaou implements Entity {

    private final Health health;

    // Movement variables
    private int dy;
    private int dx;
    private int worldX;

    private String id;
    private boolean isDead;

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

    private final Timer timer = new Timer();
    private boolean inAction = false;
    private boolean isLanded = false;

    // Design variables
    private String enemyID;
    private final Image maouImage;

    private final Image explosionGif;
    private Image landingGif;

    private final int LANDING_FRAMES = 250;
    private int frameCount = 0;
    private int randAction = 0;

    private boolean isBackingAway = false;
    private boolean isCharging = false;
    private boolean isShooting = false;
    private boolean isSlamming = false;
    private boolean slamReady = false;

    private int backingAwayTimer = 0;
    private final int MAX_BACKING_AWAY_TIME = 120; // Two seconds

    private int chargeTimer = 0;
    private final int MAX_CHARGE_TIME = 180; // Three second charge

    private int shootTimer = 0;
    private final int MAX_SHOOT_TIME = 1000; // Barrage

    private int slamTimer = 0;
    private final int MAX_SLAM_TIME = 60; // One second slam bam

    //Attacking variables
    private static final int ATTACK_RANGE = 1000; // Horizontal range to start attacking
    private static final int ATTACK_INTERVAL = 500; // Milliseconds between shots
    private long lastAttackTime = 0;
    private List<EnemyProjectile> projectiles = new ArrayList<>();

    // Death Positioning
    private int finalX = 0;
    private int finalY = 0;

    // Animation
    private final EnemyMaouAnimation maouAnimation;

    private boolean wasReset = false;

    private GamePanel panel;
    private Player player;

    public EnemyMaou(int width, int height, int xPos, int yPos, String enemyID, GamePanel panel)
    {
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
        this.enemyID = enemyID;

        this.worldX = xPos;
        this.panel = panel;

        this.projectiles = new ArrayList<>();
        maouAnimation = new EnemyMaouAnimation(this);

        isDead = false;

        health = new Health(false);
        health.setMaxHealth(50);
        health.setCurrentHealth(health.getMaxHealth());

        maouImage = ImageManager.loadImage("/gfx/characters/char_maou.png");
        explosionGif = (ImageManager.loadGif("/gfx/animations/maou/gifs/explosion.gif")).getImage();
        landingGif = (ImageManager.loadGif("/gfx/animations/maou/gifs/landing.gif")).getImage();
    }

    @Override
    public int getWorldX() { return worldX; }
    @Override
    public void setWorldX(int x) { this.worldX = x; }

    @Override
    public void setID(String id) { this.id = id; }
    @Override
    public String getID() { return id; }

    public int getWidth() { return width; }

    @Override
    public void update()
    {
        if (isDead) return;

        int tileLength = WorldGeneration.getTileLength();
        currentChunk = WorldGeneration.getChunk(((worldX) / tileLength) * tileLength);

        if (player == null)
            player = panel.getPlayerEntity();

        updateBackingAway();
        updateCharging();
        updateShooting();
        updateSlam();

        if ("DESTROYED".equals(enemyID))
            entityBounds = null;
        else
            entityBounds = new Rectangle2D.Double(xPos, yPos - 2, width, height);

        Chunk newChunk = WorldGeneration.getChunk(((worldX + tileLength) / tileLength) * tileLength);
        determineChunkTile(newChunk);

        if (randAction != 0)
            switch (randAction)
            {
                case 1 -> actionSummon();
                case 2 -> actionCharge();
                case 3 -> actionShoot();
                case 4 -> actionSlam();
            }

        if (!inAction)
            performAction();
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

    public String getEnemyID() { return enemyID; }
    public void setEnemyID(String enemyID) { this.enemyID = enemyID; }

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
    public void draw(Graphics2D g2)
    {
        if (isDead)
            g2.drawImage(explosionGif, (int) finalX - width, (int) finalY - width, width * 2, height * 2, null);
        else
        {
            if (isLanded)
            {
                if (frameCount == 0)
                    landingGif = ImageManager.loadGif("/gfx/animations/maou/gifs/landing.gif").getImage();
                    
                if (frameCount < LANDING_FRAMES)
                {
                    frameCount++;
                    g2.drawImage(landingGif, (int) xPos + (Math.abs(width) / 2) - 300, (int) yPos + 120, Math.abs(width) + 300, height, null);
                }
            }

            for (int i = 0; i < projectiles.size(); i++)
            {
                EnemyProjectile projectile = projectiles.get(i);
                projectile.draw(g2);
            }

            if (isCharging || isBackingAway)
                maouAnimation.draw(g2);

            if (!isBackingAway && !isCharging)
                g2.drawImage(maouImage, (int) xPos, (int) yPos, width, height, null);

            drawHealthBar(g2);
        }
    }

    public void resetFrames() { frameCount = 0; }

    public void drawHealthBar(Graphics2D g2) {
        int healthBarWidth = 150;
        int healthBarHeight = 10;
        double healthBarX = xPos - (healthBarWidth / 2) + 30;
        double healthBarY = yPos - 10;
        
        // Draw background (red)
        g2.setColor(Color.RED);
        g2.fillRect((int) healthBarX, (int) healthBarY, healthBarWidth, healthBarHeight);
        
        // Draw remaining health (green)
        g2.setColor(Color.GREEN);
        int currentHealthWidth = (int)((health.getCurrentHealth() / (float) health.getMaxHealth()) * healthBarWidth);
        g2.fillRect((int) healthBarX, (int) healthBarY, currentHealthWidth, healthBarHeight);

        g2.setColor(Color.WHITE);
        String healthText = (health.getCurrentHealth()) + "/" + (health.getMaxHealth());
        g2.drawString(healthText, (int)healthBarX, (int)healthBarY - 2);
    }

    // This isn't needed for this particular enemy
    @Override
    public Chunk getNextChunk() { return null; }
    @Override
    public Chunk getPreviousChunk() { return null; }

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

    public boolean isLanded() { return isLanded; }
    public void setLanded(boolean isLanded) { this.isLanded = isLanded; }

    public boolean getIsCharging() { return isCharging; }
    public boolean getIsBackingAway() { return isBackingAway; }

    // Actions
    @Override
    public void jump() 
    { 
        if (onGround)
        {
            setVelocityY(-16);
            onGround = false;
            isLanded = false;
        }
     }
    @Override
    public void performAction()
    {
        Random random = new Random();
        inAction = true;

        maouAnimation.stopAttack();

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                randAction = random.nextInt(1, 5); // [1..4]

                switch (randAction)
                {
                    case 1 -> actionSummon();
                    case 2 -> actionCharge();
                    case 3 -> actionShoot();
                    case 4 -> actionSlam();
                }

                inAction = false;
                randAction = 0;
            }
        }, 2_500, 2_500);
    }

    private void actionSummon()
    {
        if (EnemyManager.getRemainingEnemies() != 1) return; // If it's just the boss

        if (player != null)
        {
            isBackingAway = true;
            backingAwayTimer = MAX_BACKING_AWAY_TIME;
        }

        EnemyManager.summonMinions();
    }

    private void updateBackingAway()
    {
        if (isBackingAway)
        {
            if (player != null)
            {
                dx = (int) (xPos - player.getX());
                dy = (int) (yPos - player.getY());
                double distance = Math.sqrt(dx * dx + dy * dy);
    
                if (distance != 0)
                {
                    double moveSpeed = 2.0;

                    if (xPos + dx > 0 && xPos + dx < panel.getWidth() - Math.abs(width))
                        xPos += (dx / distance) * moveSpeed;

                    yPos += (dy / distance) * moveSpeed;
                }
            }
    
            backingAwayTimer--;
    
            if (backingAwayTimer <= 0)
                isBackingAway = false;
        }
    }

    private void actionCharge()
    {
        if (player != null)
        {
            isCharging = true;
            chargeTimer = MAX_CHARGE_TIME;

            maouAnimation.startWalk();
        }
    }

    private void updateCharging()
    {
        if (isCharging)
        {
            if (player != null)
            {
                dx = (int) (xPos - player.getX());
                dy = (int) (yPos - player.getY());
                double distance = Math.sqrt(dx * dx + dy * dy);
    
                if (distance > (int) Math.abs((player.getWidth() / 2)) + 50)
                {
                    double moveSpeed = 2.0;

                    if (xPos - dx > 0 && xPos - dx < panel.getWidth() - Math.abs(width))
                        xPos -= (dx / distance) * moveSpeed;

                    yPos += (dy / distance) * moveSpeed;
                }
            }
    
            chargeTimer--;
    
            if (chargeTimer <= 0)
            {
                isCharging = false;
                maouAnimation.stopWalk();
                if (player == null) return;

                maouAnimation.startAttack();

                if (!wasReset)
                {
                    chargeTimer = 80;
                    isCharging = true;
                    wasReset = true;
                }
                else
                {
                    wasReset = false;
                    maouAnimation.stopAttack();
                }

                int newWidth = 0;
                if (width > 0)
                    newWidth = (int) (getEntityBounds().width);
                if (width < 0)
                    newWidth = (int) Math.abs((getEntityBounds().width));

                Rectangle2D.Double hitBox = new Rectangle2D.Double(getEntityBounds().x, getEntityBounds().y, newWidth, getEntityBounds().height);

                if (player.getEntityBounds().intersects(hitBox))
                {
                    player.getHealth().dealDamage(1, true, player);
                    player.jump();
                }
            }
        }
    }

    private void actionShoot()
    {
        if (player != null)
        {
            isShooting = true;
            shootTimer = MAX_SHOOT_TIME;
        }
    }

    private void updateShooting()
    {
        if (isShooting)
        {
            if (player != null)
            {
                long currentTime = System.currentTimeMillis();
        
                // Check attack timing
                if (currentTime - lastAttackTime >= ATTACK_INTERVAL)
                {
                    // Use world position for distance check
                    if (player != null)
                    {
                        dx = (int) (xPos - player.getX());
                        dy = (int) (yPos - player.getY());
                        double distance = Math.sqrt(dx * dx + dy * dy);

                        if (distance <= ATTACK_RANGE)
                        {
                            shootFireball();
                            lastAttackTime = currentTime;
                        }
                    }
                }

                // Update active projectiles
                updateProjectiles();
            }

            shootTimer--;
    
            if (shootTimer <= 0)
                isShooting = false;
        }
    }

    private void shootFireball()
    {
        EnemyProjectile projectile = new EnemyProjectile();
        
        // Calculate trajectory points
        double startX = xPos + width/2;
        double startY = yPos + height/2;

        double playerScreenX = player.getX() + player.getWidth()/2;
        double playerScreenY = player.getY() + player.getHeight()/2;
        
        double targetX = playerScreenX;
        double targetY = playerScreenY;

        // Calculate control point for arc
        double controlX = (startX + targetX) / 2;
        double controlY = Math.min(startY, targetY) - 400;
        
        projectile.setPanel(panel);

        projectile.spawn(startX, startY, 0);
        projectile.setTargetPoints(startX, startY, controlX, controlY, targetX, targetY);
        projectiles.add(projectile);
    }

    private void updateProjectiles()
    {
        projectiles.removeIf(p -> !p.isActive());

        for (EnemyProjectile projectile : projectiles) {
            projectile.move();
            //collision
            if (projectile.isActive() && player != null)
                projectile.checkCollision(player);
        }
    }

    public void actionSlam()
    {
        if (player != null)
        {
            isSlamming = true;
            slamTimer = MAX_SLAM_TIME;
        }

        jump();
    }

    private void updateSlam()
    {
        if (isSlamming)
        {
            if (player != null)
            {
                int newWidth = 0;
                if (width > 0)
                    newWidth = (int) (getEntityBounds().width) + 300;
                if (width < 0)
                    newWidth = (int) Math.abs((getEntityBounds().width)) + 300;

                Rectangle2D.Double hitBox = new Rectangle2D.Double(getEntityBounds().x - 300, getEntityBounds().y, newWidth, getEntityBounds().height);

                if (player.getEntityBounds().intersects(hitBox) && slamReady)
                {
                    player.getHealth().dealDamage(3, true, player);
                    player.jump();
                }
            }
    
            slamTimer--;
    
            if (slamTimer <= 0)
                isSlamming = false;
        }
    }

    public void setSlamReady(boolean ready) { slamReady = ready; }

    public void setDefeated(boolean isDead)
    {
        this.isDead = isDead;
        player.getPanel().getGameWindow().playAudioClip("explosion", ClipType.SFX, false);

        finalX = (int) xPos;
        finalY = (int) yPos;
    }
}

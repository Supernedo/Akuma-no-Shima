package com.novanation.akumanoshima;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Physics {
    
    private static final double GRAVITY = 1;
    private static final double INITIAL_VERTICAL_VELOCITY = -12.0;
    private static final double INITIAL_HORIZONTAL_VELOCITY = 5.0;

    private static final int JUMP_INTERVAL = 7; // In milliseconds (ms) // Influences jump smoothness
    private static final int MAX_STEP_COUNT = 230; // Maximum steps for calculating the jump // Influences jump height
    private static final double COUNT_SCALE = 0.3f;
    private static final double SPEED_SCALE = 0.2f;

    public static GamePanel panel;

    private static double gravity = 0.5f; // gravity = 9.8 m/s
    private static double terminalVelocity = 10;

    public boolean isJumping = false;
    private double currTime = 0;
    private double initY = 0;
    private double initX = 0;

    // Accessors

    public static int getJumpInterval() { return JUMP_INTERVAL; }
    public static int getMaxStep() { return MAX_STEP_COUNT; }
    public static double getCountScale() { return COUNT_SCALE; }
    public static double getSpeedScale() { return SPEED_SCALE; }
    public static double getGravity() { return gravity; }

    // Functions
    
    private static int count;

    public static void setPanel(GamePanel gamePanel) { panel = gamePanel; }

    public static void applyGravity(Entity entity, double x, double y)
    {
        if (entity == null) return;

        // Apply gravity
        if (!entity.isGrounded())
        {
            double newVelocityY = Math.min(entity.getVelocityY() + gravity, terminalVelocity);
            entity.setVelocityY(newVelocityY);
        }

        entity.moveY(entity.getVelocityY());

        // Check if player is landing
        Rectangle2D.Double chunkBounds = entity.getCurrentChunk() != null
            ? entity.getCurrentChunk().getChunkBounds()
            : null;

            if (chunkBounds != null) {
                int sinkDepth = 0;
        
                if (entity.getNextChunk() != null && entity.getNextChunk().getTileType() == TileType.TERTIARY) {
                    if (entity.getNextChunk().getWorldType() == WorldType.FOREST || 
                        entity.getNextChunk().getWorldType() == WorldType.VOLCANIC)
                        sinkDepth = WorldGeneration.getTileLength();
                }
        
                double entityBottom = entity.getY() + entity.getHeight();
                double groundY = chunkBounds.getY() + sinkDepth;
        
                // Check if EnemyMaou is about to land (within 5 pixels of ground)
                if (entity instanceof EnemyMaou enemyMaou) {
                    if (entityBottom + 2 >= groundY && entity.getVelocityY() < 0) {
                        enemyMaou.setSlamReady(true);
                    }
                }
        
                // Only snap the entity to the ground if they are falling on the ground (not air gap)
                if (entityBottom >= groundY && entity.getVelocityY() >= 0) {
                    entity.setY(groundY - entity.getHeight());
                    entity.setGrounded(true);
                    entity.setVelocityY(0);
        
                    // Check if entity is EnemyMaou and just landed
                    if (entity instanceof EnemyMaou enemyMaou) {
                        if (!enemyMaou.isLanded()) {
                            enemyMaou.setLanded(true);
                            enemyMaou.resetFrames();
                        }
                    }
                }
                else
                    entity.setGrounded(false);
            }

        if (entity.getY() > panel.getHeight())
        {
            if (entity instanceof Player)
            {
                if (!entity.getHealth().isDead())
                    entity.getHealth().killPlayer();
            }
            else
                entity.getHealth().destroyEntity(entity);
        }
    }

    public static Point2D.Double calculateBezierPoint(double startX, double startY, 
        double controlX, double controlY, 
        double endX, double endY, 
        double t) {
        double u = 1 - t;
        double tSq = t * t;
        double uSq = u * u;

        double x = uSq * startX + 2 * u * t * controlX + tSq * endX;
        double y = uSq * startY + 2 * u * t * controlY + tSq * endY;

        return new Point2D.Double(x, y);
    }

    public static Point2D.Double calculateBezierDerivative(double startX, double startY, 
        double controlX, double controlY, 
        double endX, double endY, 
        double t) {
        
        double u = 1 - t;

        double dx = 2 * u * (controlX - startX) + 2 * t * (endX - controlX);
        double dy = 2 * u * (controlY - startY) + 2 * t * (endY - controlY);

        return new Point2D.Double(dx, dy);
    }


    // kinematics 
    // s = ut + 1/2at^2
    public static double calculateVertComponent(double initialVelocity, double timeElapsed) 
    {
        return(initialVelocity * timeElapsed - 0.5 * GRAVITY * timeElapsed * timeElapsed);
    }

    public static double calculateHorizComponent(double direction, double timeElapsed)
    {
        return INITIAL_HORIZONTAL_VELOCITY * direction + timeElapsed;
    }

    public void startJump(double startY, double startX)
    {
        isJumping = true;
        currTime = 0;
        initY = startY;
        initX = startX;
    }

    public void resetJumpPos()
    {
        isJumping = false;
        currTime = 0;
    }
}

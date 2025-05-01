package com.novanation.akumanoshima;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

public class EnemyProjectile implements Projectile {

    private double x, y;
    private double t = 0; // Time parameter for bezier curve (0 to 1)
    private double speed = 0.003; // Speed of projectile movement
    private int size = 30; // Size of projectile
    private boolean active = true;
    private static final int FIREBALL_DAMAGE = 1; // Damage dealt by the fireball

    private double directionX;
    private double directionY;
    private double targetX, targetY;
   
    private double worldX, worldY;

    // Bezier curve points
    private double startX, startY;
    private double controlX, controlY;
    private double endX, endY;

    private int worldSpeed;

    private GamePanel panel;

    public void setPanel(GamePanel panel) { this.panel = panel; }

    public void setActive(boolean active) { this.active = active; }

    @Override
    public void move() {
        if (t <= 1.0) {
            Point2D.Double pos = Physics.calculateBezierPoint(
                startX, startY,
                controlX, controlY,
                endX, endY,
                t
            );
            // Update both world and screen positions
            worldX = pos.x;
            x = worldX + panel.getWorldOffsetX(); // Adjust for world movement
            worldY = pos.y;
            y = worldY;
            t += speed;
        } else {
            active = false;
        }
    }

    public void moveWithWorld(int worldSpeed) {
        this.worldSpeed = worldSpeed;
        x = worldX + panel.getWorldOffsetX();
    }

    @Override
    public void hit() {
        active = false;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!active) return;

        int worldOffsetX = panel.getWorldOffsetX();
    
        ImageIcon fireballGif = ImageManager.loadGif("/gfx/animations/oni/gif/fireball.gif");
        int imgWidth = fireballGif.getIconWidth();
        int imgHeight = fireballGif.getIconHeight();
    
        // Get direction of movement
        Point2D.Double velocity = Physics.calculateBezierDerivative(
            startX, startY,
            controlX, controlY,
            endX, endY,
            t
        );
            
        double angleRadians = Math.atan2(velocity.y, velocity.x);
    
        AffineTransform oldTransform = g2.getTransform();
    
        // Move to the center of the projectile
        g2.translate(worldX - worldOffsetX, worldY);
    
        // Rotate to match direction
        g2.rotate(angleRadians);
    
        // Draw image centered
        g2.drawImage(fireballGif.getImage(), -imgWidth/2, -imgHeight/2, null);
    
        g2.setTransform(oldTransform);
    }

    @Override
    public void spawn(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.t = 0;
        this.active = true;
    }

    public void setTargetPoints(double startX, double startY, double controlX, double controlY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.controlX = controlX;
        this.controlY = controlY;
        this.endX = endX;
        this.endY = endY;
    }

    public boolean isActive() {
        return active;
    }

    public boolean checkCollision(Player player) {
        if (!active) return false;
        
        Rectangle2D.Double projectileBounds = new Rectangle2D.Double(
            x - size/2, // Use screen position x instead of worldX
            y - size/2, 
            size, 
            size
        );
        
        Rectangle2D.Double playerBounds = player.getEntityBounds();
        if (projectileBounds.intersects(playerBounds)) {
            hit();
            player.getHealth().dealDamage(FIREBALL_DAMAGE, true, player);
            return true;
        }
        return false;
    }

}

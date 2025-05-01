package com.novanation.akumanoshima;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class Bullet implements Projectile{

    private double x;
    private double y;
    private final double height = 6;
    private final double width = 15;
    private final double speed = 10;
    private double angle;
    private static final int BULLET_DAMAGE = 2;
    private boolean active = true;

    @Override
    public void move() {
       x += speed * Math.cos(angle);
       y += speed * Math.sin(angle); 
    }

    @Override
    public void hit() {
        active = false;
    }

    public boolean checkCollision(Entity enemy) {
        if (!active) return false;
        
        Rectangle2D.Double bulletBounds = new Rectangle2D.Double(
            x - width/2, y - height/2, width, height
        );
        
        Rectangle2D.Double enemyBounds = enemy.getEntityBounds();
        if (enemyBounds != null && bulletBounds.intersects(enemyBounds)) {
            hit();
            enemy.getHealth().dealDamage(BULLET_DAMAGE, false, enemy);
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!active) return;

        // Save the original transform
        AffineTransform oldTransform = g2.getTransform();
    
        // Create a new transform for the bullet
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(angle);
    
        // Apply the transform
        g2.setTransform(transform);
    
        // Draw the main bullet body (rectangle)
        g2.setColor(new Color(255, 215, 0)); // Golden color
        g2.fillRect((int)(-width/2), (int)(-height/2), (int)width, (int)height);
    
        // Draw the bullet tip (triangle)
        Path2D.Double tip = new Path2D.Double();
        tip.moveTo(width/2, -height/2);
        tip.lineTo(width/2 + height/2, 0);
        tip.lineTo(width/2, height/2);
        tip.closePath();
        g2.fill(tip);
    
        // Add a subtle glow effect
        g2.setColor(new Color(255, 215, 0, 64));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRect((int)(-width/2), (int)(-height/2), (int)width, (int)height);
        g2.draw(tip);
    
        // Restore the original transform
        g2.setTransform(oldTransform);
    }

   
    @Override
    public void spawn(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public int getX() {
        return (int)x;
    }
    public int getY() {
        return (int)y;
    }

    public int getPosition(){
        return (int)x;
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x - width/2, y - height/2, width, height);
    }
}

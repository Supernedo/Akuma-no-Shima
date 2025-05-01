package com.novanation.akumanoshima;


import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface Entity {
    
    public void draw(Graphics2D g2);
    public void move(int direction);
    public void jump();
    public void performAction();

    public Rectangle2D.Double getEntityBounds();
    public int getHeight();

    public Chunk getCurrentChunk();
    public Chunk getNextChunk();
    public Chunk getPreviousChunk();
    public void update();

    public void setID(String id);
    public String getID();

    public double getX();
    public double getY();
    public double getDY();
    public void setY(double newY);
    public void moveY(double dy);
    public void setWorldPos(int xPos);

    public double getVelocityY();
    public void setVelocityY(double vy);

    public boolean isGrounded();
    public void setGrounded(boolean grounded);

    public int getWorldX();
    public void setWorldX(int x);

    public Health getHealth();
}

package com.novanation.akumanoshima;


import java.awt.Graphics2D;

public interface Projectile {    // look into angles
    
   
    public void move();
    public void hit(); 
    public void draw(Graphics2D g2);
    public void spawn(double x, double y, double angle);

}

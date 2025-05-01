package com.novanation.akumanoshima;

import java.awt.Graphics2D;

public interface Weapon {
    
    public void updateShooting();
    public void reload();
    public void setAmmoMultiplier(double multiplier);
    public void setReloadSpeedMultiplier(double multiplier);
    public void resetAmmoMultiplier();
    public void resetReloadSpeedMultiplier();
    void drawBullets(Graphics2D g2);
    
}

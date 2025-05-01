package com.novanation.akumanoshima;

import java.awt.Graphics2D;

public class SpecialWeapon implements Weapon{

    @Override
    public void updateShooting() { }
    @Override
    public void reload() { }
    @Override
    public void setAmmoMultiplier(double multiplier) { }
    @Override
    public void setReloadSpeedMultiplier(double multiplier) { }
    @Override
    public void resetAmmoMultiplier() { }
    @Override
    public void resetReloadSpeedMultiplier() { }
    @Override
    public void drawBullets(Graphics2D g2) { }
}

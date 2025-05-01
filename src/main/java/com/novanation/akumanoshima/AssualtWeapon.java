package com.novanation.akumanoshima;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class AssualtWeapon implements Weapon {

    private final int fireRate = 400;
    private final int BASE_AMMO = 10;
    
    private AmmoStock ammo;
    private InputHandler inputHandler;
    private Player player;
    public int ammoCount = 20;
    private int currentBulletIndex = 0;
    private ArrayList<Bullet> usableBullets;

    //weapon conditions
    private boolean arFiring;
    private boolean arCanFire;
    private boolean arIsEmpty;
    private long lastShotTime;
    private long reloadStartTime;
    private boolean isReloading;
    private double reloadSpeedMultiplier = 1.4;
    private double ammoMultiplier = 1.0;
    private double damageMultiplier = 1.0;

    //muzzle flash stuff
    private boolean showMuzzleFlash = false;
    private long muzzleFlashStartTime = 0;
    private static final int MUZZLE_FLASH_DURATION = 50; // Duration in milliseconds
    private static final float[] FLASH_FRACTIONS = {0.0f, 0.5f, 1.0f};
    private static final Color[] FLASH_COLORS = {
    new Color(255, 255, 200, 255), // Bright center
    new Color(255, 140, 0, 180),   // Orange middle
    new Color(255, 69, 0, 0)       // Transparent outer
    };

    private int frameCount;
    private int maxFrameCount = 500;

    //Rendering
    private BufferedImage weaponImage;
    private int x, y; 
    private double rotation; 
    private boolean facingLeft = false;

    private boolean canFire = false;

    public AssualtWeapon(Player player){
        this.player = player;
        // Set the weapon on the player after the constructor finishes
        this.player.setCurrentWeapon(this);
        initializeAmmo();
        arFiring = false;
        arCanFire = true;
        
         try {
            weaponImage = ImageIO.read(getClass().getResourceAsStream("/gfx/weapons/weapon_assault.png"));
            // If your image is elsewhere, adjust the path accordingly
        } catch (IOException e) { }
    }

    private void initializeAmmo() {
        int totalAmmo = (int)(ammoCount * ammoMultiplier);
        ammo = new AmmoStock(totalAmmo);
        usableBullets = ammo.bullets;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public void updateShooting(){
        long currTime = System.currentTimeMillis();
        frameCount++;

        // Update muzzle flash visibility
        if (showMuzzleFlash && currTime - muzzleFlashStartTime > MUZZLE_FLASH_DURATION) {
            showMuzzleFlash = false;

            // player.getPanel().getGameWindow().stopAudioClip("explosion", ClipType.SFX);
        }
    
        if(arFiring && arCanFire && !isReloading) {
            if(currTime - lastShotTime >= fireRate) {
                if(currentBulletIndex < ammoCount * ammoMultiplier) {
                    double angle = inputHandler.getAngle();
                    usableBullets.get(currentBulletIndex).spawn(x, y, angle);
                    currentBulletIndex++;
                    lastShotTime = currTime;
                    
                    // Trigger muzzle flash
                    showMuzzleFlash = true;
                    muzzleFlashStartTime = currTime;
                    
                } else {
                    arIsEmpty = true;
                    arCanFire = false;
                    isReloading = true;
                    reloadStartTime = System.currentTimeMillis();
                    player.getPanel().getGameWindow().playAudioClip("weapon_reload", ClipType.SFX, false);
                }
            }
        }

        for(int i = 0; i < currentBulletIndex; i++) {
            Bullet bullet = usableBullets.get(i);
            usableBullets.get(i).move();

            if (frameCount <= maxFrameCount && isArFiring() && !isReloading)
            {
                if (!canFire && frameCount < 60)
                {
                    player.getPanel().getGameWindow().playAudioClip("weapon_shoot", ClipType.SFX, true);
                    canFire = true;
                }
            }
            else
            {
                if (canFire && frameCount > 60)
                {
                    player.getPanel().getGameWindow().stopAudioClip("weapon_shoot", ClipType.SFX);
                    canFire = false;
                }

                if (frameCount > 60)
                    frameCount = 0;
            }
                

            if (bullet.isActive()) {
                for (Entity enemy : EnemyManager.getAllEnemies()) {
                    bullet.checkCollision(enemy);
                }
            }
        }
        
    }


    @Override
    public void drawBullets(Graphics2D g2) {
        if (g2 != null) {
            for(int i = 0; i < currentBulletIndex; i++) {
                if (usableBullets != null && i < usableBullets.size()) {
                    usableBullets.get(i).draw(g2);
                }
            }
        }
    }

    // not in use 
    @Override
    public void reload() { }

    public void updateReloading(){
        if (isReloading) {
            long reloadTime = (long) (2000 * reloadSpeedMultiplier);
            if (System.currentTimeMillis() - reloadStartTime >= reloadTime) {
                initializeAmmo(); // Reinitialize ammo with current multiplier
                currentBulletIndex = 0;
                
                
                arIsEmpty = false;
                arCanFire = true;
                isReloading = false;
            }
        }
    }

    public void render(Graphics2D g) {
       if (weaponImage == null) return;
    
    // Save the current transform
    AffineTransform oldTransform = g.getTransform();
    
    // Create a new transform for the weapon
    AffineTransform transform = new AffineTransform();
    transform.translate(x, y);
    transform.rotate(rotation);
    
    // If facing left, flip the image horizontally
    if (facingLeft) {
        transform.scale(1, -1);
    }

    // Apply the transform
    g.setTransform(transform);
    
    // Draw the weapon image centered at origin point
    g.drawImage(weaponImage, -weaponImage.getWidth()/2, -weaponImage.getHeight()/2, null);
    
    // Draw muzzle flash if active
    if (showMuzzleFlash) {
        // Calculate muzzle position (at the end of the weapon)
        int muzzleX = weaponImage.getWidth()/2 + 10;
        int muzzleY = 0;
        
        // Create gradient for muzzle flash
        Point2D center = new Point2D.Float(muzzleX, muzzleY);
        float radius = 20.0f;


        RadialGradientPaint gradient = new RadialGradientPaint(
            center,
            radius,
            FLASH_FRACTIONS,
            FLASH_COLORS,
            CycleMethod.NO_CYCLE
        );
        
        // Draw the muzzle flash
        g.setPaint(gradient);
        g.fillOval(muzzleX - (int)radius, muzzleY - (int)radius, 
                  (int)radius * 2, (int)radius * 2);
    }
    
    // Restore the original transform
    g.setTransform(oldTransform);
     }



    public boolean isArFiring() {
        return arFiring;
    }

    public void setArIsFiring(boolean arFiring) {
        this.arFiring = arFiring;
    }

    public void setArCanFire(boolean arCanFire) {
        this.arCanFire= arCanFire;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void setAmmoMultiplier(double multiplier) {
        this.ammoMultiplier = multiplier;
        // Reinitialize ammo when multiplier changes
        int totalAmmo = (int)(BASE_AMMO * ammoMultiplier);
        ammo = new AmmoStock(totalAmmo);
        usableBullets = ammo.bullets;
    }

    @Override
    public void setReloadSpeedMultiplier(double multiplier) {
        this.reloadSpeedMultiplier = multiplier;
    }

    @Override
    public void resetAmmoMultiplier() {
        this.ammoMultiplier = 1.0;
    }

    @Override
    public void resetReloadSpeedMultiplier() {
        this.reloadSpeedMultiplier = 1.0;
    }

    public AmmoStock getAmmo() {
        return ammo;
    }

    public void setAmmo(AmmoStock ammo) {
        this.ammo = ammo;
    }

    public ArrayList<Bullet> getUsableBullets() {
        return usableBullets;
    }

    public void setUsableBullets(ArrayList<Bullet> usableBullets) {
        this.usableBullets = usableBullets;
    }

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    // Getter for BASE_AMMO
    public int getBaseAmmo() {
        return BASE_AMMO;
    }

    // Getter and setter for ammoMultiplier
    public double getAmmoMultiplier() {
        return ammoMultiplier;
    }

    // Getter and setter for reloadSpeedMultiplier
    public double getReloadSpeedMultiplier() {
        return reloadSpeedMultiplier;
    }

    // Getter for current bullet index
    public int getCurrentBulletIndex() {
        return currentBulletIndex;
    }
     // Getter for isReloading
     public boolean isReloading() {
        return isReloading;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }
    public boolean isFacingLeft() { return facingLeft; }
    public void setFacingLeft(boolean facingLeft) { this.facingLeft = facingLeft; }
    
    
    
}

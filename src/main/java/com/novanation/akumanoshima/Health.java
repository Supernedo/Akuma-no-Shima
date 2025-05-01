package com.novanation.akumanoshima;


import java.awt.Graphics2D;
import java.awt.Image;

public class Health {

    private final int heartSize = 32; // Size in pixels
    private final int heartOffset = 48; // Distance in pixels

    private final Image healthImage;
    private final Image halfHealthImage;
    private final Image damagedHealthImage;
    private final Image bonusHealthImage;

    // Health values
    private int maxHP = 24;
    private final int DEFAULT_HP = 20;

    private int currentHealth = 20;

    private boolean isDead = false;

    public Health(boolean isPlayer)
    {
        healthImage = ImageManager.loadImage("/gfx/images/ui/heart_filled.png");
        halfHealthImage = ImageManager.loadImage("/gfx/images/ui/heart_half_filled.png");
        damagedHealthImage = ImageManager.loadImage("/gfx/images/ui/heart_empty.png");
        bonusHealthImage = ImageManager.loadImage("/gfx/images/ui/heart_filled_bonus.png");

        if (!isPlayer)
        {
            maxHP = 100;
            currentHealth = maxHP;
        }
    }

    public void dealDamage(int damage, boolean isPlayer, Entity entity)
    {
        damage = Math.abs(damage); // In case of damage being negative

        if (isPlayer)
        {
            Player newPlayer = (Player) entity;
            newPlayer.getPanel().getGameWindow().playAudioClip("player_hit", ClipType.SFX, false);
        }

        if (currentHealth > DEFAULT_HP)
            currentHealth -= 2;
        else if (currentHealth - damage <= 0)
            if (isPlayer)
                killPlayer();
            else
                destroyEntity(entity);
        else
            currentHealth -= damage;
    }

    public void destroyEntity(Entity entity) { EnemyManager.destroyEntity(entity); }

    // TODO: Complete (Add Death Screen, etc)
    public void killPlayer()
    {
        if (!isDead)
        {
            currentHealth = 0;
            isDead = true;
        }
    }

    public boolean isDead() { return isDead; }
    public void setDead(boolean deadState) { isDead = deadState; }

    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHP; }

    public void setCurrentHealth(int hp) { currentHealth = hp; }
    public void setMaxHealth(int hp) { maxHP = hp; }

    public void addHealth(int hp)
    {
        hp = Math.abs(hp); // In case of health being negative

        if (currentHealth + hp >= DEFAULT_HP)
            currentHealth += 2;
        else if (hp + currentHealth > maxHP)
            currentHealth = maxHP;
        else
            currentHealth += hp;

        if (currentHealth > maxHP)
            currentHealth = maxHP;
    }

    public void draw(Graphics2D g2)
    {
        int x = heartOffset / 2;
        int y = heartOffset / 2;

        double hp = Math.ceil((double) (DEFAULT_HP / 2.0));
    
        int fullHearts = currentHealth / 2;
        boolean hasHalfHeart = (currentHealth % 2) == 1;

        for (int i = 0; i < fullHearts; i++)
            g2.drawImage(healthImage, (i * heartOffset) + (heartOffset / 2), y, heartSize, heartSize, null);

        if (hasHalfHeart && (int)(currentHealth / 2) < hp)
            g2.drawImage(halfHealthImage, x + (fullHearts * heartOffset), y, heartSize, heartSize, null);

        if ((int)(currentHealth / 2) < hp)
            for (int i = (currentHealth / 2); i < hp; i++)
                g2.drawImage(damagedHealthImage, (i * heartOffset) + (heartOffset / 2), y, heartSize, heartSize, null);
    
        if ((int)(currentHealth / 2) > hp)
            for (int i = (int) hp; i < (currentHealth / 2); i++)
                g2.drawImage(bonusHealthImage, (i * heartOffset) + (heartOffset / 2), y, heartSize, heartSize, null);
    }
}

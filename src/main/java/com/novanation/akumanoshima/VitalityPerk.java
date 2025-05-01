package com.novanation.akumanoshima;

public class VitalityPerk implements Perk{

    private final String name = "Vitality";
    private final String description = "Increases maximum health by one heart.";
    private boolean isActive = false;
    private final int HEALTH_BONUS = 1;

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void applyEffect(Player player) {
        Health playerHealth = player.getHealth();
        if (playerHealth != null) {
            playerHealth.addHealth(HEALTH_BONUS);
        }
    }

    @Override
    public void removeEffect(Player player) {
        Health playerHealth = player.getHealth();
        if (playerHealth != null) {
            playerHealth.dealDamage(HEALTH_BONUS, true, player);
        }
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
}

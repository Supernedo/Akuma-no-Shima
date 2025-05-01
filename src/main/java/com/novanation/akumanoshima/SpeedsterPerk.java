package com.novanation.akumanoshima;

public class SpeedsterPerk implements Perk {
    private final String name = "Speedster";
    private final String description = "Increases movement speed by 20%.";
    private boolean isActive = false;
    private final double SPEED_MULTIPLIER = 3.0;  

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
        player.setMoveSpeedMultiplier(SPEED_MULTIPLIER);
        isActive = true;
    }

    @Override
    public void removeEffect(Player player) { }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
}

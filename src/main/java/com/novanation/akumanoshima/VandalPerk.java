package com.novanation.akumanoshima;

public class VandalPerk implements Perk{

    private final String name = "Vandal";
    private final String description = "Increases maximum ammunition and reload speed.";
    public double ammoMultiplier = 1.5; 
    private double reloadSpeedMultiplier = 0.75; 
    private boolean isActive = false;

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
        Weapon weapon = player.getWeapon();
        if (weapon instanceof AssualtWeapon assaultWeapon) {
            assaultWeapon.setAmmoMultiplier(ammoMultiplier);
            assaultWeapon.setReloadSpeedMultiplier(reloadSpeedMultiplier);
        }
    }

    @Override
    public void removeEffect(Player player) {
      
        }
    

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    
}


}
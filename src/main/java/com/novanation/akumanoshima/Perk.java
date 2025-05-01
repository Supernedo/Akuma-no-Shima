package com.novanation.akumanoshima;

public interface Perk {
    String getName();
    String getDescription();
    void applyEffect(Player player);
    void removeEffect(Player player);
}

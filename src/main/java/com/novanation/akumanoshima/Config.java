package com.novanation.akumanoshima;

import java.util.HashMap;
import java.util.Map;

public class Config {

    // Graphics settings
    private boolean fullscreenMode;
    private int resolutionWidth;
    private int resolutionHeight;

    // Game state
    private int level;
    private Difficulty difficulty;
    private WorldType worldType;
    private String currentWeapon;
    private int currentAmmo;
    private int playerHealth;
    private int playerScore;
    private boolean isEndless;
    private Map<String, Integer> enemiesDefeated;
    private Map<String, Boolean> unlockedWeapons;
    private Map<String, Integer> weaponAmmo;

    // Audio settings
    private float sfxVolume;
    private float menuVolume;
    private float musicVolume;
    private float masterVolume;

    // Default constructor
    public Config() {
        // Set default values
        this.fullscreenMode = false;
        this.resolutionWidth = 1366;
        this.resolutionHeight = 768;
        this.level = 1;
        this.difficulty = Difficulty.NORMAL;
        this.worldType = WorldType.FOREST;
        this.currentWeapon = "Assault";
        this.currentAmmo = 30;
        this.sfxVolume = 1.0f;
        this.menuVolume = 1.0f;
        this.musicVolume = 1.0f;
        this.masterVolume = 1.0f;
        this.playerHealth = 20;
        this.playerScore = 0;
        this.isEndless = false;
        this.enemiesDefeated = new HashMap<>();
        this.unlockedWeapons = new HashMap<>();
        this.weaponAmmo = new HashMap<>();

        unlockedWeapons.put("Assault", true);
        weaponAmmo.put("Assault", 30);
    }

    // Accessors and mutators

    // Graphics
    public boolean isFullscreenMode() { return fullscreenMode; }
    public void setFullscreenMode(boolean fullscreenMode) { this.fullscreenMode = fullscreenMode; }

    public int getResolutionWidth() { return resolutionWidth; }
    public void setResolutionWidth(int resolutionWidth) { this.resolutionWidth = resolutionWidth; }

    public int getResolutionHeight() { return resolutionHeight; }
    public void setResolutionHeight(int resolutionHeight) { this.resolutionHeight = resolutionHeight; }

    // Game state
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public WorldType getWorldType() { return worldType; }
    public void setWorldType(WorldType worldType) { this.worldType = worldType; }

    public String getCurrentWeapon() { return currentWeapon; }
    public void setCurrentWeapon(String currentWeapon) { this.currentWeapon = currentWeapon; }

    public int getCurrentAmmo() { return currentAmmo; }
    public void setCurrentAmmo(int currentAmmo) { this.currentAmmo = currentAmmo; }

    public int getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(int health) { this.playerHealth = health; }

    public int getPlayerScore() { return playerScore; }
    public void setPlayerScore(int score) { this.playerScore = score; }

    public boolean isEndless() { return isEndless; }
    public void setEndless(boolean endless) { this.isEndless = endless; }

    public Map<String, Integer> getEnemiesDefeated() { return enemiesDefeated; }
    public void setEnemiesDefeated(Map<String, Integer> enemies) { this.enemiesDefeated = enemies; }

    public Map<String, Boolean> getUnlockedWeapons() { return unlockedWeapons; }
    public void setUnlockedWeapons(Map<String, Boolean> weapons) { this.unlockedWeapons = weapons; }

    public Map<String, Integer> getWeaponAmmo() { return weaponAmmo; }
    public void setWeaponAmmo(Map<String, Integer> ammo) { this.weaponAmmo = ammo; }

    // Audio
    public float getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(float sfxVolume) { this.sfxVolume = sfxVolume; }

    public float getMenuVolume() { return menuVolume; }
    public void setMenuVolume(float menuVolume) { this.menuVolume = menuVolume; }

    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }

    public float getMasterVolume() { return masterVolume; }
    public void setMasterVolume(float masterVolume) { this.masterVolume = masterVolume; }
}

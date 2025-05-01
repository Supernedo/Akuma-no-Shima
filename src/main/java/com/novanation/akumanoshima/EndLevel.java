package com.novanation.akumanoshima;

import java.util.HashMap;

public class EndLevel implements Level {

    private static final HashMap<Integer, Tile> tileMap = new HashMap<>();
    private static final HashMap<Integer, Chunk> chunkMap = new HashMap<>();

    // Level presets
    private static final int TILE_LENGTH = 64;                  // Length of a tile in pixels
    private static final int WORLD_LENGTH = 80;                 // Total amount of tiles that can be made
    private static final int BASE_HEIGHT = 64;                  // Starting height for world generation
    private static final int CHUNK_WIDTH = 1;

    private int currentTile = 0;

    // Game Panel
    private final GamePanel panel;
    private int panelHeight;

    public EndLevel(GamePanel panel) { this.panel = panel; }

    public void createChunk(int elevation)
    {
        Chunk newChunk = new Chunk((currentTile * TILE_LENGTH), panelHeight, CHUNK_WIDTH, (elevation / TILE_LENGTH), TILE_LENGTH, TileType.PRIMARY, WorldType.END);
        chunkMap.put(currentTile * TILE_LENGTH, newChunk);
    }

    @Override
    public void createLevel()
    { 
        panelHeight = panel.getHeight() - BASE_HEIGHT - (TILE_LENGTH / 2);

        GameWindow window = panel.getGameWindow();
        
        EnemyManager.generateEnemies(window.getConfig().getDifficulty(), true, panel);

        for (int i = 0; i < WORLD_LENGTH; i++)
        {
            setMainTile();
            currentTile++;
        }
     }

    @Override
    public void setMainTile()
    { 
        Tile newTile = new Tile(panel, (currentTile * TILE_LENGTH), panelHeight, TILE_LENGTH, TileType.PRIMARY, WorldType.END);
        tileMap.put(newTile.getX(), newTile);

        createChunk(TILE_LENGTH);
     }

    @Override
    public void setSecondaryTile() { }

    @Override
    public void setTertiaryTile() { }

    @Override
    public void setVoidTile() { }

    @Override
    public void setElevation(int randHeight) { }

    @Override
    public void fillAirGaps(TileType tileType) { }

    @Override
    public void generateFloatingPlatform() { }

    @Override
    public int getTileLength() { return TILE_LENGTH; }

    @Override
    public HashMap<Integer, Tile> getTileDictionary() { return tileMap; }

    @Override
    public HashMap<Integer, Tile> getTileDepthDictionary() { return null; }

    @Override
    public HashMap<Integer, Chunk> getChunkDictionary() { return chunkMap; }
}

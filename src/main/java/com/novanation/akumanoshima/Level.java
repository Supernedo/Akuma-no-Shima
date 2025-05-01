package com.novanation.akumanoshima;


import java.util.HashMap;

public interface Level {
    
    public void createLevel();

    public void setMainTile();
    public void setSecondaryTile();
    public void setTertiaryTile();
    public void setVoidTile();

    public void setElevation(int randHeight);
    public void fillAirGaps(TileType tileType);
    public void generateFloatingPlatform();
    public int getTileLength();

    public HashMap<Integer, Tile> getTileDictionary();
    public HashMap<Integer, Tile> getTileDepthDictionary();
    public HashMap<Integer, Chunk> getChunkDictionary();
}

package com.novanation.akumanoshima;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GamePanel extends Scene {

    private final GameWindow window;

    private int playerHeight;
    private int playerWidth;

    private int worldOffsetX;

    private final int FINAL_LEVEL = 3;
    private boolean isEndless = false;

    private boolean isPlaying = false;

    // Parallax background variables
    private final BufferedImage image;
    private BackgroundManager backgroundManager;

    // Thread variables
    private Thread gameThread;
    private final int FPS = 120;
    private final double frameTimePacing = 8.33f; // milliseconds

    private final int RESPAWN_TIME = 180;
    private int frameCount = 0;

    // Entity Variables
    private Player playerEntity;
    private InputHandler playerInput;
    private CameraControls camera;
    private AssualtWeapon ar;

    private Collection<Tile> tiles;
    private Collection<Tile> tileDepths;

    //cursor
    private Cursor blankCursor;
    private BufferedImage cursorImg;

    private final Image deathImage;

    private int sfxFrameCount = 0;
    private int newRandomAmbient = 0;

    private final int LOWER_AMBIENT = 300;
    private final int UPPER_AMBIENT = 500;

    private Random random = new Random();

    public GamePanel(GameWindow window)
    {
        this.window = window;
        setPreferredSize(new Dimension(this.window.getWidth(), this.window.getHeight()));

        newRandomAmbient = random.nextInt(LOWER_AMBIENT, UPPER_AMBIENT) + 1;

        worldOffsetX = 0;

        cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
        cursorImg, new Point(0, 0), "blank cursor");
        
        // Set blank cursor to hide default cursor
        setCursor(blankCursor);
        image = new BufferedImage(this.window.getWidth(), this.window.getHeight(), BufferedImage.TYPE_INT_RGB);

        deathImage = ImageManager.loadImage("/gfx/images/ui/player_death.png");
    }

    private void saveGameState() {
        Config config = window.getConfig();
        
        // Save player state
        if (playerEntity != null) {
            // config.setPlayerHealth(playerEntity.getHealth().getCurrentHealth());
            // TODO: Save weapon state
        }

        // Save level progress
        config.setLevel(window.getCurrentLevel());
        config.setEndless(isEndless);

        // Save enemy stats
        Map<String, Integer> defeatedEnemies = new HashMap<>();
        for (Entity enemy : EnemyManager.getAllEnemies()) {
            String enemyType = enemy.getClass().getSimpleName();
            defeatedEnemies.merge(enemyType, 1, Integer::sum);
        }
        config.setEnemiesDefeated(defeatedEnemies);

        // Save to file
        ConfigManager.saveConfig(config);
    }

    public void setWorldOffsetX(int speed) { worldOffsetX += speed; }
    public int getWorldOffsetX() { return -worldOffsetX; }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        doubleBufferBackground();

        g.dispose();
    }

    private void doubleBufferBackground()
    {
        Graphics2D imageContext = (Graphics2D) image.getGraphics();

        if (backgroundManager != null)
            backgroundManager.draw(imageContext);
        
        if(ar != null) {
            ar.render(imageContext);
            ar.drawBullets(imageContext);
        }
        if (playerEntity != null)
        {
            playerEntity.draw(imageContext);
            
            if (playerEntity.getHealth() != null)
                playerEntity.getHealth().draw(imageContext);

            PlayerAnimation playerAnimation = playerEntity.getPlayerAnimation();
            
            if (playerInput.getDirection() != 0)
            {
                if (!playerAnimation.isStillActive())
                    playerAnimation.start();

                playerAnimation.draw(imageContext);
            }
            else
                playerAnimation.stop();

            // Draw the player's current chunk
            // if (playerEntity.getCurrentChunk() != null)
            //     playerEntity.getCurrentChunk().showChunkBounds(imageContext);

            // Draw the player collider
            // imageContext.setColor(new Color(255, 255, 255, 128));
            // if (playerEntity.getEntityBounds() != null)
            //     imageContext.fill(playerEntity.getEntityBounds());

             // Draw custom crosshair at clamped position
            if (playerInput != null) {
                int crosshairX = playerInput.getMouseX();
                int crosshairY = playerInput.getMouseY();
                
                playerInput.updateWeaponPosition();

                // Draw crosshair
                int size = 10;
                imageContext.setColor(Color.WHITE);
                
                // Outer circle
                // imageContext.drawOval(crosshairX - size, crosshairY - size, size * 2, size * 2);
                
                // Inner dot
                imageContext.fillOval(crosshairX - 2, crosshairY - 2, 4, 4);
                
                // Cross lines
                imageContext.drawLine(crosshairX - size, crosshairY, crosshairX + size, crosshairY);
                imageContext.drawLine(crosshairX, crosshairY - size, crosshairX, crosshairY + size);
            }
        }
        
        EnemyManager.draw(imageContext);

        imageContext.setColor(Color.WHITE);  // cross hair
        
        if (LevelManager.showLevelClear() && LevelManager.isSetUp())
        {
            long elapsed = System.currentTimeMillis() - LevelManager.levelClearStartTime();
            if (!isPlaying)
            {
                window.stopAudioClip("end_1", ClipType.MUSIC);
                window.stopAudioClip("end_2", ClipType.MUSIC);
                window.stopAudioClip("end_3", ClipType.MUSIC);

                window.stopAudioClip("forest", ClipType.AMBIENT);
                window.stopAudioClip("blizzard", ClipType.AMBIENT);
                window.stopAudioClip("lava_1", ClipType.AMBIENT);
                window.stopAudioClip("lava_2", ClipType.AMBIENT);

                window.playAudioClip("round_change", ClipType.SFX, false);

                if (!window.getRespawned())
                    window.setCurrentLevel(window.getCurrentLevel() + 1);
                else
                    window.setRespawned(false);

                isPlaying = true;
            }

            if (elapsed <= 3000)
            {
                int x = LevelManager.getImgClearX();
                int y = LevelManager.getImgClearY();
        
                imageContext.drawImage(LevelManager.getClearImage(), x, y, this);
            }
            else
            {
                LevelManager.setSetup(false);
                LevelManager.setLevelClear(false);
                startNewLevel();
            }
        }

        if (LevelManager.isFinalLevel() && LevelManager.isSetUp())
        {
            long elapsed = System.currentTimeMillis() - LevelManager.levelClearStartTime();
            
            if (!isPlaying)
            {
                window.stopAudioClip("end_1", ClipType.MUSIC);
                window.stopAudioClip("end_2", ClipType.MUSIC);
                window.stopAudioClip("end_3", ClipType.MUSIC);

                window.playAudioClip("game_win", ClipType.SFX, false);
                window.setCurrentLevel(1);
                isPlaying = true;
            }

            if (elapsed <= 5000)
            {
                int x = LevelManager.getImgWinX();
                int y = LevelManager.getImgWinY();
        
                imageContext.drawImage(LevelManager.getWinImage(), x, y, this);
            }
            else
                resetToMenu();
        }

        // Draw the tiles last to ensure that the lava or water is over the player
        if (tiles != null)
            for (Tile tile : tiles)
                tile.draw(imageContext);

        if (tileDepths != null)
            for (Tile tile : tileDepths)
                tile.draw(imageContext);

        if (playerEntity.getHealth().isDead())
        {
            imageContext.drawImage(deathImage, 0, 0, null);

            window.stopAudioClip("game_win", ClipType.SFX);
            window.stopAudioClip("forest", ClipType.AMBIENT);
            window.stopAudioClip("blizzard", ClipType.AMBIENT);
            window.stopAudioClip("lava_1", ClipType.AMBIENT);
            window.stopAudioClip("lava_2", ClipType.AMBIENT);

            window.setRespawned(true);

            if (!isPlaying)
                window.playAudioClip("player_death", ClipType.SFX, false);
        }

        imageContext.dispose();
    }

    public void resetToMenu()
    {
        saveGameState();
        LevelManager.setFinal(false);

        // Return the player to the main menu
        SceneLoader.switchScene("Menu");
        window.stopAudioClip("round_change", ClipType.SFX);
        window.stopAudioClip("game_win", ClipType.SFX);

        window.stopAudioClip("forest", ClipType.AMBIENT);
        window.stopAudioClip("blizzard", ClipType.AMBIENT);
        window.stopAudioClip("lava_1", ClipType.AMBIENT);
        window.stopAudioClip("lava_2", ClipType.AMBIENT);

        window.stopAudioClip("end_1", ClipType.MUSIC);
        window.stopAudioClip("end_2", ClipType.MUSIC);  
        window.stopAudioClip("end_3", ClipType.MUSIC);

        window.playAudioClip("Menu", ClipType.MENU, true);
        stopGameThread();
    }

    public void startNewLevel()
    {
        saveGameState();

        SceneLoader.switchScene("LoadingPanel");
        stopGameThread();
        window.loadGame();
    }

    private void loadGameState() {
        Config config = window.getConfig();
        
        // Set endless mode
        isEndless = config.isEndless();

        // Load weapon ammo
        if (ar != null) {
            int savedAmmo = config.getWeaponAmmo().getOrDefault("Assault", 30);
            // TODO: Utilize ammo
        }

        // Load player health if respawning
        if (window.getRespawned() && playerEntity != null) {
            playerEntity.getHealth().setCurrentHealth(config.getPlayerHealth());
        }
    }

    public void updateEntityCalculations()
    {
        sfxFrameCount++;

        if (sfxFrameCount >= newRandomAmbient)
        {
            int lava = WorldGeneration.getWorldType() == WorldType.VOLCANIC ? 1 : 0;

            int randomTrack = random.nextInt(1, 2) + lava;

            if (WorldGeneration.getWorldType() == WorldType.VOLCANIC)
            {
                if (randomTrack == 1)
                    window.playAudioClip("lava_1", ClipType.AMBIENT, false);
                else
                 window.playAudioClip("lava_2", ClipType.AMBIENT, false);
            }
            else if (WorldGeneration.getWorldType() == WorldType.BLIZZARD)
                window.playAudioClip("blizzard", ClipType.AMBIENT, false);
            
            sfxFrameCount = 0;
            newRandomAmbient = random.nextInt(LOWER_AMBIENT, UPPER_AMBIENT) + 1;
        }

        if (playerEntity.getHealth().isDead())
        {
            frameCount++;

            if (frameCount > RESPAWN_TIME)
            {
                frameCount = 0;
                playerEntity.getHealth().setDead(false);

                resetToMenu();
            }
        }

        if (playerInput != null)
            if (!playerInput.getLocking())
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            else
                setCursor(blankCursor);

        // Constantly apply gravity to the player
        Physics.applyGravity(playerEntity, playerEntity.getX(), playerEntity.getY());

        // Add entities to a separate list to prevent modification during iteration
        ArrayList<Entity> entityList = new ArrayList<>(EnemyManager.getEnemies().values());
        
        for (Entity entity : entityList) {
            Physics.applyGravity(entity, entity.getX(), entity.getY());
            entity.update();
        }
            
        playerEntity.update();
        
        // Create another copy for performAction
        entityList = new ArrayList<>(EnemyManager.getAllEnemies());
        for (Entity enemy : entityList) {
            if (enemy instanceof EnemyOni oni) {
                oni.performAction();
            }
        }
        // This will keep track of the world and player and update their locations accordingly
        camera.update();
        if(ar != null) {
            ar.updateShooting();
            ar.updateReloading();
        }

        LevelManager.update();
    }

    public GameWindow getGameWindow() { return window; }

    public Player getPlayerEntity() { return playerEntity; }

    public int getScaledWidth() { return playerWidth; }
    public int getScaledHeight() { return playerHeight; }

    public void createGameEntities()
    {
        double widthScale = (double) window.getConfig().getResolutionWidth() / window.getWidth();
        double heightScale = (double) window.getConfig().getResolutionHeight() / window.getHeight();

        playerHeight = (int) (130 * heightScale);
        playerWidth = (int) (60 * widthScale);

        Physics.setPanel(this);
      
        LevelManager.setupToast(this);
        LevelManager.setLevelClear(false);
        LevelManager.setFinal(false);

        WorldType world = WorldGeneration.getRandomWorld();

        if (window.getCurrentLevel() >= FINAL_LEVEL && !isEndless)
        {
            WorldGeneration.generateLevel(this, WorldType.END);

            int randomTrack = random.nextInt(1, 4);

            switch (randomTrack)
            {
                case 1 -> window.playAudioClip("end_1", ClipType.MUSIC, true);
                case 2 -> window.playAudioClip("end_2", ClipType.MUSIC, true);
                case 3 -> window.playAudioClip("end_3", ClipType.MUSIC, true);
            }

            backgroundManager = new BackgroundManager(this, window, WorldType.END);
        }
        else
        {
            WorldGeneration.generateLevel(this, world);

            // if (WorldGeneration.getWorldType() == WorldType.FOREST)
            //     window.playAudioClip("forest", ClipType.AMBIENT, true);

            backgroundManager = new BackgroundManager(this, window, world);
        }
            
        tiles = WorldGeneration.getAllTiles();
        tileDepths = WorldGeneration.getAllDepthTiles();

        playerEntity = new Player(this, 30, (getHeight() - playerHeight) - (int) (WorldGeneration.getTileLength() * 1.5) - 30, playerWidth, playerHeight);
        playerEntity.setWorldPos((int) playerEntity.getX());

        EnemyManager.setPlayer(playerEntity);

        if (window.getCurrentLevel() >= FINAL_LEVEL && !isEndless)
            EnemyManager.generateEnemies(window.getConfig().getDifficulty(), true, this);
        else
            EnemyManager.generateEnemies(window.getConfig().getDifficulty(), false, this);

        playerInput = new InputHandler(playerEntity);
        camera = new CameraControls(playerEntity, playerInput, backgroundManager);
        ar = new AssualtWeapon(playerEntity);
        ar.setInputHandler(playerInput);
        playerInput.setWeapon(ar);      
        
        loadGameState();

        addKeyListener(playerInput);

        //Mouse setup
       
        addMouseListener(playerInput);
        addMouseMotionListener(playerInput);

        repaint();
    }

    public void startGameThread()
    {
        if (gameThread == null || !gameThread.isAlive())
        {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stopGameThread() { gameThread.interrupt(); gameThread = null; }

    @Override
    public void run()
    {
        double drawInterval = 100000000/FPS;
		long lastTime = System.nanoTime();
		double timeDelta = 0;
		
		while(!Thread.currentThread().isInterrupted())
        {
			long currentTime = System.nanoTime();

			timeDelta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;

            // Update the game accordingly to match the FTP of 60FPS
			if(timeDelta >= frameTimePacing)
            {
                updateEntityCalculations();
                repaint();
				timeDelta -= frameTimePacing;
		    }
		}
    }
}

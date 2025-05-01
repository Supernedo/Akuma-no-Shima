package com.novanation.akumanoshima;


import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Menu extends Scene {

    // Thread Variables
    private Thread menuThread;
    private final int FPS = 120;
    private final double frameTimePacing = 8.33f; // milliseconds

    // Buttons
    private Polygon startButton;
    private Polygon settingsButton;
    private Polygon exitButton;

    private final GameWindow window;

    private final MenuAnimation menuAnimation;

    private final BufferedImage image;
    private final Image loadingImage;

    public Menu(GameWindow window)
    {
        this.window = window;
        menuAnimation = new MenuAnimation(this.window);

        setPreferredSize(new Dimension(this.window.getWidth(), this.window.getHeight()));

        image = new BufferedImage(this.window.getWidth(), this.window.getHeight(), BufferedImage.TYPE_INT_RGB);
        loadingImage = ImageManager.loadImage("/gfx/images/background/main_menu.png");

        startMenuThread();
    }

    public void startMenuThread()
    {
        menuThread = new Thread(this);
        menuThread.start();

        menuAnimation.start();

        handleButtonListeners();
        window.playAudioClip("Menu", ClipType.MENU, true);
    }

    public void stopThread() { menuAnimation.stop(); menuThread.interrupt(); window.stopAudioClip("Menu", ClipType.MENU); }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        Graphics2D g2 = (Graphics2D) g;
        draw(g2);
        menuAnimation.draw(g2);

        g.dispose();
    }

    private void update()
    {
        startButton = generateStepButton(0); // Start Game
        settingsButton = generateStepButton(1); // Settings
        exitButton = generateStepButton(2); // Quit
    }

    private void handleButtonListeners()
    {
        addMouseMotionListener((MouseMotionListener) new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            if (startButton == null || settingsButton == null || exitButton == null)
                return;

            if (startButton.contains(e.getPoint()) || settingsButton.contains(e.getPoint()) || exitButton.contains(e.getPoint()))
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            else
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }});

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (startButton == null || settingsButton == null || exitButton == null)
                    return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (startButton.contains(e.getPoint()))
                        startGame();
                    if (settingsButton.contains(e.getPoint()))
                        loadSettings();
                    if (exitButton.contains(e.getPoint()))
                        exitGame();
                }
            }
        });
    }

    private void startGame()
    {
        // TODO: Check if game save already exists
        
        stopThread();
        window.loadGame();
    }

    private void loadSettings() { SceneLoader.switchScene("Settings"); }

    private void exitGame()
    {
        // TODO: Save game state

        // TODO: Customize this panel
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit",
        JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private Polygon generateStepButton(int step)
    {
        int xOffset = 52 * step;
        int yOffset = 48 * step;

        // For some reason it wants to be difficult (Look into this)
        int panelOffset = 0; 
        switch (step) {
            case 0 -> panelOffset = -30;
            case 1 -> panelOffset = 30;
            case 2 -> panelOffset = 70;
        }

        int[] xPoints = {908, 1170, 1123, 853};
        int[] yPoints = {477, 477, 531, 531};

        // Update points
        for (int i = 0; i < yPoints.length; i++)
        {
            xPoints[i] -= xOffset;
            yPoints[i] += yOffset + panelOffset;
        }

        Polygon parallelogramButton = new Polygon(xPoints, yPoints, 4);

        return parallelogramButton;
    }

    private void draw(Graphics2D g2) { g2.drawImage(loadingImage, 0, 0, window.getWidth(), this.window.getHeight() - 35, null); }

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

            // Update the loading accordingly to match the FTP of 60FPS
			if(timeDelta >= frameTimePacing)
            {
                repaint();
                update();
				timeDelta -= frameTimePacing;
		    }
		}
    }
}

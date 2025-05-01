package com.novanation.akumanoshima;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class LoadingPanel extends JPanel implements Runnable {

    private Thread loadingThread;
    private final int FPS = 120;
    private final double frameTimePacing = 8.33f; // milliseconds

    private final BufferedImage image;
    private final Image loadingImage;

    private final GameWindow window;

    private int currentProgress;
    
    public LoadingPanel(GameWindow window)
    {
        this.window = window;
        setPreferredSize(new Dimension(this.window.getWidth(), this.window.getHeight()));

        image = new BufferedImage(this.window.getWidth(), this.window.getHeight(), BufferedImage.TYPE_INT_RGB);
        loadingImage = ImageManager.loadImage("/gfx/images/background/loading_scene.png");

        currentProgress = 0;
    }
    
    public void startLoadThread()
    {
        currentProgress = 0;

        loadingThread = new Thread(this);
        loadingThread.start();
    }

    public void stopThread() { loadingThread.interrupt(); }
    public boolean isRunning() { return loadingThread.isAlive(); }

    public void draw(Graphics2D g2) { g2.drawImage(loadingImage, 0, 0, window.getWidth(), this.window.getHeight(), null); }
    public void incrementProgress(int progress) { currentProgress += progress; }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        Graphics2D g2 = (Graphics2D) g;
        draw(g2);

        // It's 620 pixels in length for the loading bar
        // TODO: Fix this for resolution change
        int width = (currentProgress * (620 / 100)) + 20; // Off by 20 pixels, but it's not really a huge issue
        Rectangle2D.Double loadingBar = new Rectangle2D.Double(373, 618, width, 39);

        g2.setColor(Color.GREEN);
        g2.fill(loadingBar);

        g.dispose();
    }

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
				timeDelta -= frameTimePacing;
		    }
		}
    }
}

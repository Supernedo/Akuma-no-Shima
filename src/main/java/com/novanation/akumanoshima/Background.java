package com.novanation.akumanoshima;


import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Background {
	
  	private Image bgImage;
	private ImageIcon animImage;

  	private int bgImageWidth;      		// width of the background (>= panel Width)

	private GamePanel panel;

 	private int bgX;			// X-coordinate of "actual" position

	private int bg1X;			// X-coordinate of first background
	private int bg2X;			// X-coordinate of second background
	private int bgDX;			// size of the background move (in pixels)


	public Background(GamePanel panel, String imageFile, int bgDX)
	{
		this.panel = panel;

		if (imageFile.endsWith(".gif"))
			this.animImage = ImageManager.loadGif(imageFile);
		else
			this.bgImage = ImageManager.loadImage(imageFile);

		if (bgImage != null)
			bgImageWidth = bgImage.getWidth(null);	// get width of the background
		else
			bgImageWidth = animImage.getImage().getWidth(null);

		this.bgDX = bgDX;

		bgX = 0;
		bg1X = 0;
		bg2X = bgImageWidth;
	}

	public void move (int direction)
	{
		if (direction == -1)
			moveRight();
		else
		if (direction == 1)
			moveLeft();
	}

	public void moveLeft()
	{
		bgX = bgX - bgDX;

		bg1X = bg1X - bgDX;
		bg2X = bg2X - bgDX;

		if (bg1X < (bgImageWidth * -1))
		{
			bg1X = 0;
			bg2X = bgImageWidth;
		}
	}

	public void moveRight()
	{
		bgX = bgX + bgDX;
					
		bg1X = bg1X + bgDX;
		bg2X = bg2X + bgDX;

		if (bg1X > 0)
		{
			bg1X = bgImageWidth * -1;
			bg2X = 0;
		}
	}

	public void draw (Graphics2D g2, GameWindow window)
	{
		if (bgImage != null)
		{
			g2.drawImage(bgImage, bg1X, 0, bgImageWidth, window.getHeight() - 35, null);
			g2.drawImage(bgImage, bg2X, 0, bgImageWidth, window.getHeight() - 35, null);
		}
		
		if (animImage != null)
		{
			Image newAnim = animImage.getImage();

			g2.drawImage(newAnim, bg1X, 0, window.getWidth(), window.getHeight() - 35, null);
			g2.drawImage(newAnim, bg2X, 0, window.getWidth(), window.getHeight() - 35, null);
		}
	}
}

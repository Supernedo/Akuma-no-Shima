package com.novanation.akumanoshima;


import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageManager {

	public static Image loadImage(String fileName)
	{
		try { return ImageIO.read(ImageManager.class.getResource(fileName)); }
		catch (IOException | IllegalArgumentException e) { return null; }
	}

	public static ImageIcon loadGif(String fileName)
	{
		try {
			return new ImageIcon(ImageManager.class.getResource(fileName));
		} catch (Exception e) {
			return null;
		}
	}
}

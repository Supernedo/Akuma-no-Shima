package com.novanation.akumanoshima;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class SoundManager {

    private HashMap<String, Clip> musicClips = new HashMap<>();
    private HashMap<String, Clip> sfxClips = new HashMap<>();
    private HashMap<String, Clip> ambientClips = new HashMap<>();
	private final Clip menuClip;

	private static SoundManager instance = null;	// keeps track of Singleton instance

	private SoundManager ()
	{	
		// Menu Setup
		Clip clip = loadClip("audio/music/menu_ego_super_slowed.wav");
		menuClip = clip;

		// Music Setup
        clip = loadClip("audio/music/boss_fight_1_empire_slowed.wav");
		musicClips.put("end_1", clip);

		clip = loadClip("audio/music/boss_fight_2_aura2.wav");
		musicClips.put("end_2", clip);

		clip = loadClip("audio/music/end_scene.wav");
		musicClips.put("end_3", clip);

		clip = loadClip("audio/music/retro_music_loopable.wav");
		musicClips.put("retro_loop", clip);

		clip = loadClip("audio/music/battle_music_loopable.wav");
		musicClips.put("battle_loop", clip);

		// Ambient Setup
		clip = loadClip("audio/ambient/blizzard_ambient.wav");
		ambientClips.put("blizzard", clip);

		clip = loadClip("audio/ambient/forest_ambient.wav");
		ambientClips.put("forest", clip);

		clip = loadClip("audio/ambient/lava_ambient.wav");
		ambientClips.put("lava_1", clip);

		clip = loadClip("audio/ambient/volcanic_ambient.wav");
		ambientClips.put("lava_2", clip);

		// Sound Effect Setup
		clip = loadClip("audio/sfx/explosion.wav");
		sfxClips.put("explosion", clip);

		clip = loadClip("audio/sfx/fireball_charge.wav");
		sfxClips.put("fireball_charge", clip);

		clip = loadClip("audio/sfx/fireball_fizzle_or_hit.wav");
		sfxClips.put("fireball_hit", clip);

		clip = loadClip("audio/sfx/fireball_launch.wav");
		sfxClips.put("fireball_launch", clip);

		clip = loadClip("audio/sfx/game_win.wav");
		sfxClips.put("game_win", clip);

		clip = loadClip("audio/sfx/hellhound_bite.wav");
		sfxClips.put("hellhound_bite", clip);

		clip = loadClip("audio/sfx/hellhound_growl.wav");
		sfxClips.put("hellhound_growl", clip);

		clip = loadClip("audio/sfx/ice_skate.wav");
		sfxClips.put("ice_skate", clip);

		clip = loadClip("audio/sfx/maou_step.wav");
		sfxClips.put("maou_step", clip);

		clip = loadClip("audio/sfx/perk_equip.wav");
		sfxClips.put("perk_equip", clip);

		clip = loadClip("audio/sfx/player_death.wav");
		sfxClips.put("player_death", clip);

		clip = loadClip("audio/sfx/player_hit.wav");
		sfxClips.put("player_hit", clip);

		clip = loadClip("audio/sfx/player_jump.wav");
		sfxClips.put("player_jump", clip);

		clip = loadClip("audio/sfx/player_step.wav");
		sfxClips.put("player_step", clip);

		clip = loadClip("audio/sfx/round_change.wav");
		sfxClips.put("round_change", clip);

		clip = loadClip("audio/sfx/shop_purchase.wav");
		sfxClips.put("shop_purchase", clip);

		clip = loadClip("audio/sfx/water_splash.wav");
		sfxClips.put("water_splash", clip);

		clip = loadClip("audio/sfx/ar_weapon_reload.wav");
		sfxClips.put("weapon_reload", clip);

		clip = loadClip("audio/sfx/ar_weapon_shoot.wav");
		sfxClips.put("weapon_shoot", clip);

		clip = loadClip("audio/sfx/weapon_hit.wav");
		sfxClips.put("weapon_hit", clip);
	}

	// TODO: Add SFX
	public void setSFXVolume(float volume) { }

	public void setMenuVolume(float volume)
	{
		FloatControl volumeControl = (FloatControl) menuClip.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(volume);
	}

	public void setMusicVolume(float volume)
	{
		for (Clip clip : musicClips.values())
		{
			FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        	volumeControl.setValue(volume);
		}
	}

	public void setMasterVolume(float volume)
	{
		FloatControl volumeControl = (FloatControl) menuClip.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(volume);

		for (Clip clip : musicClips.values())
		{
			volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        	volumeControl.setValue(volume);
		}

		// TODO: Add SFX
	}

	// Class method to retrieve instance of Singleton
	public static SoundManager getInstance()
	{	
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}

	// Gets clip from the specified file
    public Clip loadClip (String fileName) 
	{	
 		Clip clip = null;

		try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName))
		{
			if (is == null)
			{
				System.out.println("Audio file not found: " + fileName);
				return null;
			}

			BufferedInputStream bufferedIn = new BufferedInputStream(is);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
			
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
			System.out.println("Error opening sound file: " + e);
		}

		return clip;
    }

	public Clip getClip (String title, ClipType clipType)
	{
		switch (clipType) {
			case MENU -> { return menuClip; }
			case MUSIC -> {  return musicClips.get(title); }
			case AMBIENT -> { return ambientClips.get(title); }
			case SFX -> { return sfxClips.get(title); }
			default -> { return null; }
		}
	}

    public void playClip(String title, ClipType clipType, boolean looping)
	{    
		Clip clip = getClip(title, clipType);

		if (clip != null) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
    }

    public void stopClip(String title, ClipType clipType)
	{
		Clip clip = getClip(title, clipType);

		if (clip != null)
			clip.stop();
    }
}

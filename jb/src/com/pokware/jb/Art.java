package com.pokware.jb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Art {

	private static final int TILESIZE = 32;
		
	public static Sound coinSound;
	public static Sound hurtSound;
	
	// Jack
	public static Animation walkingRightAnimation;
	public static Animation walkingLeftAnimation;
	public static Animation climbingAnimation;
	
	// Zombie
	public static Animation zombieWalkingRightAnimation;
	public static Animation zombieWalkingLeftAnimation;
	
	// Spider
	public static Animation spiderAnimation;
	
	// Bonus
	public static Animation blueJewelAnimation;
	public static Animation bigBlueJewelAnimation;
			
	public static void load(TextureAtlas atlas) {
		// Jack
		TextureRegion[] walkingTextures = atlas.findRegion("walk").split(TILESIZE, TILESIZE)[0];		
		walkingRightAnimation = new Animation(0.1f, walkingTextures);
		
		TextureRegion[] flippedWalkingTextures = atlas.findRegion("walk").split(TILESIZE, TILESIZE)[0];
		for (TextureRegion textureRegion : flippedWalkingTextures) {
			textureRegion.flip(true, false);
		}		
		walkingLeftAnimation = new Animation(0.1f, flippedWalkingTextures);
		
		TextureRegion[] climbTextures = atlas.findRegion("Climb").split(TILESIZE, TILESIZE)[0];
		climbingAnimation = new Animation(0.1f, climbTextures);
		
		// Zombie
		TextureRegion[] zombieWalkingTextures = atlas.findRegion("zombie").split(TILESIZE, TILESIZE)[0];		
		zombieWalkingRightAnimation = new Animation(0.2f, zombieWalkingTextures);
		
		TextureRegion[] zombieFlippedWalkingTextures = atlas.findRegion("zombie").split(TILESIZE, TILESIZE)[0];
		for (TextureRegion textureRegion : zombieFlippedWalkingTextures) {
			textureRegion.flip(true, false);
		}		
		zombieWalkingLeftAnimation = new Animation(0.2f, zombieFlippedWalkingTextures);
		
		TextureRegion[] spiderTextures = atlas.findRegion("spider0").split(TILESIZE, 16)[0];
		System.out.println(spiderTextures.length);
		spiderAnimation = new Animation(0.2f, spiderTextures);
				
		// Goodies
		TextureRegion[] blueJewelTextures = atlas.findRegion("crystal-qubodup-ccby3-16-blue").split(16, 16)[0];		
		blueJewelAnimation = new Animation(0.1f, blueJewelTextures);
		TextureRegion[] bigBlueJewelTextures = atlas.findRegion("crystal-qubodup-ccby3-32-blue").split(32, 32)[0];		
		bigBlueJewelAnimation = new Animation(0.1f, bigBlueJewelTextures);
		
		coinSound = Gdx.audio.newSound(Gdx.files.getFileHandle("data/output/sound/coin.wav", FileType.Internal));
		hurtSound = Gdx.audio.newSound(Gdx.files.getFileHandle("data/output/sound/hurt.wav", FileType.Internal));
	}
	
	
}
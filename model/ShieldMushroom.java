package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.Sound;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ShieldMushroom extends Mushroom {
	public static final int SPAWN_SHIELD_FLOATER = Game.FRAMES_PER_SECOND * 25;
	public enum ImageState {MUSHROOM}
	private ImageState imageState;


	public ShieldMushroom() {
		setExpiry(260);
		Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
		imageState = ImageState.MUSHROOM;
		rasterMap.put(imageState, loadGraphic("/imgs/Mushroom/mushroom_blue.png") );
		setRasterMap(rasterMap);
	}

	@Override
	public void remove(LinkedList<Movable> list) {
		super.remove(list);
		//if getExpiry() > 0, then this remove was the result of a collision, rather than natural mortality
		if (getExpiry() > 0) {
			Sound.playSound("shieldup.wav");
		    CommandCenter.getInstance().getSquirrel().setShield(Squirrel.MAX_SHIELD);
	   }

	}


	@Override
	public void draw(Graphics g) {
		renderRaster((Graphics2D) g, getRasterMap().get(imageState));
	}


}

package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.Sound;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class RasenganMushroom extends Mushroom {

	public static final int SPAWN_NUKE_FLOATER = Game.FRAMES_PER_SECOND * 40;
	public enum ImageState {MUSHROOM}
	private ImageState imageState;
	public RasenganMushroom() {

		setExpiry(150);
		Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
		imageState = ImageState.MUSHROOM;
		rasterMap.put(imageState, loadGraphic("/imgs/Mushroom/mushroom_red.png") );
		setRasterMap(rasterMap);
	}

	@Override
	public void remove(LinkedList<Movable> list) {
		super.remove(list);
		//if getExpiry() > 0, then this remove was the result of a collision, rather than natural mortality
		if (getExpiry() > 0) {
			Sound.playSound("nuke-up.wav");
			CommandCenter.getInstance().getSquirrel().setRasenganMeter(Squirrel.MAX_RASENGAN);
		}

	}

	@Override
	public void draw(Graphics g) {
		renderRaster((Graphics2D) g, getRasterMap().get(imageState));
	}

}

package edu.uchicago.gerber._08final.mvc.model;


import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.awt.*;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.GameOp;
import edu.uchicago.gerber._08final.mvc.controller.Sound;


public class Slime extends Sprite {

	private final int radius = 50;
	public enum ImageState{SLIME}
	private ImageState imageState;

	public Slime(){

		setRadius(radius);

		//Asteroid is FOE
		setTeam(Team.FOE);
		setColor(Color.WHITE);

		//the spin will be either plus or minus 0-9
		setSpin(somePosNegValue(10));
		//random delta-x
		setDeltaX(somePosNegValue(10));
		//random delta-y
		setDeltaY(somePosNegValue(10));

		Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
		imageState = ImageState.SLIME;
		rasterMap.put(imageState, loadGraphic("/imgs/Slime/Slime.png") );
		setRasterMap(rasterMap);

	}


	@Override
	public void move(){
		if (getCenter().x > Game.DIM.width || getCenter().x < 0) {
			setDeltaX(-getDeltaX());
		}
		if (getCenter().y > Game.DIM.height || getCenter().y < 0) {
			setDeltaY(-getDeltaY());
		}

		//the slimes will chase the squirrel depends on their relative positions
		if (getCenter().x > CommandCenter.getInstance().getSquirrel().getCenter().x) {
			setDeltaX(-2);
		}
		if (getCenter().x < CommandCenter.getInstance().getSquirrel().getCenter().x) {
			setDeltaX(2);
		}
		if (getCenter().y > CommandCenter.getInstance().getSquirrel().getCenter().y) {
			setDeltaY(-2);
		}
		if (getCenter().y < CommandCenter.getInstance().getSquirrel().getCenter().y) {
			setDeltaY(2);
		}

		double newXPos = getCenter().x + getDeltaX();
		double newYPos = getCenter().y + getDeltaY();
		setCenter(new Point((int) newXPos, (int) newYPos));

		if (getExpiry() > 0) expire();
		if (getSpin() != 0) setOrientation(getOrientation() + getSpin());
	}


	@Override
	public void draw(Graphics g) {
		renderRaster((Graphics2D) g, getRasterMap().get(imageState));
	}

	@Override
	public void remove(LinkedList<Movable> list) {
		super.remove(list);
		CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 100L);
		Sound.playSound("kapow.wav");
	}

}

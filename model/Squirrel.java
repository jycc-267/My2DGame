package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.Sound;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

@Data
public class Squirrel extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================

	//static fields

	//number of degrees the squirrel will turn at each animation cycle if the turnState is LEFT or RIGHT
	public final static int TURN_STEP = 90;

	//number of frames that the squirrel will be protected after a spawn
	public static final int INITIAL_SPAWN_TIME = 46;
	//number of frames squirrel will be protected after consuming a NewShieldFloater
	public static final int MAX_SHIELD = 200;

	public static final int MAX_RASENGAN = 600;
	public static final int MIN_RADIUS = 28;
	public boolean collisionOn = false;





	//image states
	public enum ImageState {UP, DOWN, LEFT, RIGHT}
	private ImageState imageState;

	public enum Weapon {ACORN, SHURIKEN}
	private Weapon weapon = Weapon.ACORN;
	private boolean change = false;
	//instance fields (getters/setters provided by Lombok @Data above)
	private int shield; // the duration of the shield

	private int rasenganMeter;

	//showLevel is not germane to the Squirrel. Rather, it controls whether the level is shown in the middle of the
	// screen. However, given that the Squirrel reference is never null, and that a Squirrel is a Movable whose move/draw
	// methods are being called every ~40ms, this is a very convenient place to store this variable.
	private int showLevel;

	private boolean moving; // set to true whenever an arrow button is pressed

	//enum used for turnState field
	public enum TurnState {UP, DOWN, LEFT, RIGHT, IDLE}
	private TurnState turnState = TurnState.UP;


	// ==============================================================
	// CONSTRUCTOR
	// ==============================================================
	
	public Squirrel() {

		setTeam(Team.FRIEND);
		setRadius(MIN_RADIUS);

		//We use HashMap which has a seek-time of O(1)
		//See the resources directory in the root of this project for pngs.
		//Using enums as keys is safer b/c we know the value exists when we reference the consts later in code.
    	Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
		rasterMap.put(ImageState.UP, loadGraphic("/imgs/Squirrel/SquirrelBrownNorth2.png") ); //normal ship
		rasterMap.put(ImageState.DOWN, loadGraphic("/imgs/Squirrel/SquirrelBrownSouth2.png") ); //normal ship thrusting
		rasterMap.put(ImageState.LEFT, loadGraphic("/imgs/Squirrel/SquirrelBrownWest2.png") ); //protected ship (green)
		rasterMap.put(ImageState.RIGHT, loadGraphic("/imgs/Squirrel/SquirrelBrownEast2.png") ); //green thrusting

		setRasterMap(rasterMap);


	}


	// ==============================================================
	// METHODS 
	// ==============================================================

	public void switchWeapon(){
		if (change) {
			setWeapon(Weapon.SHURIKEN);
		}
		else setWeapon(Weapon.ACORN);
		change = !change;
	}

	@Override
	public void move() {
		super.move();

		if (shield > 0) shield--;
		if (rasenganMeter > 0) rasenganMeter--;
		//The squirrel is a convenient place to decrement the showLevel variable as the squirrel
		//move() method is being called every frame (~40ms); and the squirrel reference is never null.
		if (showLevel > 0) showLevel--;

		final double speed = 15;

		if (moving) {
			// move only if the squirrel has not collided with traps
			if (isCollisionOn() == false){
				setDeltaX(Math.cos(Math.toRadians(getOrientation())) * speed);
				setDeltaY(Math.sin(Math.toRadians(getOrientation())) * speed);
			}
		}
		else{
			// stop when the player released an arrow button
			setDeltaX(0);
			setDeltaY(0);
		}

		//adjust the orientation given the turnState (determined by which arrow buttons the player has pressed)
		int adjustOr = getOrientation();
		switch (turnState) {
			case UP:
				adjustOr = 270;
				break;
			case DOWN:
				adjustOr = 90;
				break;
			case LEFT:
				adjustOr = 180;
				break;
			case RIGHT:
				adjustOr = 0;
				break;
			case IDLE:
			default:
				//do nothing
		}
		setOrientation(adjustOr);

	}

	// draw the squirrel depends on its turnstate
	@Override
	public void draw(Graphics g) {

		if (shield > 0){drawShield(g);}
		switch (turnState) {
			case UP:
				imageState = ImageState.UP;
				break;
			case DOWN:
				imageState = ImageState.DOWN;
				break;
			case LEFT:
				imageState = ImageState.LEFT;
				break;
			case RIGHT:
				imageState = ImageState.RIGHT;
				break;
			case IDLE:
			default:
				//do nothing
		}
		//down-cast (widen the aperture of) the graphics object to gain access to methods of Graphics2D
		//and render the raster image according to the image-state
		renderRaster((Graphics2D) g, getRasterMap().get(imageState));

	}

	private void drawShield(Graphics g){
		g.setColor(Color.CYAN);
		g.drawOval(getCenter().x - getRadius(), getCenter().y - getRadius(), getRadius() *2, getRadius() *2);
	}

	@Override
	public void remove(LinkedList<Movable> list) {
		//The squirrel is never actually removed from the game-space; instead we decrement numSquirrels
		//only execute the decrementSquirrelNumAndSpawn() method if shield is down.
		if (shield == 0)  decrementSquirrelNumAndSpawn();
	}


	public void decrementSquirrelNumAndSpawn(){

		CommandCenter.getInstance().setNumSquirrels(CommandCenter.getInstance().getNumSquirrels() -1);
		if (CommandCenter.getInstance().isGameOver()) return;
		Sound.playSound("mouse-laugh.wav");
		setShield(Squirrel.INITIAL_SPAWN_TIME);
		//put squirrel in the middle of the game-space
		setCenter(new Point(Game.DIM.width / 2, Game.DIM.height / 2));
		//random number between 0-360 in steps of TURN_STEP
		setOrientation(Game.R.nextInt(360 / Squirrel.TURN_STEP) * Squirrel.TURN_STEP);
		setDeltaX(0);
		setDeltaY(0);
		setRadius(Squirrel.MIN_RADIUS);
		setRasenganMeter(0);
		setCollisionOn(false); // set the collision status back to false when a squirrel is dead
	}

}

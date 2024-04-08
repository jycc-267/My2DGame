package edu.uchicago.gerber._08final.mvc.controller;



import edu.uchicago.gerber._08final.mvc.model.*;
import lombok.Data;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

//The CommandCenter is a singleton that manages the state of the game.
//the lombok @Data gives us automatic getters and setters on all members
@Data
public class CommandCenter {

	private  int numSquirrels;
	private  int level;
	private  long score;
	private  boolean paused;
	private  boolean muted;

	//this value is used to count the number of frames (full animation cycles) in the game
	private long frame;

	//the squirrel is located in the movFriends list, but since we use this reference a lot, we keep track of it in a
	//separate reference. Use final to ensure that the squirrel ref always points to the single squirrel object on heap.
	//Lombok will not provide setter methods on final members
	private final Squirrel squirrel = new Squirrel();

	//lists containing our movables subdivided by team
	private final LinkedList<Movable> movDebris = new LinkedList<>();
	private final LinkedList<Movable> movFriends = new LinkedList<>();
	private final LinkedList<Movable> movFoes = new LinkedList<>();
	private final LinkedList<Movable> movMushrooms = new LinkedList<>();
	private final LinkedList<Movable> movTraps = new LinkedList<>();

	private final GameOpsQueue opsQueue = new GameOpsQueue();

	//for sound playing. Limit the number of threads to 5 at a time.
	private final ThreadPoolExecutor soundExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);


	// The CommandCenter class maintains game state - make this a singleton.
	// Singleton: Constructor made private
	private static CommandCenter instance = null;
	private CommandCenter() {}
	public static CommandCenter getInstance(){
		if (instance == null){
			instance = new CommandCenter();
		}
		return instance;
	}


	public void initGame(){
		clearAll();
		setLevel(0);
		setScore(0);
		setPaused(false);
		//set to one greater than number of squirrels lives in your game as initFalconAndDecrementNum() also decrements
		setNumSquirrels(4);
		squirrel.decrementSquirrelNumAndSpawn();
		//add the squirrel to the movFriends list
		opsQueue.enqueue(squirrel, GameOp.Action.ADD);

	}



	public void incrementFrame(){
		//use of ternary expression to simplify the logic to one line
		frame = frame < Long.MAX_VALUE ? frame + 1 : 0;
	}

	private void clearAll(){
		movDebris.clear();
		movFriends.clear();
		movFoes.clear();
		movMushrooms.clear();
		movTraps.clear();
	}

	public boolean isGameOver() {
		//if the number of squirrels is zero, then game over
		return numSquirrels < 1;
	}






}

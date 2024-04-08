package edu.uchicago.gerber._08final.mvc.controller;

import edu.uchicago.gerber._08final.mvc.model.*;
import edu.uchicago.gerber._08final.mvc.view.GamePanel;


import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;


// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

    // ===============================================
    // FIELDS
    // ===============================================

    public static final Dimension DIM = new Dimension(1200, 900); //the dimension of gamePanel.
    private final GamePanel gamePanel;
    //this is used throughout many classes.
    public static final Random R = new Random();
    public final static int ANIMATION_DELAY = 30; // milliseconds between frames
    public final static int FRAMES_PER_SECOND = 1000 / ANIMATION_DELAY;
    private final Thread animationThread;


    //key-codes
    private static final int
            PAUSE = KeyEvent.VK_P, // p key
            QUIT = KeyEvent.VK_Q, // q key
            LEFT = KeyEvent.VK_LEFT, // move left; left arrow
            RIGHT = KeyEvent.VK_RIGHT, // move right; right arrow
            UP = KeyEvent.VK_UP, // move up; up arrow
            DOWN = KeyEvent.VK_DOWN, // move down; down arrow
            START = KeyEvent.VK_S, // start the game; s key
            FIRE = KeyEvent.VK_SPACE, // shoot a weapon; space key
            MUTE = KeyEvent.VK_M, // play or mute the background music; m-key mute
            RASENGAN = KeyEvent.VK_R, // release a rasengan; r-key
            SWITCH = KeyEvent.VK_SHIFT; // switch a weapon; shift key



    private final Clip soundMove;
    private final Clip soundBackground;


    // ===============================================
    // ==CONSTRUCTOR
    // ===============================================

    public Game() {
        gamePanel = new GamePanel(DIM);
        gamePanel.addKeyListener(this); //Game object implements KeyListener
        soundMove = Sound.clipForLoopFactory("whitenoise.wav");
        soundBackground = Sound.clipForLoopFactory("music-background.wav");

        /**
         * Create a Runnable implementer and implement the run() method.
         * Instantiate the Thread class and pass the implementer to the Thread, Thread has a constructor which accepts Runnable instances.
         * Invoke start() of Thread instance, start internally calls run() of the implementer.
         * Invoking start() creates a new Thread that executes the code written in run().
         */

        //fire up the animation thread
        animationThread = new Thread(this); // pass the animation thread a runnable object, the Game object
        animationThread.setDaemon(true);
        animationThread.start();
    }

    // ===============================================
    // ==METHODS
    // ===============================================

    public static void main(String[] args) {
        //typical Swing application start; we pass EventQueue a Runnable object.
        EventQueue.invokeLater(Game::new);
    }

    // Game implements runnable, and must have run method
    @Override
    public void run() {

        // lower animation thread's priority, thereby yielding to the 'Event Dispatch Thread' or EDT
        // thread which listens to keystrokes
        animationThread.setPriority(Thread.MIN_PRIORITY);

        // and get the current time
        long startTime = System.currentTimeMillis();

        // this thread animates the scene
        while (Thread.currentThread() == animationThread) {


            //this call will cause all movables to move() and draw() themselves every ~40ms
            // see GamePanel class for details
            //UNDERSTAND THIS
            gamePanel.update(gamePanel.getGraphics());

            checkCollisions();
            checkNewLevel();
            checkFloaters();

            //keep track of the frame for development purposes
            CommandCenter.getInstance().incrementFrame();

            // surround the sleep() in a try/catch block
            // this simply controls delay time between
            // the frames of the animation
            try {
                // The total amount of time is guaranteed to be at least ANIMATION_DELAY long.  If processing (update)
                // between frames takes longer than ANIMATION_DELAY, then the difference between startTime -
                // System.currentTimeMillis() will be negative, then zero will be the sleep time
                startTime += ANIMATION_DELAY;

                Thread.sleep(Math.max(0,
                        startTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // do nothing (bury the exception), and just continue, e.g. skip this frame -- no big deal
            }
        } // end while
    } // end run

    private void checkFloaters() {
        spawnShieldFloater();
        spawnNukeFloater();
        if (CommandCenter.getInstance().getLevel() > 1) spawnTrapFloater();
    }


    private void checkCollisions() {

        //This has order-of-growth of O(FOES * FRIENDS)
        Point pntFriendCenter, pntFoeCenter;
        int radFriend, radFoe;
        for (Movable movFriend : CommandCenter.getInstance().getMovFriends()) {
            for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {

                pntFriendCenter = movFriend.getCenter();
                pntFoeCenter = movFoe.getCenter();
                radFriend = movFriend.getRadius();
                radFoe = movFoe.getRadius();

                //detect circle collision
                if (pntFriendCenter.distance(pntFoeCenter) < (radFriend + radFoe)) {
                    //if the movFriend is a Shuriken, it will not disappear after colliding with a movFoe. (Penetrate the enemy)
                    if (movFriend instanceof Shuriken) CommandCenter.getInstance().getOpsQueue().enqueue(movFoe, GameOp.Action.REMOVE);

                    else{
                        //remove the friend from its list
                        CommandCenter.getInstance().getOpsQueue().enqueue(movFriend, GameOp.Action.REMOVE);
                        //remove the foe its list
                        CommandCenter.getInstance().getOpsQueue().enqueue(movFoe, GameOp.Action.REMOVE);
                    }
                }
            }//end inner for
        }//end outer for

        //check for collisions between Squirrel and mushrooms.
        Point pntSqlCenter = CommandCenter.getInstance().getSquirrel().getCenter();
        int radFalcon = CommandCenter.getInstance().getSquirrel().getRadius();

        Point pntMushroomCenter;
        int radFloater;
        for (Movable movMushroom : CommandCenter.getInstance().getMovMushrooms()) {
            pntMushroomCenter = movMushroom.getCenter();
            radFloater = movMushroom.getRadius();
            //detect circle collision
            if (pntSqlCenter.distance(pntMushroomCenter) < (radFalcon + radFloater)) {
                //enqueue the floater
                CommandCenter.getInstance().getOpsQueue().enqueue(movMushroom, GameOp.Action.REMOVE);
            }
        }

        Point pntTrapCenter;
        int radTrap;
        for (Movable movTrap : CommandCenter.getInstance().getMovTraps()) {
            pntTrapCenter = movTrap.getCenter();
            radTrap = movTrap.getRadius();
            //detect circle collision
            if (pntSqlCenter.distance(pntTrapCenter) < (radFalcon + radTrap)){
                //if the squirrel and a trap collide, update the collision status of the squirrel to true (the squirrel cannot move either until the next level or when it revives)
                CommandCenter.getInstance().getSquirrel().setCollisionOn(true);
                //Sound.playSound("");
            }
        }

        processGameOpsQueue(); // deferred mutation

    }//end meth


    // This method adds and removes movables to/from their respective linked-lists.
    private void processGameOpsQueue() {

        // deferred mutation:
        // these operations are done AFTER we have completed our collision detection
        // to avoid mutating the movable linkedlists while iterating them above.
        while (!CommandCenter.getInstance().getOpsQueue().isEmpty()) {

            GameOp gameOp = CommandCenter.getInstance().getOpsQueue().dequeue();

            //given team, determine this object will be added-to or removed-from which linked-list
            LinkedList<Movable> list;
            Movable mov = gameOp.getMovable();
            switch (mov.getTeam()) {
                case FOE:
                    list = CommandCenter.getInstance().getMovFoes();
                    break;
                case FRIEND:
                    list = CommandCenter.getInstance().getMovFriends();
                    break;
                case MUSHROOM:
                    list = CommandCenter.getInstance().getMovMushrooms();
                    break;
                case TRAP:
                    list = CommandCenter.getInstance().getMovTraps();
                    break;
                case DEBRIS:
                default:
                    list = CommandCenter.getInstance().getMovDebris();
            }

            //pass the appropriate linked-list from above
            //this block will execute the add() or remove() callbacks in the Movable models.
            GameOp.Action action = gameOp.getAction();
            if (action == GameOp.Action.ADD)
                mov.add(list);
            else //REMOVE
                mov.remove(list); // implement by each movable class

        }//end while
    }


    private void spawnShieldFloater() {

        if (CommandCenter.getInstance().getFrame() % ShieldMushroom.SPAWN_SHIELD_FLOATER == 0) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new ShieldMushroom(), GameOp.Action.ADD);
        }
    }

    private void spawnTrapFloater() {

        if (CommandCenter.getInstance().getFrame() % TrapMushroom.SPAWN_TRAP_FLOATER == 0) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new TrapMushroom(), GameOp.Action.ADD);
        }
    }

    private void spawnNukeFloater() {

        if (CommandCenter.getInstance().getFrame() % RasenganMushroom.SPAWN_NUKE_FLOATER == 0) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new RasenganMushroom(), GameOp.Action.ADD);
        }
    }


    //this method spawns new slime

    private void spawnSlime(int num) {
        if (CommandCenter.getInstance().getLevel() == 1) {
            //For level 1, spawn 3 slimes
            for (int i = 0; i < 3; i++) {
                CommandCenter.getInstance().getOpsQueue().enqueue(new Slime(), GameOp.Action.ADD);
            }
        }
        else {
            //For other levels, spawn the number of slimes at that level
            while (num-- > 0) {
                CommandCenter.getInstance().getOpsQueue().enqueue(new Slime(), GameOp.Action.ADD);

            }
        }
    }

    private void spawnFox(int num){
        // spawn a boss fox if the level is not a multiple of 3
        if (CommandCenter.getInstance().getLevel() % 3 == 0) {
            // spawn "n" boss foxes, where "n" is the quotient
            for (int i = 0; i < (CommandCenter.getInstance().getLevel() / 3); i++) {
                CommandCenter.getInstance().getOpsQueue().enqueue(new Fox(1, 5), GameOp.Action.ADD);
            }
        }
        else{
            // spawn a normal fox if the level is not a multiple of 3
            while (num-- > 0){
                CommandCenter.getInstance().getOpsQueue().enqueue(new Fox(1, 2), GameOp.Action.ADD);
            }
        }
    }

    private void spawnBear(int num){
        while (num-- > 0){
            CommandCenter.getInstance().getOpsQueue().enqueue(new Bear(0), GameOp.Action.ADD);
        }
    }

    private boolean isLevelClear() {
        //if there are no more foes on the screen, return true
        boolean asteroidFree = true;
        boolean BearFree = true;
        boolean FoxFree = true;
        for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {
            if (movFoe instanceof Slime) {
                asteroidFree = false;
                break;
            } else if (movFoe instanceof Bear) {
                BearFree = false;
            } else if (movFoe instanceof  Fox) {
                FoxFree = false;
            }
        }
        return (asteroidFree && BearFree && FoxFree);
    }

    private void checkNewLevel() {

        if (isLevelClear()) {
            //currentLevel will be zero at beginning of game
            int level = CommandCenter.getInstance().getLevel();
            //award some points for having cleared the previous level
            CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + (10_000L * level));
            //bump the level up
            level = level + 1;
            CommandCenter.getInstance().setLevel(level);
            CommandCenter.getInstance().getSquirrel().setCollisionOn(false); // reset the collision state of the squirrel back to false
            // do not let slime and bear appear simultaneously with boss foxes
            if (level % 3 != 0) spawnSlime(level);
            if (level > 1 && (level % 3) != 0) spawnBear(level); // bears appear only when the level is larger than 2
            if (level > 1) spawnFox(level); // foxes appear only when the level is larger than 2
            //make squirrel invincible momentarily in case new asteroids spawn on top of him, and give player
            //time to adjust to new foes in game space.
            CommandCenter.getInstance().getSquirrel().setShield(Squirrel.INITIAL_SPAWN_TIME);
            //show "Level X" in middle of screen
            CommandCenter.getInstance().getSquirrel().setShowLevel(Squirrel.INITIAL_SPAWN_TIME);

        }
    }


    // Varargs for stopping looping-music-clips
    private static void stopLoopingSounds(Clip... clpClips) {
        Arrays.stream(clpClips).forEach(clip -> clip.stop());
    }

    // ===============================================
    // KEYLISTENER METHODS
    // ===============================================

    @Override
    public void keyPressed(KeyEvent e) {
        Squirrel squirrel = CommandCenter.getInstance().getSquirrel();
        int keyCode = e.getKeyCode();

        if (keyCode == START && CommandCenter.getInstance().isGameOver()) {
            CommandCenter.getInstance().initGame();
            return;
        }


        switch (keyCode) {
            case PAUSE:
                CommandCenter.getInstance().setPaused(!CommandCenter.getInstance().isPaused());
                if (CommandCenter.getInstance().isPaused()) stopLoopingSounds(soundBackground, soundMove);
                break;
            case QUIT:
                System.exit(0);
                break;
            // set squirrel's turn state and moving state whenever the arrow buttons are hit
            case UP:
                squirrel.setTurnState(Squirrel.TurnState.UP);
                squirrel.setMoving(true);
                soundMove.loop(Clip.LOOP_CONTINUOUSLY);
                break;
            case DOWN:
                squirrel.setTurnState(Squirrel.TurnState.DOWN);
                squirrel.setMoving(true);
                soundMove.loop(Clip.LOOP_CONTINUOUSLY);
                break;
            case LEFT:
                squirrel.setTurnState(Squirrel.TurnState.LEFT);
                squirrel.setMoving(true);
                soundMove.loop(Clip.LOOP_CONTINUOUSLY);
                break;
            case RIGHT:
                squirrel.setTurnState(Squirrel.TurnState.RIGHT);
                squirrel.setMoving(true);
                soundMove.loop(Clip.LOOP_CONTINUOUSLY);
                break;

            default:
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Squirrel squirrel = CommandCenter.getInstance().getSquirrel();
        int keyCode = e.getKeyCode();
        //show the key-code in the console
        System.out.println(keyCode);

        switch (keyCode) {
            case FIRE:
                // allow the squirrel to shoot different weapon
                if (squirrel.getWeapon() == Squirrel.Weapon.ACORN) CommandCenter.getInstance().getOpsQueue().enqueue(new Acorn(squirrel), GameOp.Action.ADD);
                if (squirrel.getWeapon() == Squirrel.Weapon.SHURIKEN) CommandCenter.getInstance().getOpsQueue().enqueue(new Shuriken(squirrel), GameOp.Action.ADD);
                break;
            case RASENGAN:
                CommandCenter.getInstance().getOpsQueue().enqueue(new Rasengan(squirrel), GameOp.Action.ADD);
                Sound.playSound("rasengan.wav");
                break;
            // stop the squirrel whenever the arrow buttons are released
            case LEFT:
            case RIGHT:
            case UP:
            case DOWN:
                squirrel.setMoving(false);
                soundMove.stop();
                break;

            case MUTE:
                CommandCenter.getInstance().setMuted(!CommandCenter.getInstance().isMuted());

                if (!CommandCenter.getInstance().isMuted()) {
                    stopLoopingSounds(soundBackground);
                } else {
                    soundBackground.loop(Clip.LOOP_CONTINUOUSLY);
                }
                break;

            case SWITCH:
                squirrel.switchWeapon();
            default:
                break;
        }

    }

    @Override
    // does nothing, but we need it b/c of KeyListener contract
    public void keyTyped(KeyEvent e) {
    }

}



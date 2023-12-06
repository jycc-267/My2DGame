package main;

import javax.swing.*;
import java.awt.*;

// serve as the game screen, screen settings are designed here
public class GamePanel extends JPanel implements Runnable{

    final int originalTileSize = 16; // 16x16 tile, the default standard size of the sprites and map tiles in most retro 2D Game
    final int scale = 3; // for scaling the tile to cope with a higher resolution of modern computer screens
    final int tileSize = originalTileSize * scale; // 48x48 tile
    final int screenWidth = 16 * tileSize; // 768 pixels
    final int screenHeight = 12 * tileSize; // 576 pixels

    /** Keeps the program updating animation with a thread until the game is stopped
     * Create a Runnable implementer and implement the run() method.
     * Instantiate the Thread class and pass the implementer to the Thread, Thread has a constructor which accepts Runnable instances.
     * Invoke start() of Thread instance, start automatically calls run() of the implementer.
     * Invoking start() creates a new Thread that executes the code written in run().
     */
    Thread gameThread;
    public void gameStart(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    // This is where the GAME LOOP is designed
    @Override
    public void run(){

        while(Thread.currentThread() == gameThread) {
            
        }

    }

    // constructor
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // set the screen size of this panel
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // improve rendering performance, all the drawing of this component will be done in an off-screen painting buffer


    }

}

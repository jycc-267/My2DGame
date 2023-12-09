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
    final int FPS = 60; // frame per second
    final int ANIMATION_INTERVAL = 1000 / FPS; // milliseconds between frames

    // Set Player's initial position and moving information
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4; // move 4 pixels

    KeyHandler keyHandler = new KeyHandler();

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

    /**
     * This is where the GAME LOOP is designed:
        * UPDATE: renew game information
        * DRAW: draw the screen based on updated information
     * These actions are repeated "n" times per second, where "n" is the FPS
     */
    @Override
    public void run(){

        long nextFrameTime = System.currentTimeMillis();

        while(Thread.currentThread() == gameThread) {

            update();
            repaint();

            try {
                nextFrameTime += ANIMATION_INTERVAL;
                Thread.sleep(Math.max(0, nextFrameTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // do nothing (bury the exception), and just continue, e.g. skip this frame -- no big deal
            }
        }

    }

    // constructor
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // set the screen size of this panel
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // improve rendering performance, all the drawing of this component will be done in an off-screen painting buffer
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

    }

    public void update(){
        playerMove();
    }


    private void playerMove(){
        if (keyHandler.upPressed == true) {
            playerY -= playerSpeed;
        }
        else if (keyHandler.downPressed == true) {
            playerY += playerSpeed;
        }
        else if (keyHandler.leftPressed == true) {
            playerX -= playerSpeed;
        }
        else if (keyHandler.rightPressed == true) {
            playerX += playerSpeed;
        }
    }

    @Override
    public void paintComponent(Graphics g){ // need to ensure the input g is an instance of Graphics2D
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.fillRect(playerX, playerY, tileSize, tileSize);
        g2.dispose(); // dispose of this graphic content and release any system resources that is using

    }

}

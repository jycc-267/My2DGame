package main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyHandler extends KeyAdapter {

    private static final int
            UP = KeyEvent.VK_UP, // move up
            DOWN = KeyEvent.VK_DOWN, // move down
            LEFT = KeyEvent.VK_LEFT, // move left
            RIGHT = KeyEvent.VK_RIGHT; // move right


    public boolean upPressed, downPressed, leftPressed, rightPressed;


    @Override
    public void keyPressed(KeyEvent e) {

        int keyCode = e.getKeyCode();
        switch (keyCode){
            case UP:
                upPressed = true;
                break;
            case DOWN:
                downPressed = true;
                break;
            case LEFT:
                leftPressed = true;
                break;
            case RIGHT:
                rightPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int keyCode = e.getKeyCode();
        switch (keyCode){
            case UP:
                upPressed = false;
                break;
            case DOWN:
                downPressed = false;
                break;
            case LEFT:
                leftPressed = false;
                break;
            case RIGHT:
                rightPressed = false;
                break;
        }
    }
}

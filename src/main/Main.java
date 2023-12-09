package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false); // cannot resize the window
        window.setTitle("My2DJavaGame");
        window.setLocationRelativeTo(null); // not specifying the location of the window, center-defaulted

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack(); // fit the frame into the size and layouts of its subcomponent (GamePanel)

        window.setVisible(true);
        gamePanel.gameStart();

    }


}

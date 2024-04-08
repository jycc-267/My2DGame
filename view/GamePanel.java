package edu.uchicago.gerber._08final.mvc.view;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.Utils;
import edu.uchicago.gerber._08final.mvc.model.*;
import lombok.Data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Data
public class GamePanel extends JPanel {

    // ==============================================================
    // FIELDS
    // ==============================================================
    private final Font fontNormal = new Font("SansSerif", Font.BOLD, 12);
    private final Font fontBig = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
    private FontMetrics fontMetrics;
    private int fontWidth;
    private int fontHeight;

    //used for double-buffering
    private Image imgOff;
    private Graphics grpOff;
    private BufferedImage img;


    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    public GamePanel(Dimension dim) {

        GameFrame gameFrame = new GameFrame();
        gameFrame.getContentPane().add(this);
        gameFrame.pack();
        initFontInfo();
        gameFrame.setSize(dim);

        gameFrame.setTitle("Squirrel In Jungle");
        gameFrame.setResizable(false);
        gameFrame.setVisible(true);
        setFocusable(true);
    }


    // ==============================================================
    // METHODS
    // ==============================================================

    private void drawSquirrelStatus(final Graphics graphics){

        graphics.setColor(Color.white);
        graphics.setFont(fontNormal);

        //draw score always
        graphics.drawString("Score :  " + CommandCenter.getInstance().getScore(), fontWidth, fontHeight);

        //draw the level upper-left corner always
        String levelText = "Level: " + CommandCenter.getInstance().getLevel();
        graphics.drawString(levelText, 20, 30); //upper-left corner

        //build the status string array with possible messages in middle of screen
        List<String> statusArray = new ArrayList<>();
        if (CommandCenter.getInstance().getSquirrel().getShowLevel() > 0) statusArray.add(levelText);
        if (CommandCenter.getInstance().getSquirrel().getRasenganMeter() > 0) statusArray.add("PRESS N for NUKE");

        //draw the statusArray strings to middle of screen
        if (statusArray.size() > 0)
            displayTextOnScreen(graphics, statusArray.toArray(new String[0]));

    }

    //this is used for development, you can remove it from your final game
    private void drawNumFrame(Graphics g) {
        g.setColor(Color.white);
        g.setFont(fontNormal);
        g.drawString("FRAME :  " + CommandCenter.getInstance().getFrame(), fontWidth,
                Game.DIM.height  - (fontHeight + 22));

    }

    private void drawMeters(Graphics g){

        //will be a number between 0-100 inclusive
        int shieldValue =   CommandCenter.getInstance().getSquirrel().getShield() / 2;
        drawOneMeter(g, Color.CYAN, 1, shieldValue);
    }

    private void drawOneMeter(Graphics g, Color color, int offSet, int percent) {

        int xVal = Game.DIM.width - (100 + 120 * offSet);
        int yVal = Game.DIM.height - 45;

        //draw meter
        g.setColor(color);
        g.fillRect(xVal, yVal, percent, 10);

        //draw gray box
        g.setColor(Color.DARK_GRAY);
        g.drawRect(xVal, yVal, 100, 10);
    }

    @Override
    public void update(Graphics g) {

        // The following "off" vars are used for the off-screen double-buffered image.
        imgOff = createImage(Game.DIM.width, Game.DIM.height);
        //get its graphics context
        grpOff = imgOff.getGraphics();

        //this is used for development, you may remove drawNumFrame() in your final game.
        drawNumFrame(grpOff);

        if (CommandCenter.getInstance().isGameOver()) {
            // add background image
            try {
                img = ImageIO.read(new File("src/main/resources/imgs/forest/forest.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            grpOff.drawImage(img, 0, 0, Game.DIM.width, Game.DIM.height, null);

            displayTextOnScreen(grpOff,
                    "GAME OVER",
                    "use the arrow keys to move",
                    "use the space bar to fire",
                    "'S' to Start",
                    "'P' to Pause",
                    "'Q' to Quit",
                    "'M' to toggle music"

            );
        } else if (CommandCenter.getInstance().isPaused()) {

            grpOff.setColor(Color.BLACK);
            grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height); // Fill the off-screen image background with black when the game is paused
            grpOff.setColor(Color.WHITE);
            displayTextOnScreen(grpOff, "Game Paused");

        }

        //playing and not paused!
        else {
            // change background image everytime when the level is advanced
            try {
                if (CommandCenter.getInstance().getLevel() % 2 == 1) img = ImageIO.read(new File("src/main/resources/imgs/forest/forest.png"));
                if (CommandCenter.getInstance().getLevel() % 2 == 0) img = ImageIO.read(new File("src/main/resources/imgs/forest/forest2.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            grpOff.drawImage(img, 0, 0, Game.DIM.width, Game.DIM.height, null);

            moveDrawMovables(grpOff,
                    CommandCenter.getInstance().getMovDebris(),
                    CommandCenter.getInstance().getMovMushrooms(),
                    CommandCenter.getInstance().getMovFoes(),
                    CommandCenter.getInstance().getMovTraps(),
                    CommandCenter.getInstance().getMovFriends());


            drawNumberSquirrelRemaining(grpOff);
            drawMeters(grpOff);
            drawSquirrelStatus(grpOff);

        }

        // after drawing all the movables or text on the offscreen-image, copy it in one fell-swoop to graphics context
        // of the game panel, and show it for ~40ms. If you attempt to draw sprites directly on the gamePanel, e.g.
        // without the use of a double-buffered off-screen image, you will see flickering.
        g.drawImage(imgOff, 0, 0, this);
    }


    //this method causes all sprites to move and draw themselves
    @SafeVarargs
    private final void moveDrawMovables(final Graphics g, List<Movable>... teams) {

        BiConsumer<Movable, Graphics> moveDraw = (mov, grp) -> {
            mov.move();
            mov.draw(grp);
        };

        Arrays.stream(teams) //Stream<List<Movable>>
                //we use flatMap to flatten the teams (List<Movable>[]) passed-in above into a single stream of Movables
                .flatMap(Collection::stream) //Stream<Movable>
                .forEach(m -> moveDraw.accept(m, g));
    }




    // Draw the number of falcons remaining on the bottom-right of the screen.
    private void drawNumberSquirrelRemaining(Graphics g) {
        int numSquirrels = CommandCenter.getInstance().getNumSquirrels();
        while (numSquirrels > 0) {
            drawOneHeart(g, numSquirrels--);
        }
    }

    private void drawOneHeart(Graphics g, int offSet) {

        g.setColor(Color.ORANGE);
        int xVal = Game.DIM.width - (27 * offSet);
        int yVal = Game.DIM.height - 45;

        try {
            img = ImageIO.read(new File("src/main/resources/imgs/heart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(img, xVal, yVal, null);

    }

    private void initFontInfo() {
        Graphics g = getGraphics(); // get the graphics context for the panel
        g.setFont(fontNormal); // take care of some simple font stuff
        fontMetrics = g.getFontMetrics();
        fontWidth = fontMetrics.getMaxAdvance();
        fontHeight = fontMetrics.getHeight();
        g.setFont(fontBig); // set font info
    }


    // This method draws some text to the middle of the screen
    private void displayTextOnScreen(final Graphics graphics, String... lines) {

        //AtomicInteger is safe to pass into a stream
        final AtomicInteger spacer = new AtomicInteger(0);
        Arrays.stream(lines)
                .forEach(str ->
                            graphics.drawString(str, (Game.DIM.width - fontMetrics.stringWidth(str)) / 2,
                                    Game.DIM.height / 4 + fontHeight + spacer.getAndAdd(40))

                );


    }


}

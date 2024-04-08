package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.GameOp;
import edu.uchicago.gerber._08final.mvc.controller.Sound;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TrapMushroom extends Mushroom {

    //spawn every 25 seconds, same as the shield floater
    public static final int SPAWN_TRAP_FLOATER = Game.FRAMES_PER_SECOND * 25;
    public enum ImageState {MUSHROOM}
    private ImageState imageState;
    public TrapMushroom() {
        setExpiry(260);
        Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
        imageState = ImageState.MUSHROOM;
        rasterMap.put(imageState, loadGraphic("/imgs/Mushroom/mushroom_blue.png") );
        setRasterMap(rasterMap);
    }

    @Override
    public void remove(LinkedList<Movable> list) {
        super.remove(list);
        //if getExpiry() > 0, then this remove was the result of a collision, rather than natural mortality
        if (getExpiry() > 0) {
            Sound.playSound("insect.wav");
            setTrap(CommandCenter.getInstance().getLevel());
        }
    }

    @Override
    public void draw(Graphics g) {
        renderRaster((Graphics2D) g, getRasterMap().get(imageState));
    }

    private void setTrap(int level) {
        final int TRAP_SIZE = Game.DIM.width / 30;

        for (int nCol = 0; nCol < level; nCol++) {
            CommandCenter.getInstance().getOpsQueue().enqueue(
                    new Trap(
                            new Point(Game.R.nextInt(Game.DIM.width),
                                                    Game.R.nextInt(Game.DIM.height)),
                            TRAP_SIZE),
                    GameOp.Action.ADD);

        }
    }
}

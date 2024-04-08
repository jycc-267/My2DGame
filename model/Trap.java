package edu.uchicago.gerber._08final.mvc.model;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Trap extends Sprite {

    private final int TRAP_IMAGE = 0;

    public Trap(Point upperLeftCorner, int size) {

        setTeam(Team.TRAP);
        setCenter(new Point(upperLeftCorner.x + size/2, upperLeftCorner.y + size/2));
        setRadius(size/2);

        // As this sprite does not animate or change state, we could just store a BufferedImage as a member,
        // but since we already have a rasterMap in the Sprite class,
        // we might as well be consistent for all raster sprites and use it.
        Map<Integer, BufferedImage> rasterMap = new HashMap<>();
        rasterMap.put(TRAP_IMAGE, loadGraphic("/imgs/Trap/SquirrelTrap.png") ); //brick from Mario Bros
        setRasterMap(rasterMap);

    }

    @Override
    public void draw(Graphics g) {
        renderRaster((Graphics2D) g, getRasterMap().get(TRAP_IMAGE));
        // to help player clearly locate the position and range of a trap
        g.setColor(Color.LIGHT_GRAY);
        g.drawOval(getCenter().x - getRadius(), getCenter().y - getRadius(), getRadius() *2, getRadius() *2);
    }

    @Override
    // do nothing since a trap does not move
    public void move(){}


}

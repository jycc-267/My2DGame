package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.Sound;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Data
public class BeeHive extends Sprite {


    public enum ImageState {BEEHIVE}
    private ImageState imageState;


    public BeeHive(Bear bear) {

        setTeam(Team.FOE);

        //a bullet expires after 30 frames.
        setExpiry(30);
        //the size of a BeeHive
        setRadius(15);


        //everything is relative to the bear that threw the BeeHive
        setCenter(bear.getCenter());
        //set the BeeHive orientation to the bear orientation
        setOrientation(bear.getOrientation());

        final double FIRE_POWER = 10.0;
        double vectorX =
                Math.cos(Math.toRadians(bear.getOrientation())) * FIRE_POWER;
        double vectorY =
                Math.sin(Math.toRadians(bear.getOrientation())) * FIRE_POWER;

        //fire force: bear inertia + fire-vector
        //set the deltaX and deltaY to whatever the bear is to capture how fast the bear is moving
        setDeltaX(bear.getDeltaX() + vectorX);
        setDeltaY(bear.getDeltaY() + vectorY);

        Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
        imageState = ImageState.BEEHIVE;
        rasterMap.put(imageState, loadGraphic("/imgs/bullet/BeeHive.png") );
        setRasterMap(rasterMap);

    }


    @Override
    public void draw(Graphics g) {
        renderRaster((Graphics2D) g, getRasterMap().get(imageState));
    }

    @Override
    public void add(LinkedList<Movable> list) {
        super.add(list);
    }
}


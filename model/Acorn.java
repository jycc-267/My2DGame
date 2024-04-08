package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.Sound;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

@Data
public class Acorn extends Sprite {

    public enum ImageState {ACORN}
    private ImageState imageState;


    public Acorn(Squirrel squirrel) {

        setTeam(Team.FRIEND);

        //an acorn expires after 30 frames.
        setExpiry(30);
        //the size of an acorn
        setRadius(10);


        //everything is relative to the squirrel that fired the acorn
        setCenter(squirrel.getCenter());

        //set the acorn orientation to the squirrel orientation
        setOrientation(squirrel.getOrientation());

        final double FIRE_POWER = 35.0;
        double vectorX =
                Math.cos(Math.toRadians(getOrientation())) * FIRE_POWER;
        double vectorY =
                Math.sin(Math.toRadians(getOrientation())) * FIRE_POWER;

        //fire force: squirrel inertia + fire-vector
        //set the deltaX and deltaY to whatever the squirrel is to capture how fast the squirrel is moving
        setDeltaX(squirrel.getDeltaX() + vectorX);
        setDeltaY(squirrel.getDeltaY() + vectorY);


        Map<Acorn.ImageState, BufferedImage> rasterMap = new HashMap<>();
        imageState = ImageState.ACORN;
        rasterMap.put(imageState, loadGraphic("/imgs/bullet/bullet_acorn.png") );
        setRasterMap(rasterMap);

    }

    @Override
    public void draw(Graphics g) {
        renderRaster((Graphics2D) g, getRasterMap().get(imageState));
    }

    @Override
    public void add(LinkedList<Movable> list) {
        super.add(list);
        Sound.playSound("oraora.wav");

    }
}

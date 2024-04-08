package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.Sound;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

@Data
public class Shuriken extends Sprite {

    public enum ImageState {SHURIKEN}
    private ImageState imageState;


    public Shuriken(Squirrel squirrel) {

        setTeam(Team.FRIEND);

        //a shuriken expires after 20 frames.
        setExpiry(15);
        //the size of a shuriken
        setRadius(15);


        //everything is relative to the squirrel that threw the shuriken
        setCenter(squirrel.getCenter());

        //set the shuriken orientation to squirrel orientation
        setOrientation(squirrel.getOrientation());

        final double FIRE_POWER = 30.0;
        double vectorX =
                Math.cos(Math.toRadians(getOrientation())) * FIRE_POWER;
        double vectorY =
                Math.sin(Math.toRadians(getOrientation())) * FIRE_POWER;

        //fire force: squirrel inertia + fire-vector
        //set the deltaX and deltaY to whatever the squirrel is to capture how fast the squirrel is moving
        setDeltaX(squirrel.getDeltaX() + vectorX);
        setDeltaY(squirrel.getDeltaY() + vectorY);


        Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
        imageState = ImageState.SHURIKEN;
        rasterMap.put(imageState, loadGraphic("/imgs/bullet/Shuriken.png") );
        setRasterMap(rasterMap);

    }

    @Override
    // the fireball will bounce around the screen
    public void move(){
        if (getCenter().x > Game.DIM.width || getCenter().x < 0) {
            setDeltaX(-getDeltaX());
        }
        if (getCenter().y > Game.DIM.height || getCenter().y < 0) {
            setDeltaY(-getDeltaY());
        }

        double newXPos = getCenter().x + getDeltaX();
        double newYPos = getCenter().y + getDeltaY();
        setCenter(new Point((int) newXPos, (int) newYPos));

        if (getExpiry() > 0) expire();
    }

    @Override
    public void draw(Graphics g) {
        renderRaster((Graphics2D) g, getRasterMap().get(imageState));
    }

    @Override
    public void add(LinkedList<Movable> list) {
        super.add(list);
        Sound.playSound("shuriken.wav");

    }
}

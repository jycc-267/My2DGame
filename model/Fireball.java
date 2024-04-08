package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.Game;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Data
public class Fireball extends Sprite {

    public enum ImageState {FIREBALL}
    private ImageState imageState;


    // receive orientation as an argument to adjust the shooting direction of a fireball
    public Fireball(Fox fox, int orientation) {

        setTeam(Team.FOE);

        //a fireball expires after 60 frames.
        setExpiry(60);
        //the size of a fireball
        setRadius(30);


        //everything is relative to the boss fox that released the fireball
        setCenter(fox.getCenter());
        //set the fireball orientation to the boss fox orientation


        final double FIRE_POWER = 10.0;
        double vectorX =
                Math.cos(Math.toRadians(orientation)) * FIRE_POWER;
        double vectorY =
                Math.sin(Math.toRadians(orientation)) * FIRE_POWER;

        //fire force: boss fox inertia + fire-vector
        //set the deltaX and deltaY to whatever the boss fox is to capture how fast the boss fox is moving
        setDeltaX(vectorX);
        setDeltaY(vectorY);

        Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
        imageState = ImageState.FIREBALL;
        rasterMap.put(imageState, loadGraphic("/imgs/bullet/fireball.png") );
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
    }
}
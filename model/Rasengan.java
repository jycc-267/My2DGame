package edu.uchicago.gerber._08final.mvc.model;


import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Sound;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Data
public class Rasengan extends Sprite{

    private static final int EXPIRE = 60;
    private int nukeState = 0;
    public enum ImageState {RASENGAN}
    private ImageState imageState;

    public Rasengan(Squirrel squirrel) {
        setCenter(squirrel.getCenter());
        setExpiry(EXPIRE);
        setRadius(0);
        setTeam(Team.FRIEND);

        Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
        imageState = ImageState.RASENGAN;
        rasterMap.put(imageState, loadGraphic("/imgs/bullet/rasengan2.png") );
        setRasterMap(rasterMap);

    }


    @Override
    public void draw(Graphics g) {
        renderRaster((Graphics2D) g, getRasterMap().get(imageState));
    }



    @Override
    public void move() {
        super.move();
        if (getExpiry() % (EXPIRE/6) == 0) nukeState++;
        switch (nukeState) {
            //travelling
            case 0:
                setRadius(2);
                break;
            //exploding
            case 1:
            case 2:
            case 3:
                setRadius(getRadius() + 8);
                break;
            //imploding
            case 4:
            case 5:
            default:
                setRadius(getRadius() - 11);
                break;


        }

    }

    @Override
    public void add(LinkedList<Movable> list) {
        if (CommandCenter.getInstance().getSquirrel().getRasenganMeter() > 0){
            list.add(this);
            Sound.playSound("nuke.wav");
            CommandCenter.getInstance().getSquirrel().setRasenganMeter(0);
        }
    }

    @Override
    public void remove(LinkedList<Movable> list) {
        //only remove upon natural mortality (see expire() of Sprite), otherwise a Rasengan is invincible
        if (getExpiry() == 0) list.remove(this);
    }
}

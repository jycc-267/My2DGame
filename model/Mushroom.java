package edu.uchicago.gerber._08final.mvc.model;

import java.awt.*;

public abstract class Mushroom extends Sprite {

    public Mushroom() {

        setTeam(Team.MUSHROOM);

        //default values, all of which can be overridden in the extending concrete classes
        setExpiry(250);
        setRadius(50);
        //set random DeltaX
        setDeltaX(somePosNegValue(10));
        //set random DeltaY
        setDeltaY(somePosNegValue(10));
        //set random spin
        setSpin(somePosNegValue(10));

    }

    @Override
    public void draw(Graphics g) {

    }

}

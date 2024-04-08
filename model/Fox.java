package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.GameOp;
import edu.uchicago.gerber._08final.mvc.controller.Sound;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Data
public class Fox extends Sprite {

    private final int ORIGINAL_RADIUS = 100; //radius of a normal fox
    private final int BOSS_RADIUS = 200; //radius of a boss fox
    private int size;
    private int shadow; // record how many times a fox has teleported
    private int jutsu_limit; // the upper-bound of how many times a fox can teleport to another place

    public enum ImageState {NINJA, BOSS}
    private ImageState imageState;

    public Fox(int shadow, int jutsu_limit){
        this.shadow = shadow;
        this.jutsu_limit = jutsu_limit;
        if (this.shadow == 1) {
            if (this.imageState == ImageState.BOSS) setRadius(BOSS_RADIUS);
            else setRadius(ORIGINAL_RADIUS);
        }
        else {
            // the size of foxes will decrease when they use teleportation, increasing the difficulty of shooting them
            if (this.imageState == ImageState.BOSS) setRadius(BOSS_RADIUS / this.shadow);
            else setRadius(ORIGINAL_RADIUS / this.shadow);
        }

        setTeam(Team.FOE);
        //random delta-x
        setDeltaX(somePosNegValue(10));
        //random delta-y
        setDeltaY(somePosNegValue(10));

        Map<ImageState, BufferedImage> rasterMap = new HashMap<>();
        rasterMap.put(ImageState.NINJA, loadGraphic("/imgs/Fox/ninja-fox.png"));
        rasterMap.put(ImageState.BOSS, loadGraphic("/imgs/Fox/boss-fox.png"));
        setRasterMap(rasterMap);
    }



    //overloaded constructor
    public Fox(Fox shadowclone){
        //calls the prototype constructor and increment the value of shadow by 1
        this(shadowclone.getShadow() + 1, shadowclone.getJutsu_limit());
        // reset a fox to a random position
        int x = Game.R.nextInt(Game.DIM.width);
        int y = Game.R.nextInt(Game.DIM.height);
        setCenter(new Point(x, y));
    }

    //construct a 360 degree full-range attack for boss foxes
    public void bossAttack(){
        if (CommandCenter.getInstance().getFrame() % 100 == 0) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fireball(this, 0), GameOp.Action.ADD);
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fireball(this, 45), GameOp.Action.ADD);
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fireball(this, 90), GameOp.Action.ADD);
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fireball(this, 135), GameOp.Action.ADD);
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fireball(this, 180), GameOp.Action.ADD);
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fireball(this, 225), GameOp.Action.ADD);
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fireball(this, 270), GameOp.Action.ADD);
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fireball(this, 315), GameOp.Action.ADD);
            Sound.playSound("flames-effect.wav");
        }
    }

    @Override
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

        if (imageState == ImageState.BOSS) bossAttack();

    }


    @Override
    public void draw(Graphics g) {

        //set the image state of a normal fox to "BOSS" if the game level is a multiple of 3
        if (CommandCenter.getInstance().getLevel() % 3 == 0) imageState = ImageState.BOSS;
        else imageState = ImageState.NINJA;

        renderRaster((Graphics2D) g, getRasterMap().get(imageState));
    }

    @Override
    public void remove(LinkedList<Movable> list) {
        super.remove(list);
        teleportation(this);
        CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 10L * (getShadow()+ 1));
    }

    private void teleportation(Fox originalFox) {

        //if the value of shadow reaches the jutsu limit, the fox should be removed
        if (shadow == jutsu_limit) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new WhiteCloudDebris(originalFox), GameOp.Action.ADD);
            Sound.playSound("nani.wav");
        }
        else {
            CommandCenter.getInstance().getOpsQueue().enqueue(new WhiteCloudDebris(originalFox), GameOp.Action.ADD);
            Sound.playSound("substitution_clone.wav");
            //teleport by instantiating a new fox using an overloaded constructor
            CommandCenter.getInstance().getOpsQueue().enqueue(new Fox(originalFox), GameOp.Action.ADD);
        }

    }
}

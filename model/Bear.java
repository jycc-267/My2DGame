package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.GameOp;
import edu.uchicago.gerber._08final.mvc.controller.Sound;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Data
public class Bear extends Sprite{
    //radius of a large bear
    private final int LARGE_RADIUS = 80;
    private TurnState turnState;
    private ImageState imageState;
    public enum TurnState {UP, DOWN, LEFT, RIGHT, IDLE}
    public enum ImageState {
        Bear_UP,
        Bear_DOWN,
        Bear_LEFT,
        Bear_RIGHT
    }


    public Bear(int size){

        //Bear is FOE
        setTeam(Team.FOE);

        //a size of zero is a big bear
        //a size of 1 or 2 is medium or small bear respectively. See getSize() method.
        if (size == 0) setRadius(LARGE_RADIUS);
        else setRadius(LARGE_RADIUS/(size * 2));

        Map<Bear.ImageState, BufferedImage> rasterMap = new HashMap<>();
        rasterMap.put(ImageState.Bear_UP, loadGraphic("/imgs/Bear/BearNorth1.png") );
        rasterMap.put(ImageState.Bear_DOWN, loadGraphic("/imgs/Bear/BearSouth1.png") );
        rasterMap.put(ImageState.Bear_RIGHT, loadGraphic("/imgs/Bear/BearEast1.png") );
        rasterMap.put(ImageState.Bear_LEFT, loadGraphic("/imgs/Bear/BearWest1.png") );
        setRasterMap(rasterMap);

    }

    //overloaded constructor, so we can spawn smaller bears from a dead one
    public Bear(int size, Bear originalBear){

        setTeam(Team.FOE);

        if (size == 0) setRadius(LARGE_RADIUS);
        else setRadius(LARGE_RADIUS/(size * 2));
        setCenter(originalBear.getCenter());

        Map<Bear.ImageState, BufferedImage> rasterMap = new HashMap<>();
        rasterMap.put(ImageState.Bear_UP, loadGraphic("/imgs/Bear/BearNorth1.png") );
        rasterMap.put(ImageState.Bear_DOWN, loadGraphic("/imgs/Bear/BearSouth1.png") );
        rasterMap.put(ImageState.Bear_RIGHT, loadGraphic("/imgs/Bear/BearEast1.png") );
        rasterMap.put(ImageState.Bear_LEFT, loadGraphic("/imgs/Bear/BearWest1.png") );
        setRasterMap(rasterMap);
    }

    //construct a random number that determines the turn state of a bear (a naive random walking AI)
    public void setAction(){
        Random random = new Random();
        int i = random.nextInt(500) + 1;
        if (i <= 5) turnState = TurnState.UP;
        else if (i > 5 && i <= 10) turnState = TurnState.DOWN;
        else if (i > 10 && i <= 15) turnState = TurnState.RIGHT;
        else if (i > 15 && i <= 20) turnState = TurnState.LEFT;
        else turnState = TurnState.IDLE;
    }

    //attack and throw a BeeHive every 50 frames
    public void attack(){
        if (CommandCenter.getInstance().getFrame() % 50 == 0) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new BeeHive(this), GameOp.Action.ADD);
        }
    }


    //converts the radius to integer representing the size of the Bear:
    //0 = large, 1 = medium, 2 = small
    public int getSize(){
        switch (getRadius()) {
            case LARGE_RADIUS:
                return 0;
            case LARGE_RADIUS / 2:
                return 1;
            case LARGE_RADIUS / 4:
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public void move() {

        //determine the direction of a bear
        setAction();

        //right-bounds reached
        if (getCenter().x > Game.DIM.width) {
            setCenter(new Point(1, getCenter().y));
        //left-bounds reached
        } else if (getCenter().x < 0) {
            setCenter(new Point(Game.DIM.width - 1, getCenter().y));
        //bottom-bounds reached
        } else if (getCenter().y > Game.DIM.height) {
            setCenter(new Point(getCenter().x, 1));
        //top-bounds reached
        } else if (getCenter().y < 0) {
            setCenter(new Point(getCenter().x, Game.DIM.height - 1));
        //in-bounds
        } else {
            //move horizontally or vertically
            switch (turnState){
                case UP:
                    setDeltaX(Math.cos(Math.toRadians(270))*6);
                    setDeltaY(Math.sin(Math.toRadians(270))*6);
                    break;
                case DOWN:
                    setDeltaX(Math.cos(Math.toRadians(90))*6);
                    setDeltaY(Math.sin(Math.toRadians(90))*6);
                    break;
                case LEFT:
                    setDeltaX(Math.cos(Math.toRadians(180))*6);
                    setDeltaY(Math.sin(Math.toRadians(180))*6);
                    break;
                case RIGHT:
                    setDeltaX(Math.cos(Math.toRadians(0))*6);
                    setDeltaY(Math.sin(Math.toRadians(0))*6);
                    break;
                case IDLE:
                default:
                    //do nothing
            }
            double newXPos = getCenter().x + getDeltaX();
            double newYPos = getCenter().y + getDeltaY();
            setCenter(new Point((int) newXPos, (int) newYPos));

            //bear attacks
            attack();
        }

        int adjustOr = getOrientation();
        switch (turnState) {
            case UP:
                adjustOr = 270;
                break;
            case DOWN:
                adjustOr = 90;
                break;
            case LEFT:
                adjustOr = 180;
                break;
            case RIGHT:
                adjustOr = 0;
                break;
            case IDLE:
            default:
                //do nothing
        }

        // set orientation of a bear for the BeeHive class to access this field data
        setOrientation(adjustOr);


    }

    @Override
    public void draw(Graphics g) {

        if (getTurnState() == TurnState.UP){
            imageState = ImageState.Bear_UP;
        }
        if (getTurnState() == TurnState.DOWN){
            imageState = ImageState.Bear_DOWN;
        }
        if (getTurnState() == TurnState.LEFT){
            imageState = ImageState.Bear_LEFT;
        }
        if (getTurnState() == TurnState.RIGHT){
            imageState = ImageState.Bear_RIGHT;
        }
//        down-cast (widen the aperture of) the graphics object to gain access to methods of Graphics2D
//        and render the raster image according to the image-state
        renderRaster((Graphics2D) g, getRasterMap().get(imageState));

    }

    @Override
    public void remove(LinkedList<Movable> list) {
        super.remove(list);
        spawnSmallerBear(this);
        CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 10L * (getSize() + 1));
        Sound.playSound("mamma-mia.wav");

    }

    private void spawnSmallerBear(Bear originalBear) {

        int size = originalBear.getSize();
        if (size > 1) {
            CommandCenter.getInstance().getOpsQueue().enqueue(new WhiteCloudDebris(originalBear), GameOp.Action.ADD);
        }
        else {
            // zoom out the size of a new bear when the original one is removed
            size += 1;
            CommandCenter.getInstance().getOpsQueue().enqueue(new Bear(size, originalBear), GameOp.Action.ADD);
        }
    }
}

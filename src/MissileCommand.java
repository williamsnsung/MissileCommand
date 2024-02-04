import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MissileCommand extends PApplet{
    final int SCREEN_WIDTH = 500 ;
    final int SCREEN_HEIGHT = 500 ;
    final int BALLISTA_RADII = 25;

    int xStart, yStart, xEnd, yEnd ;

    // Holds all the force generators and the particles they apply to
    ForceRegistry forceRegistry ;
    int wave;
    int meteorsPerWave;
    Ballista[] ballista;
    Infrastructure[] city;
    int[] meteoriteVelocityRange;
    LinkedList<Missile> activeMissiles;
    LinkedList<EnemyMissile> enemies;
    int score;
    int scoreMultiplier;
    int consumedPoints;
    int activeBallista;

    public void fireMissile() {

    }

    public void triggerMissiles() {

    }

    public void isGameOver() {

    }

    public void settings() {
        size(SCREEN_WIDTH, SCREEN_HEIGHT) ;
        //Create a gravitational force
        Gravity gravity = new Gravity(new PVector(0f, .1f)) ;
        //Create a drag force
        //NB Increase k1, k2 to see an effect
        Drag drag = new Drag(10, 10) ;
        //Create the ForceRegistry
        forceRegistry = new ForceRegistry() ;
        wave = 0;
        meteorsPerWave = 1;
        ballista = new Ballista[]{
                new Ballista((float)(SCREEN_WIDTH * 0.1), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5),
                new Ballista((float)(SCREEN_WIDTH * 0.5), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5),
                new Ballista((float)(SCREEN_WIDTH * 0.9), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5)
        };
        city = new Infrastructure[]{};
        meteoriteVelocityRange = new int[]{10, 20};
        activeMissiles = new LinkedList<>();
        enemies = new LinkedList<>();
        score = 0;
        scoreMultiplier = 1;
        consumedPoints = 0;
        activeBallista = 0;
    }

    public void draw(){
        rect(0, (float)(SCREEN_HEIGHT * 0.9), SCREEN_WIDTH, (float)(SCREEN_HEIGHT * 0.1));
        for (Ballista value : ballista) {
            circle(value.getPosition().x, value.getPosition().y, BALLISTA_RADII);
        }

        forceRegistry.updateForces() ;

    }

    // When mouse is pressed, store x, y coords
    public void mousePressed() {

    }

    // When mouse is released create new vector relative to stored x, y coords
    public void mouseReleased() {

    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "MissileCommand" };
        PApplet.main(appletArgs);
    }
}
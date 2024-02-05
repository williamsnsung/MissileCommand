import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedHashMap;
import java.util.LinkedList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MissileCommand extends PApplet{
    final int SCREEN_WIDTH = 500 ;
    final int SCREEN_HEIGHT = 500 ;
    final int BALLISTA_RADII = 25;
    final char BALLISTA0 = 'q';
    final char BALLISTA1 = 'w';
    final char BALLISTA2 = 'e';
    final char TRIGGER = ' ';
    final int MISSILE_RADII = 10;
    final float MISSILE_VELOCITY = 25;
    final float INVERTED_MISSILE_MASS = 0.5f;
    final Gravity gravity = new Gravity(new PVector(0, 0.1f));
    final Drag drag = new Drag(.01f, .01f);
    int xStart, yStart, xEnd, yEnd ;

    // Holds all the force generators and the particles they apply to
    ForceRegistry forceRegistry ;
    int wave, meteorsPerWave, score, scoreMultiplier, consumedPoints, activeBallista;
    Ballista[] ballista;
    Infrastructure[] city;
    int[] meteoriteVelocityRange;
    LinkedList<Missile> activeMissiles;
    LinkedHashMap<Integer, EnemyMissile> enemies;
    boolean missilesTriggered;


    public void fireMissile(float mouseX, float mouseY) {
        PVector velocity = new PVector(mouseX, mouseY);
        velocity.sub(ballista[activeBallista].getPosition());
        velocity.normalize();
        System.out.println(velocity);
        velocity.mult(MISSILE_VELOCITY);
        float ballistaX = ballista[activeBallista].getPosition().x;
        float ballistaY = ballista[activeBallista].getPosition().y;
        Missile missile = new Missile(ballistaX, ballistaY, velocity.x, velocity.y, INVERTED_MISSILE_MASS);
        activeMissiles.add(missile);
        forceRegistry.add(missile, gravity);
        forceRegistry.add(missile, drag);
    }

    public void triggerMissiles() {
        for (Missile missile : activeMissiles) {

        }
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
        enemies = new LinkedHashMap<>();
        score = 0;
        scoreMultiplier = 1;
        consumedPoints = 0;
        activeBallista = 0;
    }

    public void draw(){
        background(0);
        rect(0, (float)(SCREEN_HEIGHT * 0.9), SCREEN_WIDTH, (float)(SCREEN_HEIGHT * 0.1));
        for (Ballista value : ballista) {
            circle(value.getPosition().x, value.getPosition().y, BALLISTA_RADII);
        }
        for (Missile missile : activeMissiles) {
            circle(missile.getPosition().x, missile.getPosition().y, MISSILE_RADII);
            missile.integrate();
        }

        forceRegistry.updateForces() ;

    }

    // When mouse is pressed, store x, y coords
    public void mousePressed() {

    }

    // When mouse is released create new vector relative to stored x, y coords
    public void mouseReleased() {
        System.out.println(mouseX + " " + mouseY);
        fireMissile(mouseX, mouseY);
    }

    public void keyReleased() {
        switch (key) {
            case BALLISTA0:
                activeBallista = 0;
                break;
            case BALLISTA1:
                activeBallista = 1;
                break;
            case BALLISTA2:
                activeBallista = 2;
                break;
            case TRIGGER:
                triggerMissiles();
                break;
        }
        System.out.println("Active Ballista: " + activeBallista);
    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "MissileCommand" };
        PApplet.main(appletArgs);
    }
}
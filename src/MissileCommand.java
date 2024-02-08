import processing.core.PApplet;
import processing.core.PVector;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MissileCommand extends PApplet{
    final int SCREEN_WIDTH = 500 ;
    final int SCREEN_HEIGHT = 500 ;
    final int BALLISTA_RADII = 25;
    final int CITY_RADII = 15;
    final int CITY_SCORE = 100;
    final char BALLISTA0 = 'q';
    final char BALLISTA1 = 'w';
    final char BALLISTA2 = 'e';
    final char TRIGGER = ' ';
    final int MISSILE_RADII = 10;
    final float MISSILE_VELOCITY = 28;
    final float INVERTED_MISSILE_MASS = 0.5f;
    final int EXPLOSION_RADIUS = 50;
    final Gravity gravity = new Gravity(new PVector(0, 0.1f));
    final Drag drag = new Drag(.01f, .01f);
    int xStart, yStart, xEnd, yEnd ;

    // Holds all the force generators and the particles they apply to
    ForceRegistry forceRegistry ;
    int wave, meteorsPerWave, score, scoreMultiplier, consumedPoints, activeBallista;
    Ballista[] ballistas;
    Infrastructure[] cities;
    int[] meteoriteVelocityRange;
    LinkedHashMap<Integer, Missile> exploding;
    LinkedHashMap<Integer, Missile> triggeredMissiles;
    LinkedHashMap<Integer, Missile> activeMissiles;
    LinkedHashMap<Integer, EnemyMissile> enemies;

    public <K, V> V popFirst(LinkedHashMap<K, V> hashMap){
        if (hashMap.isEmpty()) {
            return null;
        }
        Map.Entry<K, V> res = hashMap.entrySet().iterator().next();

        // Remove the first entry
        hashMap.remove(res.getKey());
        return res.getValue();
    }

    public void fireMissile(float mouseX, float mouseY) {
        PVector velocity = new PVector(mouseX, mouseY);
        velocity.sub(ballistas[activeBallista].getPosition());
        velocity.normalize();
        System.out.println(velocity);
        velocity.mult(MISSILE_VELOCITY);
        float ballistaX = ballistas[activeBallista].getPosition().x;
        float ballistaY = ballistas[activeBallista].getPosition().y;
        Missile missile = new Missile(ballistaX, ballistaY, velocity.x, velocity.y, INVERTED_MISSILE_MASS, EXPLOSION_RADIUS);
        activeMissiles.put(missile.getId(), missile);
        forceRegistry.add(missile, gravity);
        forceRegistry.add(missile, drag);
    }

    public void triggerMissiles() {
        for (Missile missile : activeMissiles.values()) {
            triggeredMissiles.put(missile.getId(), missile);
        }
        activeMissiles = new LinkedHashMap<>();
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
        ballistas = new Ballista[]{
                new Ballista((float)(SCREEN_WIDTH * 0.1), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5),
                new Ballista((float)(SCREEN_WIDTH * 0.5), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5),
                new Ballista((float)(SCREEN_WIDTH * 0.9), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5)
        };
        cities = new Infrastructure[]{
                new Infrastructure((float)(SCREEN_WIDTH * 0.2), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE),
                new Infrastructure((float)(SCREEN_WIDTH * 0.3), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE),
                new Infrastructure((float)(SCREEN_WIDTH * 0.4), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE),
                new Infrastructure((float)(SCREEN_WIDTH * 0.6), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE),
                new Infrastructure((float)(SCREEN_WIDTH * 0.7), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE),
                new Infrastructure((float)(SCREEN_WIDTH * 0.8), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE)
        };
        meteoriteVelocityRange = new int[]{10, 20};
        activeMissiles = new LinkedHashMap<>();
        exploding = new LinkedHashMap<>();
        triggeredMissiles = new LinkedHashMap<>();
        enemies = new LinkedHashMap<>();
        score = 0;
        scoreMultiplier = 1;
        consumedPoints = 0;
        activeBallista = 0;
    }

    public void draw(){
        cursor(CROSS);
        background(0);
        rect(0, (float)(SCREEN_HEIGHT * 0.9), SCREEN_WIDTH, (float)(SCREEN_HEIGHT * 0.1));
        for (Ballista ballista : ballistas) {
            ballista.draw(this, BALLISTA_RADII);
        }
        for (Infrastructure city : cities) {
            city.draw(this, CITY_RADII);
        }
        if (!triggeredMissiles.isEmpty()) {
            Missile activatedMissile = popFirst(triggeredMissiles);
            exploding.put(activatedMissile.getId(), activatedMissile);
        }

        LinkedList<Missile> surfaceMissiles = new LinkedList<>();
        for (Missile missile : activeMissiles.values()) {
            boolean isSurfaceMissile = false;
            if (missile.position.y > (float)(SCREEN_HEIGHT * 0.9)) {
                surfaceMissiles.add(missile);
                isSurfaceMissile = true;
            }
            if (!isSurfaceMissile) {
                missile.draw(this, MISSILE_RADII);
                missile.integrate();
            }
        }
        for (Missile surfaceMissile : surfaceMissiles) {
            activeMissiles.remove(surfaceMissile.getId());
            exploding.put(surfaceMissile.getId(), surfaceMissile);
        }

        if (!exploding.isEmpty()) {
            LinkedList<Missile> toExplode = new LinkedList<>();
            LinkedList<Missile> exploded = new LinkedList<>();

            for (Missile missile : exploding.values()) {
                toExplode =  missile.explode(this, ballistas, cities, enemies, activeMissiles, exploding);
                if (missile.getExplosionState() == 0) {
                    exploded.add(missile);
                }
            }

            while (!exploded.isEmpty() || !toExplode.isEmpty()) {
                if (!exploded.isEmpty()) {
                    for (Missile missile : exploded) {
                        exploding.remove(missile.getId());
                    }
                    exploded = new LinkedList<>();
                }
                if (!toExplode.isEmpty()) {
                    for (Missile missile : toExplode) {
                        exploding.put(missile.getId(), missile);
                        if (activeMissiles.containsKey(missile.getId())) {
                            activeMissiles.remove(missile.getId());
                        }
                        else {
                            enemies.remove(missile.getId());
                        }
                    }
                    toExplode = new LinkedList<>();
                }
            }
        }



        forceRegistry.updateForces() ;

    }

    // When mouse is released create new vector relative to stored x, y coords
    public void mouseReleased() {
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
    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[] { new Object(){}.getClass().getEnclosingClass().getSimpleName() };
        PApplet.main(appletArgs);
    }
}
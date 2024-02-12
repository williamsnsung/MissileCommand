import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

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
    final float INITIAL_METEORITE_VELOCITY = 1;
    final float INVERTED_MISSILE_MASS = 0.5f;
    final float INVERTED_METEORITE_MASS = 0.5f;
    final int EXPLOSION_RADIUS = 50;
    final int METEORITE_EXPLOSION_RADIUS = 25;
    final int METEORITE_RADII = 20;
    final int METEORITE_EXPLOSION_STATES = 5;
    final int EXPLOSION_STATES = 10;
    final int TRIGGER_SEQUENCE_LAG = 10;
    final int METEORITE_SPAWN_LAG = 75;
    final int MAX_EXPLOSION_DURATION = 20;
    final float SPLIT_PROBABILITY = 0.01f;
    final float BOMBER_PROBABILITY = 0.001f;
    final float SMART_BOMB_DISPLACEMENT_FORCE = 5f;
    final int SMART_BOMB_SEARCH_RADIUS = 100;
    final float BOMBER_DROP_PROBABILITY = 0.01f;
    final float BOMBER_MIN_HEIGHT = SCREEN_HEIGHT * 0.1f;
    final float BOMBER_MAX_HEIGHT = SCREEN_HEIGHT * 0.4f;
    final float SMART_BOMB_PROBABILITY = 0.001f;
    final int METEORITE_SPLIT_RADIUS = 15;
    final Gravity gravity = new Gravity(new PVector(0, 0.1f));
    final Drag drag = new Drag(.01f, .01f);
    final int METEORITE_SCORE = 25;
    final int CITY_REVIVAL_THRESHOLD = 10000;
    int triggerLag, explosionLag, meteoriteLag;
    ForceRegistry forceRegistry ;
    int activeBallista;
    Ballista[] ballistas;
    Infrastructure[] cities;
    int[] meteoriteVelocityRange;
    LinkedHashMap<Integer, Missile> exploding;
    LinkedHashMap<Integer, Missile> triggeredMissiles;
    LinkedHashMap<Integer, Missile> activeMissiles;
    LinkedHashMap<Integer, EnemyMissile> enemies;
    LinkedList<Missile> exploded;
    WaveManager waveManager;


    /**
     * Generic method that pops the first element from a LinkedHashMap
     * @param hashMap The hashmap to remove the first element from
     * @return Returns the value of the first element removed
     * @param <K> Any type
     * @param <V> Any type
     */
    public <K, V> V popFirst(LinkedHashMap<K, V> hashMap){
        if (hashMap.isEmpty()) {
            return null;
        }
        Map.Entry<K, V> res = hashMap.entrySet().iterator().next();

        // Remove the first entry
        hashMap.remove(res.getKey());
        return res.getValue();
    }

    /**
     * Fires a missile in a straight line by making a vector from your mouse position and the position of the active
     * ballista.
     * @param mouseX mouses x position
     * @param mouseY mouses y position
     */
    public void fireMissile(float mouseX, float mouseY) {
        if (ballistas[activeBallista].getMissiles() == 0) {
            return;
        }
        ballistas[activeBallista].decrementMissiles();
        PVector velocity = new PVector(mouseX, mouseY);
        velocity.sub(ballistas[activeBallista].getPosition());
        velocity.normalize();
        velocity.mult(MISSILE_VELOCITY);
        float ballistaX = ballistas[activeBallista].getPosition().x;
        float ballistaY = ballistas[activeBallista].getPosition().y;
        Missile missile = new Missile(ballistaX, ballistaY, velocity.x, velocity.y,
                INVERTED_MISSILE_MASS, EXPLOSION_RADIUS, EXPLOSION_STATES, MISSILE_RADII);
        activeMissiles.put(missile.getId(), missile);
        forceRegistry.add(missile, gravity);
        forceRegistry.add(missile, drag);
    }

    /**
     * Moves the active missiles into a triggered mode so that they can explode in sequence
     */
    public void triggerMissiles() {
        for (Missile missile : activeMissiles.values()) {
            triggeredMissiles.put(missile.getId(), missile);
        }
        activeMissiles = new LinkedHashMap<>();
    }

    /**
     * Checks if a missile is in the air or if it has collided with the ground
     * @param missile the missile to check collisions for
     * @return if the missile is in the air then true
     */
    public boolean missileInAir(Missile missile) {
        return !(missile.position.y > (float) (SCREEN_HEIGHT * 0.9));
    }

    /**
     * Takes GameObjects that have been triggered by another explosion and adds them into the list of exploding
     * objects
     * @param toExplode LinkedList of game objects that are exploding
     */
    public void integrateExplosions(LinkedList<Missile> toExplode) {
        while (!toExplode.isEmpty()) {
            Missile missile = toExplode.removeFirst();
            exploding.put(missile.getId(), missile);
            if (activeMissiles.containsKey(missile.getId())) {
                activeMissiles.remove(missile.getId());
            }
            else {
                enemies.remove(missile.getId());
            }
            forceRegistry.remove(missile);
        }
    }

    public void settings() {
        size(SCREEN_WIDTH, SCREEN_HEIGHT) ;
        //Create a gravitational force
        Gravity gravity = new Gravity(new PVector(0f, .1f)) ;
        //Create a drag force
        //Create the ForceRegistry
        forceRegistry = new ForceRegistry() ;
        ballistas = new Ballista[]{
                new Ballista((float)(SCREEN_WIDTH * 0.1), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5, BALLISTA_RADII),
                new Ballista((float)(SCREEN_WIDTH * 0.5), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5, BALLISTA_RADII),
                new Ballista((float)(SCREEN_WIDTH * 0.9), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, 5, BALLISTA_RADII)
        };
        cities = new Infrastructure[]{
                new Infrastructure((float)(SCREEN_WIDTH * 0.2), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE, CITY_RADII),
                new Infrastructure((float)(SCREEN_WIDTH * 0.3), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE, CITY_RADII),
                new Infrastructure((float)(SCREEN_WIDTH * 0.4), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE, CITY_RADII),
                new Infrastructure((float)(SCREEN_WIDTH * 0.6), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE, CITY_RADII),
                new Infrastructure((float)(SCREEN_WIDTH * 0.7), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE, CITY_RADII),
                new Infrastructure((float)(SCREEN_WIDTH * 0.8), (float)(SCREEN_HEIGHT * 0.9), 0, 0, 0, CITY_SCORE, CITY_RADII)
        };
        meteoriteVelocityRange = new int[]{10, 20};
        activeMissiles = new LinkedHashMap<>();
        exploding = new LinkedHashMap<>();
        exploded = new LinkedList<>();
        triggeredMissiles = new LinkedHashMap<>();
        enemies = new LinkedHashMap<>();
        activeBallista = 0;
        triggerLag = 1000000;
        explosionLag = 0;
        meteoriteLag = 10000000;
        waveManager = new WaveManager(this, SCREEN_HEIGHT, SCREEN_WIDTH, ballistas, cities,
                INVERTED_METEORITE_MASS, METEORITE_SCORE, METEORITE_EXPLOSION_RADIUS, METEORITE_EXPLOSION_STATES,
                INITIAL_METEORITE_VELOCITY, forceRegistry, gravity, drag, enemies, METEORITE_RADII,
                CITY_REVIVAL_THRESHOLD, SPLIT_PROBABILITY);
    }

    public void draw(){

        background(0);
        cursor(CROSS);
        // If game is over show game over screen
        if (waveManager.isGameOver()) {
            PFont font = createFont("Arial",16,true);
            textFont(font,24);
            text("GAME OVER", 175, 250);
        }
        else {
            // if no meteors remain and all meteors have been spawned then start a new wave
            if (waveManager.getMeteorsPerWave() <= waveManager.getMeteorsSpawned() && waveManager.getEnemiesAlive() <= 0) {
                waveManager.newWave();
            }
            waveManager.draw();

            // draws the ground
            rect(0, (float)(SCREEN_HEIGHT * 0.9), SCREEN_WIDTH, (float)(SCREEN_HEIGHT * 0.1));
            // draws the ballistas
            for (int i = 0; i < ballistas.length; i++) {
                ballistas[i].draw(this, i == activeBallista);
            }
            // draws the cities
            for (Infrastructure city : cities) {
                city.draw(this);
            }
            // game objects that have reached their maximum explosion radius are considered exploded
            // these will continue to explode until the explosion lag is expired
            for (Missile missile : exploded) {
                LinkedList<Missile> toExplode = missile.explode(this, ballistas, cities, enemies, activeMissiles, triggeredMissiles, waveManager);
                integrateExplosions(toExplode);
                explosionLag++;
            }

            // removes game objects in sequence from the exploded list
            if (!exploded.isEmpty() && explosionLag >= MAX_EXPLOSION_DURATION) {
                if (exploded.getFirst() instanceof EnemyMissile) {
                    waveManager.enemyKilled();
                }
                exploded.removeFirst();
                explosionLag = 0;
            }

            // If the spawn lag from the previous meteor is over then spawn a new meteor
            if (waveManager.getMeteorsSpawned() < waveManager.getMeteorsPerWave() && meteoriteLag > METEORITE_SPAWN_LAG) {
                meteoriteLag = 0;
                EnemyMissile enemyMissile = waveManager.spawnEnemy();
                enemies.put(enemyMissile.getId(), enemyMissile);
                forceRegistry.add(enemyMissile, gravity);
                forceRegistry.add(enemyMissile, drag);
            }

            // adds the triggered missiles in sequence to explode after some lag
            if (!triggeredMissiles.isEmpty() && triggerLag >= TRIGGER_SEQUENCE_LAG) {
                triggerLag = 0;
                Missile activatedMissile = popFirst(triggeredMissiles);
                exploding.put(activatedMissile.getId(), activatedMissile);
            }

            // draws missiles in the air unless they are colliding with an object, from which it will add it to a list
            // of colliding objects
            LinkedList<Missile> collidingMissiles = new LinkedList<>();
            for (Missile missile : activeMissiles.values()) {
                if (missileInAir(missile)) {
                    missile.draw(this);
                    missile.integrate();
                }
                else {
                    collidingMissiles.add(missile);
                }
            }

            LinkedList<EnemyMissile> newMissiles = new LinkedList<>();
            LinkedList<EnemyMissile> offScreenEnemies = new LinkedList<>();
            for (EnemyMissile enemyMissile : enemies.values()) {
                boolean collision = false;
                Missile collider = null;
                // checks if the enemy missile is colliding with any friendly ones. If it is, then adds the missile as
                // a collider and then adds the score of the current missile
                for (Missile missile : activeMissiles.values()) {
                    collision = enemyMissile.collisionCheck(missile);
                    if (collision) {
                        collider = missile;
                        waveManager.addScore(enemyMissile.getScore());
                        break;
                    }
                }

                // if missile is a bomber, then try dropping a bomb
                // if the bomber gets off the screen, then remove it
                if (enemyMissile.getType() == 1) {
                    EnemyMissile bomb = enemyMissile.dropBomb(this, waveManager, METEORITE_RADII, BOMBER_DROP_PROBABILITY);
                    if (bomb != null) {
                        newMissiles.add(bomb);
                    }
                    if (enemyMissile.getPosition().x > SCREEN_WIDTH) {
                        offScreenEnemies.add(enemyMissile);
                    }
                }
                // if the enemy is a smart bomb, then check for explosions nearby and move if necessary
                else if (enemyMissile.getType() == 2) {
                    enemyMissile.detectExplosions(exploding, SMART_BOMB_DISPLACEMENT_FORCE, SMART_BOMB_SEARCH_RADIUS);
                }

                // if it isn't the first wave, then try splitting meteorites
                if (waveManager.getWave() != 1) {
                    EnemyMissile splitMissile = enemyMissile.split(this, waveManager, METEORITE_SPLIT_RADIUS);
                    if (splitMissile != null ) {
                        newMissiles.add(splitMissile);
                    }
                }
                // if a missile is in the air and no collisions have occurred, then draw the enemy
                if (missileInAir(enemyMissile) && !collision) {
                    enemyMissile.draw(this);
                    enemyMissile.integrate();
                }
                // otherwise, add the enemy to the colliding missiles list along with the missile it collided with if it
                // is not null
                else {
                    if (collider != null){
                        collidingMissiles.add(collider);
                    }
                    collidingMissiles.add(enemyMissile);
                }
            }
            // if it isn't the first wave, try spawning a bomber
            if (waveManager.getWave() != 1) {
                EnemyMissile bomber = waveManager.spawnBomber(25, BOMBER_PROBABILITY,
                        BOMBER_MIN_HEIGHT, BOMBER_MAX_HEIGHT);
                if (bomber != null ) {
                    enemies.put(bomber.getId(), bomber);
                }
                // if the wave is above 5, then try spawning a smart bomb
                if (waveManager.getWave() >= 6) {
                    EnemyMissile smartBomb = waveManager.spawnSmartBomb(SMART_BOMB_PROBABILITY);
                    if (smartBomb != null ) {
                        enemies.put(smartBomb.getId(), smartBomb);
                        forceRegistry.add(smartBomb, gravity);
                        forceRegistry.add(smartBomb, drag);
                    }
                }
            }
            // remove off screen enemies
            for (EnemyMissile offScreenEnemy : offScreenEnemies) {
                enemies.remove(offScreenEnemy.getId());
            }
            // if new meteorites have been created such as from the bomber or from splitting, then add them
            while (!newMissiles.isEmpty()) {
                EnemyMissile enemyMissile = newMissiles.removeFirst();
                enemies.put(enemyMissile.getId(), enemyMissile);
                forceRegistry.add(enemyMissile, gravity);
                forceRegistry.add(enemyMissile, drag);
                enemyMissile.draw(this);
                enemyMissile.integrate();
            }

            // draw the triggered missiles
            for (Missile missile : triggeredMissiles.values()) {
                missile.draw(this);
                missile.integrate();
            }

            // for each colliding missile, remove them from their respective lists and add them to the list of exploding
            // missiles
            for (Missile missile : collidingMissiles) {
                if (missile instanceof EnemyMissile) {
                    enemies.remove(missile.getId());
                }
                else {
                    activeMissiles.remove(missile.getId());
                }
                exploding.put(missile.getId(), missile);
                forceRegistry.remove(missile);
            }

            // if the list of exploding missiles is not empty, keep exploding them
            // if they reach their max radius, add them to the list of exploded missiles
            // if other explosions were triggered by the current explosion, then integrate them into the game
            if (!exploding.isEmpty()) {
                LinkedList<Missile> toExplode = new LinkedList<>();

                for (Missile missile : exploding.values()) {
                    toExplode =  missile.explode(this, ballistas, cities, enemies, activeMissiles, triggeredMissiles,
                            waveManager);
                    if (missile.getExplosionState() == 1) {
                        exploded.add(missile);
                    }
                }
                for (Missile missile : exploded) {
                    exploding.remove(missile.getId());
                }
                integrateExplosions(toExplode);
            }

            triggerLag++;
            meteoriteLag++;
            forceRegistry.updateForces() ;
        }
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
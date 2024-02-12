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
    final float BOMBER_DROP_PROBABILITY = 0.01f;
    final float SMART_BOMB_PROBABILITY = 0.001f;
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

    public void triggerMissiles() {
        for (Missile missile : activeMissiles.values()) {
            triggeredMissiles.put(missile.getId(), missile);
        }
        activeMissiles = new LinkedHashMap<>();
    }

    public boolean missileInAir(Missile missile) {
        return !(missile.position.y > (float) (SCREEN_HEIGHT * 0.9));
    }

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
        if (waveManager.isGameOver()) {
            PFont font = createFont("Arial",16,true);
            textFont(font,24);
            text("GAME OVER", 175, 250);
        }
        else {
            if (waveManager.getMeteorsPerWave() <= waveManager.getMeteorsSpawned() && waveManager.getEnemiesAlive() <= 0) {
                waveManager.newWave();
            }
            waveManager.draw();

            rect(0, (float)(SCREEN_HEIGHT * 0.9), SCREEN_WIDTH, (float)(SCREEN_HEIGHT * 0.1));
            for (int i = 0; i < ballistas.length; i++) {
                ballistas[i].draw(this, i == activeBallista);
            }

            for (Infrastructure city : cities) {
                city.draw(this);
            }

            for (Missile missile : exploded) {
                LinkedList<Missile> toExplode = missile.explode(this, ballistas, cities, enemies, activeMissiles, triggeredMissiles, waveManager);
                integrateExplosions(toExplode);
                explosionLag++;
            }

            if (!exploded.isEmpty() && explosionLag >= MAX_EXPLOSION_DURATION) {
                if (exploded.getFirst() instanceof EnemyMissile) {
                    waveManager.enemyKilled();
                }
                exploded.removeFirst();
                explosionLag = 0;
            }

            if (waveManager.getMeteorsSpawned() < waveManager.getMeteorsPerWave() && meteoriteLag > METEORITE_SPAWN_LAG) {
                meteoriteLag = 0;
                EnemyMissile enemyMissile = waveManager.spawnMeteorite();
                enemies.put(enemyMissile.getId(), enemyMissile);
                forceRegistry.add(enemyMissile, gravity);
                forceRegistry.add(enemyMissile, drag);
            }

            if (!triggeredMissiles.isEmpty() && triggerLag >= TRIGGER_SEQUENCE_LAG) {
                triggerLag = 0;
                Missile activatedMissile = popFirst(triggeredMissiles);
                exploding.put(activatedMissile.getId(), activatedMissile);
            }

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
                for (Missile missile : activeMissiles.values()) {
                    collision = enemyMissile.collisionCheck(missile);
                    if (collision) {
                        collider = missile;
                        waveManager.addScore(enemyMissile.getScore());
                        break;
                    }
                }

                if (enemyMissile.getType() == 1) {
                    EnemyMissile bomb = enemyMissile.dropBomb(this, waveManager, METEORITE_RADII, BOMBER_DROP_PROBABILITY);
                    if (bomb != null) {
                        newMissiles.add(bomb);
                    }
                    if (enemyMissile.getPosition().x > SCREEN_WIDTH) {
                        offScreenEnemies.add(enemyMissile);
                    }
                } else if (enemyMissile.getType() == 2) {
                    enemyMissile.detectExplosions(exploding, SMART_BOMB_DISPLACEMENT_FORCE);
                }

                if (waveManager.getWave() != 1) {
                    EnemyMissile splitMissile = enemyMissile.split(this, waveManager);
                    if (splitMissile != null ) {
                        newMissiles.add(splitMissile);
                    }
                }
                if (missileInAir(enemyMissile) && !collision) {
                    enemyMissile.draw(this);
                    enemyMissile.integrate();
                }
                else {
                    if (collider != null){
                        collidingMissiles.add(collider);
                    }
                    collidingMissiles.add(enemyMissile);
                }
            }
            if (waveManager.getWave() != 1) {
                EnemyMissile bomber = waveManager.spawnBomber(25, BOMBER_PROBABILITY);
                if (bomber != null ) {
                    enemies.put(bomber.getId(), bomber);
                }

                if (waveManager.getWave() >= 6) {
                    EnemyMissile smartBomb = waveManager.spawnSmartBomb(SMART_BOMB_PROBABILITY);
                    if (smartBomb != null ) {
                        enemies.put(smartBomb.getId(), smartBomb);
                        forceRegistry.add(smartBomb, gravity);
                        forceRegistry.add(smartBomb, drag);
                    }
                }
            }
            for (EnemyMissile offScreenEnemy : offScreenEnemies) {
                enemies.remove(offScreenEnemy.getId());
            }

            while (!newMissiles.isEmpty()) {
                EnemyMissile enemyMissile = newMissiles.removeFirst();
                enemies.put(enemyMissile.getId(), enemyMissile);
                forceRegistry.add(enemyMissile, gravity);
                forceRegistry.add(enemyMissile, drag);
                enemyMissile.draw(this);
                enemyMissile.integrate();
            }

            for (Missile missile : triggeredMissiles.values()) {
                missile.draw(this);
                missile.integrate();
            }

            for (Missile surfaceMissile : collidingMissiles) {
                if (surfaceMissile instanceof EnemyMissile) {
                    enemies.remove(surfaceMissile.getId());
                }
                else {
                    activeMissiles.remove(surfaceMissile.getId());
                }
                exploding.put(surfaceMissile.getId(), surfaceMissile);
            }

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
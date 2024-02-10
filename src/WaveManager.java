import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.util.LinkedHashMap;

public class WaveManager {
    int wave;
    int meteorsPerWave;
    int meteorsSpawned;
    int enemiesAlive;
    final int SCREEN_WIDTH, SCREEN_HEIGHT, METEORITE_SCORE, METEORITE_EXPLOSION_RADIUS, METEORITE_EXPLOSION_STATES,
    METEORITE_RADII;
    final float INVERTED_METEORITE_MASS;
    Ballista[] ballistas;
    Infrastructure[] cities;
    PApplet sketch;
    float meteoriteVelocity;
    ForceRegistry forceRegistry;
    Gravity gravity;
    Drag drag;
    LinkedHashMap<Integer, EnemyMissile> enemies;
    final int FIB_INIT = 2;

    WaveManager(PApplet sketch, int SCREEN_HEIGHT, int SCREEN_WIDTH, Ballista[] ballistas, Infrastructure[] cities,
                float INVERTED_METEORITE_MASS, int METEORITE_SCORE, int METEORITE_EXPLOSION_RADIUS,
                int METEORITE_EXPLOSION_STATES, float INITIAL_METEORITE_VELOCITY,
                ForceRegistry forceRegistry, Gravity gravity, Drag drag, LinkedHashMap<Integer, EnemyMissile> enemies,
                int METEORITE_RADII) {
        this.sketch = sketch;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
        this.INVERTED_METEORITE_MASS = INVERTED_METEORITE_MASS;
        this.METEORITE_SCORE = METEORITE_SCORE;
        this.METEORITE_EXPLOSION_RADIUS = METEORITE_EXPLOSION_RADIUS;
        this.METEORITE_EXPLOSION_STATES = METEORITE_EXPLOSION_STATES;
        this.METEORITE_RADII = METEORITE_RADII;
        this.meteoriteVelocity = INITIAL_METEORITE_VELOCITY;
        this.ballistas = ballistas;
        this.cities = cities;
        this.wave = 1;
        this.meteorsPerWave = fib(wave);
        this.meteorsSpawned = 0;
        this.enemiesAlive = 0;
        this.forceRegistry = forceRegistry;
        this.gravity = gravity;
        this.drag = drag;
        this.enemies = enemies;
    }

    // https://r-knott.surrey.ac.uk/Fibonacci/fibFormula.html [09/02/2024]
    public int fib(int n) {
        n += FIB_INIT;
        double phi = 1.6180339887;
        double sqrt5 = 2.2360679775;
        double res = (Math.pow(phi, n) - Math.pow(-phi, -n))/sqrt5;
        return (int) res;
    }

    public float newMissileVelocity() {
        return sketch.random(meteoriteVelocity, meteoriteVelocity + fib(wave));
    }

    public void spawnMeteorite() {
        float x = sketch.random(SCREEN_WIDTH);
        float y = (float) (SCREEN_HEIGHT * 0.1);
        PVector pos = new PVector(x, y);
        PVector velocity;
        int target = (int) sketch.random(ballistas.length + cities.length);
        if (target < cities.length) {
//            velocity = pos.add(cities[target].getPosition());
            velocity = cities[target].getPosition().sub(pos);
        }
        else {
//            velocity = pos.add(ballistas[target % cities.length].getPosition());
            velocity = ballistas[target % cities.length].getPosition().sub(pos);
        }
        velocity.normalize();
        velocity.mult(newMissileVelocity());
        this.meteorsSpawned++;
        this.enemiesAlive++;
        EnemyMissile enemyMissile = new EnemyMissile(x, y, velocity.x, velocity.y,
                INVERTED_METEORITE_MASS, METEORITE_SCORE, METEORITE_EXPLOSION_RADIUS, METEORITE_EXPLOSION_STATES,
                METEORITE_RADII
                );
        enemies.put(enemyMissile.getId(), enemyMissile);
        forceRegistry.add(enemyMissile, gravity);
        forceRegistry.add(enemyMissile, drag);
    }

    public void newWave() {
        this.wave++;
        this.meteorsPerWave = fib(wave);
        this.meteoriteVelocity += fib(wave);
        this.meteorsSpawned = 0;
        this.enemiesAlive = 0;
        for (Ballista ballista : ballistas) {
            ballista.restockMissiles();
        }
    }

    public int getMeteorsPerWave() {
        return this.meteorsPerWave;
    }

    public int getMeteorsSpawned() {
        return this.meteorsSpawned;
    }

    public int getEnemiesAlive() {
        return this.enemiesAlive;
    }

    public void enemyKilled() {
        this.enemiesAlive--;
    }

    public void draw() {
        PFont font = sketch.createFont("Arial",16,true);
        sketch.textFont(font,16);
        sketch.text("WAVE: " + wave, 5, 20);
    }
}

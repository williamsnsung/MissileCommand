import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.util.LinkedHashMap;

public class WaveManager {
    int wave, meteorsPerWave, meteorsSpawned, enemiesAlive, score;
    final int SCREEN_WIDTH, SCREEN_HEIGHT, METEORITE_SCORE, METEORITE_EXPLOSION_RADIUS, METEORITE_EXPLOSION_STATES,
    METEORITE_RADII, CITY_REVIVAL_THRESHOLD;
    final float INVERTED_METEORITE_MASS, SPLIT_PROBABILITY;
    Ballista[] ballistas;
    Infrastructure[] cities;
    PApplet sketch;
    float meteoriteVelocity;
    ForceRegistry forceRegistry;
    Gravity gravity;
    Drag drag;
    LinkedHashMap<Integer, EnemyMissile> enemies;
    final int FIB_INIT = 2;
    int consumedPoints;
    boolean gameOver;

    WaveManager(PApplet sketch, int SCREEN_HEIGHT, int SCREEN_WIDTH, Ballista[] ballistas, Infrastructure[] cities,
                float INVERTED_METEORITE_MASS, int METEORITE_SCORE, int METEORITE_EXPLOSION_RADIUS,
                int METEORITE_EXPLOSION_STATES, float INITIAL_METEORITE_VELOCITY,
                ForceRegistry forceRegistry, Gravity gravity, Drag drag, LinkedHashMap<Integer, EnemyMissile> enemies,
                int METEORITE_RADII, int CITY_REVIVAL_THRESHOLD, float SPLIT_PROBABILITY) {
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
        this.score = 0;
        this.consumedPoints = 0;
        this.CITY_REVIVAL_THRESHOLD = CITY_REVIVAL_THRESHOLD;
        this.gameOver = false;
        this.SPLIT_PROBABILITY = SPLIT_PROBABILITY;
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
        return sketch.random(meteoriteVelocity * 0.9f, meteoriteVelocity * 1.1f);
    }

    public EnemyMissile spawnMeteorite() {
        float x = sketch.random(SCREEN_WIDTH);
        float y = (float) (SCREEN_HEIGHT * 0.1);
        return spawnMeteorite(METEORITE_RADII, x, y, SPLIT_PROBABILITY);
    }

    public EnemyMissile spawnMeteorite(int radius, float x, float y, float splitProbability) {
        PVector pos = new PVector(x, y);
        PVector velocity;
        int target = (int) sketch.random(ballistas.length + cities.length);
        if (target < cities.length) {
            velocity = cities[target].getPosition().sub(pos);
        }
        else {
            velocity = ballistas[target % cities.length].getPosition().sub(pos);
        }
        velocity.normalize();
        velocity.mult(newMissileVelocity());
        this.meteorsSpawned++;
        this.enemiesAlive++;

        return new EnemyMissile(x, y, velocity.x, velocity.y,
                INVERTED_METEORITE_MASS, METEORITE_SCORE, METEORITE_EXPLOSION_RADIUS, METEORITE_EXPLOSION_STATES,
                radius, splitProbability, 0);
    }

    public EnemyMissile spawnBomber(int radius, float spawnProbability) {
        EnemyMissile enemyMissile = null;
        boolean spawn = spawnProbability > sketch.random(1);
        if (spawn) {
            float x = 0;
            float y = sketch.random((float) (SCREEN_HEIGHT * 0.2), (float) SCREEN_HEIGHT / 2);

            enemyMissile = new EnemyMissile(x, y, 3, 0,
                    INVERTED_METEORITE_MASS, 100, METEORITE_EXPLOSION_RADIUS, METEORITE_EXPLOSION_STATES,
                    radius, 0, 1);
        }
        return enemyMissile;
    }

    public void newWave() {
        this.wave++;
        this.meteorsPerWave = fib(wave);
        this.meteoriteVelocity = wave;
        this.meteorsSpawned = 0;
        this.enemiesAlive = 0;
        int curScore = 0;

        for (Infrastructure city : cities) {
            if (city.isAlive()) {
                curScore += city.getScore();
            }
        }

        for (Ballista ballista : ballistas) {
            curScore += ballista.getMissiles() * ballista.getScore();
            ballista.restockMissiles();
        }
        this.score += curScore * getScoreMultiplier();

        if (this.score - this.consumedPoints > CITY_REVIVAL_THRESHOLD) {
            for (Infrastructure city : cities) {
                if (!city.isAlive()) {
                    city.revive();
                    this.consumedPoints += CITY_REVIVAL_THRESHOLD;
                    break;
                }
            }
        }

        int dead = 0;
        for (Infrastructure city : cities) {
            if (!city.isAlive()) {
                dead++;
            }
        }
        gameOver = dead == cities.length;
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
        sketch.text("WAVE: " + this.wave, 5, 20);
        sketch.text("SCORE: " + this.score, 5, 40);
    }

    public void addScore(int score) {
        this.score += score * getScoreMultiplier();
    }

    private int getScoreMultiplier() {
        int scoreMultiplier = 1;
        switch (this.wave) {
            case (1):
            case (2):
                break;
            case (3):
            case (4):
                scoreMultiplier = 2;
                break;
            case (5):
            case (6):
                scoreMultiplier = 3;
                break;
            case (7):
            case (8):
                scoreMultiplier = 4;
                break;
            case (9):
            case (10):
                scoreMultiplier = 5;
                break;
            default:
                scoreMultiplier = 6;
        }
        return scoreMultiplier;
    }

    public boolean isGameOver() {
        return this.gameOver;
    }

    public int getWave() {
        return this.wave;
    }
}

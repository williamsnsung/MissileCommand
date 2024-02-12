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
        this.meteorsPerWave = wave;
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

    /**
     * Allocates a random velocity ±10% of the current meteorite velocity attribute
     * @return the selected velocity
     */
    public float newMissileVelocity() {
        return sketch.random(meteoriteVelocity * 0.9f, meteoriteVelocity * 1.1f);
    }

    /**
     * Spawns a meteorite with a given radius, position, and probability
     * Location of meteorite is selected randomly at the top of the screen
     * @return
     */
    public EnemyMissile spawnEnemy() {
        float x = sketch.random(SCREEN_WIDTH);
        float y = (float) (SCREEN_HEIGHT * 0.1);
        return spawnEnemy(METEORITE_RADII, x, y, SPLIT_PROBABILITY, 0);
    }

    /**
     * Spawns a smart bomb if it passes a probability check
     * @param spawnProbability the spawn probability for a smart bomb
     * @return
     */
    public EnemyMissile spawnSmartBomb(float spawnProbability) {
        EnemyMissile enemyMissile = null;
        boolean drop = spawnProbability > sketch.random(1);
        if (drop) {
            float x = sketch.random(SCREEN_WIDTH);
            float y = (float) (SCREEN_HEIGHT * 0.1);
            enemyMissile = spawnEnemy(METEORITE_RADII, x, y, 0, 2);
        }
        return enemyMissile;
    }

    /**
     * Spawns an enemy with specified parameters targeting a random piece of infrastructure
     * @param radius radius of the spawned meteorite
     * @param x the x location of the meteorite to spawn in
     * @param y the x location of the meteorite to spawn in
     * @param splitProbability the probability for this meteorite to split in two
     * @param type what type of enemy to spawn
     * @return the spawned enemy
     */
    public EnemyMissile spawnEnemy(int radius, float x, float y, float splitProbability, int type) {
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
                radius, splitProbability, type);
    }

    /**
     * Spawns a bomber
     * @param radius radius of the bomber
     * @param spawnProbability the probability that the bomber should spawn
     * @param maxHeight the max possible height the bomber could spawn at
     * @param minHeight the minimum possible height the bomber could spawn at
     * @return the spawned bomber or null
     */
    public EnemyMissile spawnBomber(int radius, float spawnProbability, float minHeight, float maxHeight) {
        EnemyMissile enemyMissile = null;
        boolean spawn = spawnProbability > sketch.random(1);
        if (spawn) {
            float x = 0;
            float y = sketch.random(minHeight, maxHeight);

            enemyMissile = new EnemyMissile(x, y, 3, 0,
                    INVERTED_METEORITE_MASS, 100, METEORITE_EXPLOSION_RADIUS, METEORITE_EXPLOSION_STATES,
                    radius, 0, 1);
        }
        return enemyMissile;
    }

    /**
     * Starts a new wave
     */
    public void newWave() {
        this.wave++;
        this.meteoriteVelocity = wave;
        this.meteorsPerWave = wave;
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

    /**
     * Decrements the enemiesAlive parameter
     */
    public void enemyKilled() {
        this.enemiesAlive--;
    }

    public void draw() {
        PFont font = sketch.createFont("Arial",16,true);
        sketch.textFont(font,16);
        sketch.text("WAVE: " + this.wave, 5, 20);
        sketch.text("SCORE: " + this.score, 5, 40);
    }

    /**
     * Adds to the score based on the score multiplier
     * @param score the score to be added
     */
    public void addScore(int score) {
        this.score += score * getScoreMultiplier();
    }

    /**
     * Gets the score multiplier for the current round
     * @return the score multiplier for the current round
     */
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

    /**
     * Returns whether the game is over
     * @return boolean based on if game is over, if it is, true
     */
    public boolean isGameOver() {
        return this.gameOver;
    }

    public int getWave() {
        return this.wave;
    }
}

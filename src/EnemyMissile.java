import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedHashMap;

public class EnemyMissile extends Missile{
    private final int score;
    private float splitProbability;
    private final int type;

    EnemyMissile(float x, float y, float xVel, float yVel, float invM, int score, int explosionRadius,
                 int explosionState, int METEOR_RADIUS, float splitProbability, int type) {
        super(x, y, xVel, yVel, invM, explosionRadius, explosionState, METEOR_RADIUS);
        this.score = score;
        this.splitProbability = splitProbability;
        this.type = type;
    }

    /**
     * Draws the missile depending on the type
     * 0 for meteor
     * 1 for bomber
     * 2 for smart bomb
     * @param sketch the processing sketch
     */
    public void draw(PApplet sketch) {

        switch (type) {
            case 0:
                sketch.fill(255, 0, 0);
                break;
            case 1:
                sketch.fill(255, 0, 255);
                break;
            case 2:
                sketch.fill(255, 255, 0);
        }
        sketch.circle(this.position.x, this.position.y, this.getRadius());
        sketch.fill(255, 255, 255);
    }

    public int getScore() {
        return this.score;
    }

    /**
     * Tries to split the meteor based on some probability given
     * @param sketch the processing sketch
     * @param waveManager the wave manager
     * @return null if probability check failed, new missile if it didn't
     */
    public EnemyMissile split(PApplet sketch, WaveManager waveManager, int splitRadius) {
        EnemyMissile enemyMissile = null;
        boolean meteorSplit = this.splitProbability > sketch.random(1);
        if (meteorSplit) {
            enemyMissile = waveManager.spawnEnemy(splitRadius,
                    this.position.x, this.position.y, 0, 0);
            this.splitProbability = 0;
            this.setRadius(splitRadius);
        }
        return enemyMissile;
    }

    /**
     * Drops a bomb from a bomber when called if a probability check passes
     * @param sketch the current processing sketch
     * @param waveManager the wave manager
     * @param radius radius of the object being dropped
     * @param bombProbability the probability of the bomb being successfully spawned
     * @return null if probability check fails, new meteorite if it passes
     */
    public EnemyMissile dropBomb(PApplet sketch, WaveManager waveManager, int radius, float bombProbability) {
        EnemyMissile enemyMissile = null;
        boolean drop = bombProbability > sketch.random(1);
        if (drop) {
            enemyMissile = waveManager.spawnEnemy(radius,
                    this.position.x, this.position.y, splitProbability, 0);
        }
        return enemyMissile;
    }

    /**
     * Returns the type of enemy this is
     * @return 0 for meteorite, 1 for bomber, 2 for smart bomb
     */
    public int getType() {
        return this.type;
    }

    /**
     * Detects explosions nearby in a given radius and moves in an opposing direction to get away from the explosion
     * if there is one nearby
     * @param exploding the list of exploding objects
     * @param smartBombSearchRadius the radius that the bomb searches for explosions in
     * @param force the force from which it will try to avoid the explosion
     */
    public void detectExplosions(LinkedHashMap<Integer, Missile> exploding, float force, int smartBombSearchRadius) {
        for (Missile missile : exploding.values()) {
            if (this.position.dist(missile.getPosition()) < smartBombSearchRadius) {
                PVector velocity = missile.getPosition().sub(this.position);
                velocity.x = -velocity.x;
                velocity.y = -velocity.y;
                velocity.normalize();
                velocity.mult(force);
                this.velocity = velocity;
                return;
            }
        }
    }
}

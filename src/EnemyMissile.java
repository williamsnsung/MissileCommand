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

    public EnemyMissile split(PApplet sketch, WaveManager waveManager) {
        EnemyMissile enemyMissile = null;
        boolean meteorSplit = this.splitProbability > sketch.random(1);
        if (meteorSplit) {
            enemyMissile = waveManager.spawnMeteorite((int) this.getRadius() / 2,
                    this.position.x, this.position.y, 0, 0);
            this.splitProbability = 0;
            this.setRadius(this.getRadius() / 2);
        }
        return enemyMissile;
    }

    public EnemyMissile dropBomb(PApplet sketch, WaveManager waveManager, int radius, float bombProbability) {
        EnemyMissile enemyMissile = null;
        boolean drop = bombProbability > sketch.random(1);
        if (drop) {
            enemyMissile = waveManager.spawnMeteorite(radius,
                    this.position.x, this.position.y, splitProbability, 0);
        }
        return enemyMissile;
    }

    public int getType() {
        return this.type;
    }
    public void detectExplosions(LinkedHashMap<Integer, Missile> exploding, float force) {
        for (Missile missile : exploding.values()) {
            if (this.position.dist(missile.getPosition()) < 100) {
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

import processing.core.PApplet;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Missile extends GameObject{
    private final int explosionRadius;
    private int explosionState;
    private int radius;

    Missile(float x, float y, float xVel, float yVel, float invM, int explosionRadius, int explosionState,
            int MISSILE_RADIUS) {
        super(x, y, xVel, yVel, invM);
        this.explosionRadius = explosionRadius;
        this.explosionState = explosionState;
        this.radius = MISSILE_RADIUS;
    }

    public LinkedList<Missile> explode(PApplet sketch, Ballista[] ballistas, Infrastructure[] cities,
                        LinkedHashMap<Integer, EnemyMissile> enemies, LinkedHashMap<Integer, Missile> activeMissiles,
                        LinkedHashMap<Integer, Missile> triggeredMissiles, WaveManager waveManager) {

        float curRadius = (float) explosionRadius / explosionState;
        LinkedList<Missile> toExplode = new LinkedList<>();

        sketch.circle(this.position.x, this.position.y, curRadius);

        for (Ballista ballista : ballistas) {
            if (this.position.dist(ballista.getPosition()) < curRadius) {
                ballista.die();
            }
        }
        for (Infrastructure city : cities) {
            if (this.position.dist(city.getPosition()) < curRadius) {
                city.die();
            }
        }
        for (EnemyMissile enemy : enemies.values()) {
            if (this != enemy && this.position.dist(enemy.getPosition()) < curRadius) {
                toExplode.add(enemy);
                waveManager.addScore(enemy.getScore());
            }
        }
        for (Missile missile : activeMissiles.values()) {
            if (this != missile && this.position.dist(missile.getPosition()) < curRadius) {
               toExplode.add(missile);
            }
        }

        for (Missile missile : triggeredMissiles.values()) {
            if (this != missile && this.position.dist(missile.getPosition()) < curRadius) {
                toExplode.add(missile);
            }
        }

        if (explosionState > 1) {
            explosionState--;
        }
        return toExplode;
    }

    public boolean collisionCheck(GameObject toCheck) {
        return this.position.dist(toCheck.getPosition()) < this.getRadius();
    }

    public void draw(PApplet sketch) {
        sketch.circle(this.position.x, this.position.y, this.radius);
    }

    public int getExplosionState() {
        return this.explosionState;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}

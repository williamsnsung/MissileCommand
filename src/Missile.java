import processing.core.PApplet;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Missile extends GameObject{
    private final int explosionRadius;
    private int explosionState;

    Missile(float x, float y, float xVel, float yVel, float invM, int explosionRadius) {
        super(x, y, xVel, yVel, invM);
        this.explosionRadius = explosionRadius;
        explosionState = 10;
    }

    public LinkedList<Missile> explode(PApplet sketch, Ballista[] ballistas, Infrastructure[] cities,
                        LinkedHashMap<Integer, EnemyMissile> enemies, LinkedHashMap<Integer, Missile> activeMissiles,
                        LinkedHashMap<Integer, Missile> exploding) {

        float curRadius = (float) explosionRadius / explosionState;
        LinkedList<EnemyMissile> enemiesToRemove = new LinkedList<>();
        LinkedList<Missile> missilesToRemove = new LinkedList<>();
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
            if (this.position.dist(enemy.getPosition()) < curRadius) {
                enemiesToRemove.add(enemy);
            }
        }
        for (Missile missile : activeMissiles.values()) {
            if (this != missile && this.position.dist(missile.getPosition()) < curRadius) {
               missilesToRemove.add(missile);
            }
        }

        for (EnemyMissile enemyMissile : enemiesToRemove) {
            toExplode.add(enemyMissile);
            enemies.remove(enemyMissile.getId());
        }

        for (Missile missile : missilesToRemove) {
            toExplode.add(missile);
            activeMissiles.remove(missile.getId());
        }

        explosionState--;
        return toExplode;
    }

    public void draw(PApplet sketch, int MISSILE_RADII) {
        sketch.circle(this.position.x, this.position.y, MISSILE_RADII);
    }

    public int getExplosionState() {
        return this.explosionState;
    }
}

import processing.core.PApplet;

import java.util.LinkedHashMap;

public class Missile extends GameObject{
    private static final float EXPLOSION_RADIUS = 12;
    private int explosionState;

    Missile(float x, float y, float xVel, float yVel, float invM) {
        super(x, y, xVel, yVel, invM);
        explosionState = 4;
    }

    public void explode(Ballista[] ballistas, Infrastructure[] cities,
                       LinkedHashMap<Integer, EnemyMissile> enemies, LinkedHashMap<Integer, Missile> activeMissiles) {
        float curRadius = EXPLOSION_RADIUS / explosionState;
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
                enemy.explode(ballistas, cities, enemies, activeMissiles);
            }
        }
        for (Missile missile : activeMissiles.values()) {
            if (this != missile && this.position.dist(missile.getPosition()) < curRadius) {
                missile.explode(ballistas, cities, enemies, activeMissiles);
            }
        }

        explosionState--;
    }
}

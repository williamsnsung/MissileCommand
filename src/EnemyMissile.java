import processing.core.PApplet;

public class EnemyMissile extends Missile{
    private final int score;
    EnemyMissile(float x, float y, float xVel, float yVel, float invM, int score, int explosionRadius, int explosionState) {
        super(x, y, xVel, yVel, invM, explosionRadius, explosionState);
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

    public void splinter() {

    }
}

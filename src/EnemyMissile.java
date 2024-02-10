import processing.core.PApplet;

public class EnemyMissile extends Missile{
    private final int score;
    EnemyMissile(float x, float y, float xVel, float yVel, float invM, int score, int explosionRadius,
                 int explosionState, int METEOR_RADIUS) {
        super(x, y, xVel, yVel, invM, explosionRadius, explosionState, METEOR_RADIUS);
        this.score = score;
    }

    public void draw(PApplet sketch) {
        sketch.fill(255, 0, 0);
        sketch.circle(this.position.x, this.position.y, this.getRadius());
        sketch.fill(255, 255, 255);
    }

    public int getScore() {
        return this.score;
    }

    public void splinter() {

    }
}

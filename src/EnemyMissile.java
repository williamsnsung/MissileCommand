import processing.core.PApplet;

public class EnemyMissile extends Missile{
    private final int score;
    private float splitProbability;
    EnemyMissile(float x, float y, float xVel, float yVel, float invM, int score, int explosionRadius,
                 int explosionState, int METEOR_RADIUS, float splitProbability) {
        super(x, y, xVel, yVel, invM, explosionRadius, explosionState, METEOR_RADIUS);
        this.score = score;
        this.splitProbability = splitProbability;
    }

    public void draw(PApplet sketch) {
        sketch.fill(255, 0, 0);
        sketch.circle(this.position.x, this.position.y, this.getRadius());
        sketch.fill(255, 255, 255);
    }

    public int getScore() {
        return this.score;
    }

    public boolean split(PApplet sketch, WaveManager waveManager) {
        boolean meteorSplit = this.splitProbability > sketch.random(1);
        if (this.splitProbability > sketch.random(1)) {
            waveManager.spawnMeteorite((int) this.getRadius() / 2, this.position.x, this.position.y, 0);
            this.splitProbability = 0;
            this.setRadius(this.getRadius() / 2);
        }
        return meteorSplit;
    }
}

import processing.core.PApplet;

public class Infrastructure extends GameObject{
    private boolean alive;
    private final int score;
    private final int radius;

    Infrastructure(float x, float y, float xVel, float yVel, float invM, int score, int radius) {
        super(x, y, xVel, yVel, invM);
        this.alive = true;
        this.score = score;
        this.radius = radius;
    }

    public void draw(PApplet sketch) {
        if (this.alive) {
            sketch.circle(this.position.x, this.position.y, radius);
        }
    }

    public int getRadius() {
        return this.radius;
    }

    public int getScore() {
        return this.score;
    }

    /**
     *
     */
    public void die() {
        this.alive = false;
    }

    public void revive() {
        this.alive = true;
    }

    public boolean isAlive() {
        return this.alive;
    }
}

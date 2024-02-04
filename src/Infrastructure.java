import processing.core.PApplet;

public class Infrastructure extends GameObject{
    private boolean alive;
    private final int score;

    Infrastructure(float x, float y, float xVel, float yVel, float invM, int score) {
        super(x, y, xVel, yVel, invM);
        this.alive = true;
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

    public void die() {
        this.alive = false;
    }

    public void revive() {
        this.alive = true;
    }
}

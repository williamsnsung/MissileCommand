import processing.core.PApplet;

public class Ballista extends Infrastructure{
    private int missiles;

    Ballista(float x, float y, float xVel, float yVel, float invM, int score) {
        super(x, y, xVel, yVel, invM, score);
        this.missiles = 10;
    }

    public void restockMissiles() {
        this.missiles = 10;
    }

    public void decrementMissiles() {
        this.missiles--;
    }

    public void draw(PApplet sketch, int BALLISTA_RADII) {
        sketch.circle(this.position.x, this.position.y, BALLISTA_RADII);
    }
}

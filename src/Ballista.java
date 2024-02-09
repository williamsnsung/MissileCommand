import processing.core.PApplet;
import processing.core.PFont;

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

    public int getMissiles() {
        return this.missiles;
    }
    public void draw(PApplet sketch, int BALLISTA_RADII) {
        sketch.circle(this.position.x, this.position.y, BALLISTA_RADII);
        PFont font = sketch.createFont("Arial",16,true);
        sketch.textFont(font,16);
        sketch.fill(0,0,0);
        sketch.text(missiles, this.position.x - 8, this.position.y + 30);
        sketch.fill(255,255,255);
    }
}

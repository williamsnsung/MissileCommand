import processing.core.PApplet;
import processing.core.PFont;

public class Ballista extends Infrastructure{
    private int missiles;

    Ballista(float x, float y, float xVel, float yVel, float invM, int score, int radius) {
        super(x, y, xVel, yVel, invM, score, radius);
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

    public void draw(PApplet sketch, boolean active) {
        if (this.missiles == 0) {
            sketch.fill(128,128,128);
            sketch.circle(this.position.x, this.position.y, this.getRadius());
            sketch.fill(255,255,255);
        }
        else if (active) {
            sketch.fill(0,255,0);
            sketch.circle(this.position.x, this.position.y, this.getRadius());
            sketch.fill(255,255,255);
        }
        else {
            sketch.circle(this.position.x, this.position.y, this.getRadius());
        }

        PFont font = sketch.createFont("Arial",16,true);
        sketch.textFont(font,16);
        sketch.fill(0,0,0);
        sketch.text(missiles, this.position.x - 8, this.position.y + 30);
        sketch.fill(255,255,255);
    }

    @Override
    public void die() {
        this.missiles = 0;
    }
}

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

    public void draw(PApplet sketch) {
        sketch.circle(this.position.x, this.position.y, this.getRadius());
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

import processing.core.PApplet;

public class Missile extends GameObject{
    private static float explosionRadius1 = 10;

    Missile(float x, float y, float xVel, float yVel, float invM) {
        super(x, y, xVel, yVel, invM);
    }

    public void explode() {

    }
}

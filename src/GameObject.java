import processing.core.PApplet;
import processing.core.PVector;

// a representation of a point mass
class GameObject {
    protected PVector position, velocity ;
    private PVector forceAccumulator ;
    public final float invMass ;
    private static int idCount = 0;
    private final int id;

    public float getMass() {return 1/invMass ;}

    GameObject(float x, float y, float xVel, float yVel, float invM) {
        position = new PVector(x, y) ;
        velocity = new PVector(xVel, yVel) ;
        forceAccumulator = new PVector(0, 0) ;
        invMass = invM ;
        this.id = idCount++;
    }

    public PVector getPosition() {
        return this.position.copy();
    }

    public int getId() {
        return this.id;
    }

    void addForce(PVector force) {
        forceAccumulator.add(force) ;
    }

    void integrate() {
        // If infinite mass, we don't integrate
        if (invMass <= 0f) return ;

        // update position
        position.add(velocity) ;

        // NB If you have a constant acceleration (e.g. gravity) start with
        //    that then add the accumulated force / mass to that.
        PVector resultingAcceleration = forceAccumulator.copy() ;
        resultingAcceleration.mult(invMass) ;

        // update velocity
        velocity.add(resultingAcceleration) ;

        // Clear accumulator
        forceAccumulator.x = 0 ;
        forceAccumulator.y = 0 ;
    }
}

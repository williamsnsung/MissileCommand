import java.util.ArrayList;
import java.util.Iterator ;
import java.util.LinkedHashMap;

/**
 * Holds all the force generators and the particles they apply to
 */
class ForceRegistry {

    /**
     * Keeps track of one force generator and the particle
     *  it applies to.
     */
    static class ForceRegistration {
        public final GameObject gameObject;
        public final ForceGenerator forceGenerator ;
        ForceRegistration(GameObject p, ForceGenerator fg) {
            gameObject = p ;
            forceGenerator = fg ;
        }
    }

    // Holds the list of registrations
    LinkedHashMap<Integer, ForceRegistration> registrations = new LinkedHashMap<>() ;

    /**
     * Register the given force to apply to the given particle
     */
    void add(GameObject gameObject, ForceGenerator fg) {
        registrations.put(gameObject.getId(), new ForceRegistration(gameObject, fg));
    }

    /**
     * Remove the given registered pair from the registry. If the
     * pair is not registered, this method will have no effect.
     */
    public void remove(GameObject gameObject) {
        registrations.remove(gameObject.getId());
    }

    /**
     * Clear all registrations from the registry
     */
    void clear() {
        registrations.clear() ;
    }

    /**
     * Calls all force generators to update the forces of their
     *  corresponding particles.
     */
    void updateForces() {
        for (Integer id : registrations.keySet()) {
            ForceRegistration fr = registrations.get(id);
            fr.forceGenerator.updateForce(fr.gameObject) ;
        }
    }
}

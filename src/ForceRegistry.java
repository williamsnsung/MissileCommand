import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Holds all the force generators and the particles they apply to
 */
class ForceRegistry {

    // Holds the list of registrations
    LinkedHashMap<GameObject, LinkedList<ForceGenerator>> registrations = new LinkedHashMap<>() ;

    /**
     * Register the given force to apply to the given particle
     */
    void add(GameObject gameObject, ForceGenerator fg) {
        if (!registrations.containsKey(gameObject)) {
            registrations.put(gameObject, new LinkedList<>());
        }
        registrations.get(gameObject).add(fg);
    }

    /**
     * Remove the given registered pair from the registry. If the
     * pair is not registered, this method will have no effect.
     */
    public void remove(GameObject gameObject) {
        registrations.remove(gameObject);
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
        for (GameObject gameObject : registrations.keySet()) {
            for (ForceGenerator fg : registrations.get(gameObject)) {
                fg.updateForce(gameObject);
            }
        }
    }
}

package hierarchy;

/**
 * Concrete {@link Hierarchy} implementation backed by parallel arrays.
 *
 * <p>This class assumes that the provided arrays already satisfy
 * the hierarchy depth invariants.</p>
 */
public class ArrayBasedHierarchy implements Hierarchy {

    private final int[] nodeIds;
    private final int[] depths;

    /**
     * Creates a hierarchy backed by the given arrays.
     *
     * @param nodeIds array of node IDs in DFS order
     * @param depths  array of corresponding depth values
     */
    public ArrayBasedHierarchy(int[] nodeIds, int[] depths) {
        this.nodeIds = nodeIds;
        this.depths = depths;
    }

    @Override
    public int size() {
        return depths.length;
    }

    @Override
    public int nodeId(int index) {
        return nodeIds[index];
    }

    @Override
    public int depth(int index) {
        return depths[index];
    }
}

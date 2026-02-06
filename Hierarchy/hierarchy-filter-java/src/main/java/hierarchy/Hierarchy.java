package hierarchy;

public interface Hierarchy {

    /**
     * @return the total number of nodes in the hierarchy
     */
    int size();

    /**
     * Returns the unique node ID at the given index.
     *
     * @param index index in DFS order (0 ≤ index &lt; size)
     * @return node ID at the given index
     */
    int nodeId(int index);

    /**
     * Returns the depth of the node at the given index.
     *
     * @param index index in DFS order (0 ≤ index &lt; size)
     * @return depth of the node at the given index
     */
    int depth(int index);

    /**
     * Utility method used mainly for testing and debugging.
     *
     * @return a string representation in the form [nodeId:depth, ...]
     */
    default String formatString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(nodeId(i)).append(":").append(depth(i));
        }
        sb.append("]");
        return sb.toString();
    }
}

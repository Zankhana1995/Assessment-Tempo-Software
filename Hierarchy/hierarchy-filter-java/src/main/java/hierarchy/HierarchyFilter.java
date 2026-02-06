package hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

/**
 * Utility class providing filtering operations on {@link Hierarchy}.
 */
public final class HierarchyFilter {

    private HierarchyFilter() {
        // Prevent instantiation
    }

    /**
     * Filters the given hierarchy according to the following rule:
     *
     * <p>A node is included in the result if and only if:</p>
     * <ul>
     *   <li>the node itself satisfies the predicate</li>
     *   <li>all of its ancestors satisfy the predicate</li>
     * </ul>
     *
     * <p>The hierarchy is processed in DFS order and evaluated in a single pass.</p>
     *
     * @param hierarchy the input hierarchy
     * @param predicate predicate applied to node IDs
     * @return a new {@link Hierarchy} containing only the allowed nodes
     */
    public static Hierarchy filter(Hierarchy hierarchy, IntPredicate predicate) {

        List<Integer> resultNodeIds = new ArrayList<>();
        List<Integer> resultDepths = new ArrayList<>();

        // Tracks whether ancestors at each depth were included
        List<Boolean> keptStack = new ArrayList<>();

        for (int i = 0; i < hierarchy.size(); i++) {
            int id = hierarchy.nodeId(i);
            int depth = hierarchy.depth(i);

            // Adjust ancestor stack when moving up the hierarchy
            while (keptStack.size() > depth) {
                keptStack.remove(keptStack.size() - 1);
            }

            // A node can only be included if all ancestors were included
            boolean parentKept = keptStack.stream().allMatch(Boolean::booleanValue);

            // Apply predicate to the current node
            boolean keepCurrent = parentKept && predicate.test(id);

            // Record decision for descendants
            keptStack.add(keepCurrent);

            if (keepCurrent) {
                resultNodeIds.add(id);
                resultDepths.add(depth);
            }
        }

        return new ArrayBasedHierarchy(
                resultNodeIds.stream().mapToInt(Integer::intValue).toArray(),
                resultDepths.stream().mapToInt(Integer::intValue).toArray()
        );
    }
}

package hierarchy;

import java.util.function.IntPredicate;

public final class HierarchyFilterWithArray {

    private HierarchyFilterWithArray() {
        // Prevent instantiation
    }

    // Use HierarchyFilterWithArray in test case to use this class.
    // Purpose : this class is better than HierarchyFilter class as it's using Array
    // Reason : Using Array will remove autoboxing/unboxing cost, no while loop to remove from list or deque as previous program

    public static Hierarchy filter(Hierarchy hierarchy, IntPredicate predicate) {
        int n = hierarchy.size();

        int[] nodeArray = new int[n];
        int[] depthArray = new int[n];
        boolean[] keepStack = new boolean[n];
        int counter = 0;

        for (int i=0; i<n ; i++) {
            int node = hierarchy.nodeId(i);
            int depth = hierarchy.depth(i);

            boolean parentValid = depth==0 || keepStack[depth-1];

            boolean currentValid = parentValid && predicate.test(node);

            keepStack[depth] = currentValid;

            if(currentValid) {
                nodeArray[counter] = node;
                depthArray[counter] = depth;
                counter ++;
            }
        }

        int[] resultNodeArray = new int[counter];
        int[] resultDepthArray = new int[counter];

        System.arraycopy(nodeArray, 0, resultNodeArray, 0, counter);
        System.arraycopy(depthArray, 0, resultDepthArray, 0, counter);

        return new ArrayBasedHierarchy(resultNodeArray, resultDepthArray);

    }
}

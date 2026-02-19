package hierarchy;

public class Utilities {

    public static int countLeaves(Hierarchy h) {
        int count = 0;
        System.out.println("Printing leaves");
        for(int i=0; i< h.size(); i++) {
            int currentDepth = h.depth(i);
            if(i == h.size() - 1 ||
                h.depth(i+1) <= currentDepth) {
                count ++;
                System.out.println(h.nodeId(i) + " ");
            }
        }
        return count;
    }

    public static int findHeight(Hierarchy h) {
        int maxDepth = 0;
        for (int i=0; i< h.size() ; i++) {
            maxDepth = Math.max(maxDepth, h.depth(i));
        }
        return maxDepth;
    }

    // Who has at least one child
    public static int countParents(Hierarchy h) {
        int count = 0;
        System.out.println("Printing Parents");
        for (int i=0; i < h.size()-1 ; i++) {
            count ++;
            System.out.println(h.nodeId(i) + " ");
        }
        return count;
    }

}

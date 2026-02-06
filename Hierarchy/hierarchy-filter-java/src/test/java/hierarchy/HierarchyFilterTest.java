package hierarchy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link HierarchyFilter}.
 */
class HierarchyFilterTest {

    /**
     * Verifies the example provided in the original problem statement.
     * Nodes divisible by 3 are removed, along with their descendants.
     */
    @Test
    void testGivenExample() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );

        Hierarchy actual = HierarchyFilter.filter(
                unfiltered,
                nodeId -> nodeId % 3 != 0
        );

        Hierarchy expected = new ArrayBasedHierarchy(
                new int[]{1, 2, 5, 8, 10, 11},
                new int[]{0, 1, 1, 0, 1, 2}
        );

        assertEquals(expected.formatString(), actual.formatString());
    }

    /**
     * Ensures that when the root node is filtered out,
     * all descendant nodes are also excluded.
     */
    @Test
    void testRootRemovedRemovesAll() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3},
                new int[]{0, 1, 2}
        );

        Hierarchy result = HierarchyFilter.filter(hierarchy, id -> id != 1);

        assertEquals("[]", result.formatString());
    }

    /**
     * Validates correct behavior when the hierarchy contains
     * multiple independent root nodes.
     */
    @Test
    void testMultipleRoots() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4},
                new int[]{0, 1, 0, 1}
        );

        Hierarchy result = HierarchyFilter.filter(hierarchy, id -> id > 2);

        Hierarchy expected = new ArrayBasedHierarchy(
                new int[]{3, 4},
                new int[]{0, 1}
        );

        assertEquals(expected.formatString(), result.formatString());
    }

    /**
     * Confirms that when the predicate always returns true,
     * the hierarchy remains unchanged.
     */
    @Test
    void testKeepAll() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3},
                new int[]{0, 1, 2}
        );

        Hierarchy result = HierarchyFilter.filter(hierarchy, id -> true);

        assertEquals(hierarchy.formatString(), result.formatString());
    }

    /**
     * Confirms that when the predicate always returns false,
     * the resulting hierarchy is empty.
     */
    @Test
    void testRemoveAll() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3},
                new int[]{0, 1, 2}
        );

        Hierarchy result = HierarchyFilter.filter(hierarchy, id -> false);

        assertEquals("[]", result.formatString());
    }
}

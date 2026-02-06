# Hierarchy Filter (Java + Gradle)

## Problem
Filter a DFS-flattened forest so that a node is included
only if it satisfies a predicate and all of its ancestors do as well.

## Approach
The hierarchy is already provided in DFS order.
Depth values allow inferring parent-child relationships without rebuilding the tree.
The solution processes the hierarchy in a single pass (O(n)).

## Testing
Tests cover:
- Provided example
- Root removal
- Multiple independent trees
- All-pass and all-fail predicates

## How to Run
```bash
./gradlew test
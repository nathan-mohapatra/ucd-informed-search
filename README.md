# UCD Informed Search
For this assignment, I used Java to implement the A* search algorithm which, given a three-dimensional grid, finds the shortest path from a startpoint to an endpoint. I began by implementing Dijkstra's algorithm, a benchmark that yields the optimal path. Then, I devised (and proved the admissibility of) admissible heuristics that optimized the cost and runtime of Dijkstra's algorithm, resulting in the A* search algorithm.

`html` directory contains various HTML files, all of which provide a brief overview of instructions and starter code for this assignment.  
`report1.pdf` is a written report of this assignment.

---

I first familiarized myself with `TerrainMap`, a class encapsulating a two-dimensional world layered on top of a rectangular grid. Each point in the world has a height, represented by an integer value between 0 and 255. You can move to any of the eight tiles adjacent to your own location (e.g. the four cardinal directions and the diagonals). The cost to traverse between tiles is dependent on the differences in height between the tiles.

`TerrainMap` also keeps track of which tiles an algorithm visits as it looks for an optimal path. The goal is to optimize the number of tiles visited by the algorithm (many of the better search algorithms that visit fewer tiles also run in considerably less time).

The assignment is to create an implementation of the `AIModule` interface that computes a path from the startpoint to the endpoint while minimizing the total search space. Once this function is written, it can be ran with the existing starter code by compiling the module and specifying it as a command-line parameter to the main program. For example, if you have written an AI module called PerfectAI, you can run `java Main PerfectAI`. This will run your AI module, print its performance measures and number of visited tiles to `stdout`, and create a display window showing the terrain, the tiles visited by your AI module (search space), and the path computed.

## Part 0: Dijkstra's Algorithm
Dijkstra's algorithm is an algorithm for finding the shortest paths between nodes in a graph (which may represent, for example, road networks). It uses a data structure for storing and querying partial solutions sorted by distance from the startpoint; in my implementation I used a priority queue, ordered by the cost of the path, and a hash map containing visited tiles and their attributes.

Pseudocode for potential implementation of Dijkstra's algorithm:  
```
function DIJKSTRAS(problem) returns a solution or failure
    node = a node with STATE = problem. INITIAL-STATE, PATH-COST = 0
    frontier = a priority queue ordered by PATH-COST, with node as the only element
    explored = an empty set
    loop do
        if EMPTY(frontier) then return failure
        node = POP(frontier) // chooses lowest-cost node in frontier
        if problem.GOAL-TEST(node.STATE) then return SOLUTION(node)
        add node.STATE to explored
        for each action in problem.ACTIONS(node.STATE) do
            child = CHILD-NODE(problem, node, action)
            if child.STATE is not in explored or frontier then
                frontier = INSERT(child, frontier)
            else if child.STATE is in frontier with higher PATH-COST then
                replace that frontier node with child
```

## Part 1: Creating Heuristics
Cost functions:  
```
// exponential of the height difference
public double getCost(final Point p1, final Point p2) {
    return Math.exp(getTile(p2) - getTile(p1));
}

// new height divided by old height
public double getCost(final Point p1, final Point p2) {
    return 1.0 * getTile(p2) / (getTile(p1) + 1);
}
```

A heuristic function *h(n)* estimates the cost of the cheapest path from the state at node *n* to a goal state, and is the most common form in which additional knowledge of the problem is imparted to the search algorithm. A heuristic is admissible if it never overestimates the cost to reach the goal. Since *g(n)* is the actual cost to reach *n* along the current path, and *f(n) = g(n) + h(n)*, we have as an immediate consequence that *f(n)* never overestimates the true cost of a solution along the current path through *n*.

For each cost function above, I created an admissible heuristic, documented its exact form, and proved/showed that it is, indeed, admissible (see the report for these details). These admissible heuristics will be referred to as `AStarDiv` and `AStarExp`, respectively.

## Part 2: Implementing Heuristics and A* Search Algorithm
My implementation of the A* search algorithm and admissible heuristics is in the files `AStarDiv_914862981.java` and `AStarExp_914862981.java`. While their implementation of Dijkstra's algorithm does not differ, the `getHeuristic` function is an implementation of the admissible heuristic that I devised for that specific cost function.

The `createPath` function is essentially Dijkstra's algorithm; however, it calls the `getHeuristic` function in its override of the priority queue comparator. Thus, when ordering computed paths by their costs, the priority queue accounts for the heuristic (i.e. *f(n) = g(n) + h(n)*). It is this detail that transforms the algorithm into the A* search algorithm.

## Part 3: Trying Algorithm on Small Problem
I tried out my heuristic functions with the appropriate cost function on 500x500 maps with random seeds 1, 2, 3, 4, and 5.  
Use the following command to do this: `java Main YourAIModule -seed x` with x being one of the seeds listed above.

For each execution I recorded the cost of the shortest path and the number of tiles visited as per the output of the program. To demonstrate the improvements made, here is a comparison between Dijkstra's and A* for the exponential cost function:

Dijkstra's (Exp)
|        |      PathCost      | Uncovered | TimeTaken |
|:------:|:------------------:|:---------:|:---------:|
| Seed 1 | 533.4482191461119  |  226914   |   12407   |
| Seed 2 | 549.5036346739352  |  237620   |   12420   |
| Seed 3 | 510.97825243663607 |  228697   |   11165   |
| Seed 4 | 560.6570436319696  |  216438   |   11344   |
| Seed 5 | 479.5879215923168  |  220673   |   10594   |

AStarExp
|        |      PathCost      | Uncovered | TimeTaken |
|:------:|:------------------:|:---------:|:---------:|
| Seed 1 | 533.4482191461119  |   72625   |   2849    |
| Seed 2 | 549.5036346739352  |   82171   |   3797    |
| Seed 3 | 510.97825243663607 |   75432   |   3323    |
| Seed 4 | 560.6570436319696  |   68869   |   2386    |
| Seed 5 | 479.5879215923168  |   69895   |   2523    |

While they both compute the same optimal path, the A* search algorithm does by searching a significantly smaller space in significantly less time. This confirms the validity and effectiveness of the admissible heuristic I devised.

## Part 4: "Climbing" Mount Saint Helens
A much larger grid necessitates a more cleverly implemented algorithm. I modified both my A* search algorithm and my admissible heuristic so as to find the optimal path in this new environment in the least possible time. For this part, I was asked only to consider the division cost function.  
Use the following command to try the larger grid: `java Main YourAIModule -load MTAFT.XYZ`.

`MtStHelensDiv_914862981` is a modified `AStarDiv_914862981`, with two optimizations:  
1. I implemented the *weighted* A* search algorithm by multiplying the heuristic by a constant factor, a weight *w* > 1, magnifying the heuristic's effect.
2. I modified my implementation of Dijkstra's algorithm such that there are no duplicate entries added to the priority queue (since, when iterating through the neighbors of a node that has been popped off of the priority queue, it is possible to encounter a node that has been reached previously, but at a different cost).  
Together, these two optimizations lowered the average runtime of the algorithm.

The University of California, Davis  
Winter Quarter 2021  
[ECS 170] Artificial Intelligence (w/ Professor Davidson)

# Purpose of Repo
For future reference, this repo will store the files that constitute my first project in ECS 170. Additionally, by creating this repo (and others with similar content), I am familiarizing myself with Git and GitHub.

# Description of Project
For my first project in ECS 170, I used Java to implement the A* search algorithm which, given a three-dimensional grid, finds the shortest path from a startpoint to an endpoint. I began by implementing Dijkstra's algorithm, a benchmark that yields the optimal path. Then, I devised (and proved the admissibility of) admissible heuristics that optimized the cost and runtime of Dijkstra's algorithm, resulting in the A* search algorithm.

I first worked with the **TerrainMap** class, a class encapsulating a two-dimensional world layered on top of a rectangular grid. Each point in the world has a height, represented by an integer value between 0 and 255. You can move to any of the eight squares adjacent to your own location (e.g. the four cardinal directions and the diagonals). The cost to traverse between tiles is dependent on the differences in height between the tiles.

The **TerrainMap** class also keeps track of which tiles an algorithm visits as it looks for an optimal path. The goal is to optimize the number of tiles visited by the algorithm (many of the better search algorithms that visit fewer squares also run in considerably less time).

## Part 1: Creating Heuristics
Cost functions:  
- Exponential of the height difference
- New height divided by old height

For each cost function above for the chess movement (eight neighbors), I created an admissible heuristic, documented the exact form of the heuristic, and proved/showed it is admissible.

## Part 2: Implementing Heuristics and A* Algorithm
I implemented my own version of A*. The **DirectAI** and **StupidAI** classes demonstrate how to search the state space.

> The heuristic is implemented in the `getHeuristic` function.

## Part 3: Trying Algorithm on Small Problem
Tried out my heuristic functions with the appropriate cost function on 500x500 maps with random seeds 1, 2, 3, 4, and 5.

> Use the following command to do this: `java Main YourAIModule -seed x` with x being one of the seeds listed above.

For each execution I recorded the cost of the shortest path and the number of tiles visited as per the output of the program.

## Part 4: "Climbing" Mount Saint Helens
A much larger grid necessitates a more cleverly implemented algorithm. I modified both my A* algorithm and admissible heuristic as as to find the optimal path in this new environment in the least possible time. I needed only to consider the chess movement (eight neighbors) and "New height divided by old height" cost function.

> Use the following command to try the larger grid: `java Main YourAIModule -load MTAFT.XYZ`.

### Submission
- **report1.pdf**: A clear and concise description of my modified A* algorithm and admissible heuristic with proof of admissibility; the cost of my shortest path, number of tiles visited, and time to find it for seeds 1, 2, 3, 4, and 5 for both **AStarExp** and **AStarDiv**
- The implementation of my modified A* algorithm and admissible heuristic
    - **AStarExp_914862981.java**
    - **AStarDiv_914862981.java**
    - **MtStHelensDiv_914862981.java**

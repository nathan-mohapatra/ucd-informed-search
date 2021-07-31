import java.awt.Point;
import java.util.*;

public class AStarExp_914862981 implements AIModule {

    private double getHeuristic(final TerrainMap map, final Point pt) {

        // Distance between current point and end point
        final Point endPoint = map.getEndPoint();
        final int dx = Math.abs(pt.x - endPoint.x);
        final int dy = Math.abs(pt.y - endPoint.y);

        // Best-case path depends on difference in height, between current point and end point
        final double height1 = map.getTile(pt);
        final double height2 = map.getTile(endPoint);

        // Case 1: Current point level with end point, stay at same height
        double heuristic = Math.max(dx, dy); // Chebyshev distance
        // Case 2: Current point is below end point, take small steps up then remain at same height
        if (height1 < height2) {
            for (int i = 0; i < Math.abs(height1 - height2); i++) {
                heuristic -= 1;
                heuristic += Math.exp(1);
            }
        // Case 3: Current point is above end point, take large step down then remain at same height
        } else if (height1 > height2) {
            heuristic -= Math.abs(height1 - height2);
            heuristic += 1 / Math.exp(Math.abs(height1 - height2));
        }

        return heuristic;
    }

    // Creates the path to the goal.
    public List<Point> createPath(final TerrainMap map) {

        // DIJKSTRA'S ALGORITHM
        // Initialize path to goal, start point, end point
        final ArrayList<Point> path = new ArrayList<Point>();
        final Point startPoint = map.getStartPoint();
        final Point endPoint = map.getEndPoint();

        // A lookup table, key:value pair between Point and Node (attributes: visited, cost, parent)
        final HashMap<Point, Node> reached = new HashMap<Point, Node>();
        reached.put(new Point(startPoint), new Node(false, 0.0, null));

        // A priority queue ordered by f(n) = g(n) + h(n)
        final PriorityQueue<Point> frontier = new PriorityQueue<Point>(new Comparator<Point>() {
            @Override // Define comparator, compare pair of points in priority queue
            public int compare(Point p1, Point p2) {
                final Point parent1 = reached.get(p1).parent;
                final Point parent2 = reached.get(p2).parent;
                double heuristic1 = getHeuristic(map, p1);
                double heuristic2 = getHeuristic(map, p2);
                double eval1 = reached.get(parent1).cost + map.getCost(parent1, p1) + heuristic1;
                double eval2 = reached.get(parent2).cost + map.getCost(parent2, p2) + heuristic2;
                return Double.compare(eval1, eval2);
            }
        });
        frontier.add(new Point(startPoint));

        // Iterate through priority queue
        while (!frontier.isEmpty()) {
            // Highest priority / lowest cost point becomes current point
            final Point currentPoint = frontier.remove();
            reached.get(currentPoint).visited = true;

            // Check if current point is end point
            if (currentPoint.equals(endPoint))
                break;

            // Iterate through children of current point
            Point[] children = map.getNeighbors(currentPoint);
            for (Point child : children) {
                double childCost = reached.get(currentPoint).cost + map.getCost(currentPoint, child);

                // If child has never been reached OR if child has been reached before at a greater cost
                if (!reached.containsKey(child) || childCost < reached.get(child).cost) {
                    reached.put(new Point(child), new Node(false, childCost, currentPoint));
                    // TODO: Replace duplicate entry in priority queue
                    frontier.add(new Point(child));
                }
            }
        }

        // Backtrack from end point
        Point currentPoint = endPoint;
        path.add(new Point(currentPoint));
        // Add to path until start point is added
        while (!path.contains(startPoint)) {
            path.add(new Point(reached.get(currentPoint).parent));
            currentPoint = reached.get(currentPoint).parent;
        }
        // Reverse order of points to correct path
        Collections.reverse(path);

        // We're done! Hand it back.
        return path;
    }

    // Node class stores information about Point objects for HashMap
    private class Node {

        boolean visited; // Might not be needed
        double cost;
        Point parent;

        public Node(boolean visited, double cost, Point parent) {
            this.visited = visited;
            this.cost = cost;
            this.parent = parent;
        }
    }
}

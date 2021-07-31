import java.awt.Point;
import java.util.*;

public class MtStHelensDiv_914862981 implements AIModule {

    private double getHeuristic(final TerrainMap map, final Point pt) {

        // Distance between current point and end point
        final Point endPoint = map.getEndPoint();
        final int dx1 = Math.abs(pt.x - endPoint.x);
        final int dy1 = Math.abs(pt.y - endPoint.y);
        // Distance between start point and end point
        final int dx2 = Math.abs(map.getStartPoint().x - endPoint.x);
        final int dy2 = Math.abs(map.getStartPoint().y - endPoint.y);

        // Cross product gives preference to paths along straight line between start point and end point
        int crossProduct = Math.abs((dx1 * dy2) - (dx2 * dy1));
        final double MULTIPLIER = 1.0; // constant to be adjusted

        // Calculate heuristic, estimate cost w/ distance
        final double cost = map.getCost(pt, endPoint);
        double heuristic = cost * (dx1 + dy1) + (cost - (2 * cost)) * Math.min(dx1, dy1); // Chebyshev distance

        // Modification: Weighted A* w/ constant factor of 2
        final double WEIGHT = 2.0;
        return (heuristic + (crossProduct * MULTIPLIER)) * WEIGHT;
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

                // If child has never been reached
                if (!reached.containsKey(child)) {
                    reached.put(new Point(child), new Node(false, childCost, currentPoint));
                    frontier.add(new Point(child));
                // If child has been reached before at a greater cost
                } else if (childCost < reached.get(child).cost) {
                    // Modification: Replace duplicate entry in priority queue
                    frontier.remove(child);
                    reached.put(new Point(child), new Node(false, childCost, currentPoint));
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

import java.util.*;

public class RoutePlanner {

    private final Map<String, Vertex> graph;

    public RoutePlanner(Map<String, Vertex> graph) {
        this.graph = graph;
    }

    public void RoutePlanning() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter origin station: ");
        String originName = scanner.nextLine();
        System.out.print("Enter destination station: ");
        String destName = scanner.nextLine();

        Vertex originVertex = findVertexByName(originName);
        Vertex destVertex = findVertexByName(destName);

        if (originVertex == null || destVertex == null) {
            System.out.println("One or Both stations do not exist.");
            return;
        }

        List<String> path = BFS_findRoute(graph, originVertex.stationID, destVertex.stationID);

        if (path.isEmpty()) {
            System.out.println("No available route between " + originName + "and " + destName);
            return;
        }

        double totalTime = CalculateTotalTime(path);
        DisplayRoute(path, totalTime);
    }

    public List<String> BFS_findRoute(Map<String, Vertex> graph, String originID, String destinationID) {
        Map<String, Double> distance = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        PriorityQueue<PQItem> PQ = new PriorityQueue<>(Comparator.comparingDouble(a -> a.time));

        for (Vertex vertex : graph.values()) {
            distance.put(vertex.stationID, Double.POSITIVE_INFINITY);
            parent.put(vertex.stationID, null);
        }

        distance.put(originID, 0.0);
        PQ.add(new PQItem(originID, 0.0));

        while (!PQ.isEmpty()) {
            PQItem item = PQ.poll();
            String currentID = item.id;
            double currentTime = item.time;

            if (currentID.equals(destinationID)) {
                break;
            }

            if (currentTime > distance.get(currentID)) {
                continue;
            }

            Vertex currentVertex = findVertexByID(currentID);
            if (currentVertex == null) continue;
            if (!currentVertex.isWorking) continue;

            for (Edge edge : currentVertex.edge) {
                if (!edge.isActive) continue;

                Vertex neighborVertex = findVertexByID(edge.destID);

                if (neighborVertex == null) {
                    continue;
                }

                if (!neighborVertex.isWorking) continue;

                double newTime = distance.get(currentID) + edge.timeCost;

                if (newTime < distance.get(edge.destID)) {
                    distance.put(edge.destID, newTime);
                    parent.put(edge.destID, currentID);
                    PQ.add(new PQItem(edge.destID, newTime));
                }
            }
        }

        if (distance.get(destinationID) == Double.POSITIVE_INFINITY) {
            return new ArrayList<>();
        }

        return ReconstructPath(parent, originID, destinationID);
    }

    public List<String> ReconstructPath(Map<String, String> parent, String originID, String destinationID) {
        List<String> path = new ArrayList<>();
        String currentID = destinationID;

        while (!currentID.equals(originID)) {
            path.add(0, currentID);
            currentID = parent.get(currentID);
        }

        path.add(0, originID);
        return path;
    }

    public double CalculateTotalTime(List<String> path) {
        double totalTime = 0;

        for (int i = 0; i <= path.size() - 2; i++) {
            Vertex currentVertex = findVertexByID(path.get(i));
            if (currentVertex == null) continue;
            Edge edge = findEdgeByDestID(currentVertex, path.get(i + 1));
            if (edge == null) continue;
            totalTime = totalTime + edge.timeCost;
        }
        return totalTime;
    }

    public void DisplayRoute(List<String> path, double totalTime) {
        System.out.println("Best route found: ");

        for (int i = 0; i <= path.size() - 1; i++) {
            Vertex stationVertex = findVertexByID(path.get(i));
            if (stationVertex == null) continue;

            if (i == path.size() - 1) {
                System.out.println("stationVertex.stationName");
            } else {
                System.out.println("stationVertex.stationName —>");
            }

            if (!stationVertex.isWorking) {
                System.out.println("Note: " + stationVertex.stationName + " is under maintenance");
            }
        }
        System.out.println("Estimated Arrival Time: " + totalTime + " minutes");
    }

    private Vertex findVertexByName(String name) {
        for (Vertex v : graph.values()) {
            if (v.stationName.equals(name)) return v;
        }
        return null;
    }

    private Vertex findVertexByID(String id) {
        for (Vertex v : graph.values()) {
            if (v.stationID.equals(id)) return v;
        }
        return null;
    }

    private Edge findEdgeByDestID(Vertex vertex, String destID) {
        for (Edge e : vertex.edge) {
            if (e.destID.equals(destID)) return e;
        }
        return null;
    }

    private static class PQItem {
        String id;
        double time;

        PQItem(String id, double time) {
            this.id = id;
            this.time = time;
        }
    }
}
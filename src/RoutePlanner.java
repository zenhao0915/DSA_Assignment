import java.util.*;

public class RoutePlanner {

    public void routePlanning(Map<String, Vertex> graph) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(Main.YELLOW + "Enter origin station ID: " + Main.RESET);
        String originName = scanner.nextLine().trim();
        System.out.print(Main.YELLOW + "Enter destination station ID: " + Main.RESET);
        String destName = scanner.nextLine().trim();

        if (originName.equalsIgnoreCase(destName)) {
            System.out.println(Main.RED + "[Error] Duplicated Station Found!" + Main.RESET);
            return;
        }

        Vertex originVertex = findVertexByID(graph, originName);
        Vertex destVertex = findVertexByID(graph, destName);

        if (originVertex == null || destVertex == null) {
            System.out.println(Main.RED + "[Error] One or Both stations do not exist." + Main.RESET);
            return;
        }

        if (!originVertex.isWorking && !destVertex.isWorking) {
            System.out.println(Main.RED + "[Notice] Both origin station (" + originVertex.stationName + ") and destination station (" + destVertex.stationName + ") are under maintenance!" + Main.RESET);
            return;
        }
        if (!originVertex.isWorking) {
            System.out.println(Main.RED + "[Notice] Origin station " + originVertex.stationName + " (" + originName + ") is currently under maintenance!" + Main.RESET);
            return;
        }
        if (!destVertex.isWorking) {
            System.out.println(Main.RED + "[Notice] Destination station " + destVertex.stationName + " (" + destName + ") is currently under maintenance!" + Main.RESET);
            return;
        }

        List<String> path = findRouteByBFS(graph, originVertex.stationID, destVertex.stationID);

        if (path.isEmpty()) {
            System.out.println(Main.RED + "No available route between " + originName + " and " + destName + " (Transit tracks closed or intermediate stations under maintenance)." + Main.RESET);
            return;
        }

        double totalTime = calculateTotalTime(graph, path);
        displayRoute(graph, path, totalTime);
    }

    public List<String> findRouteByBFS(Map<String, Vertex> graph, String originID, String destinationID) {
        Vertex startNode = findVertexByID(graph, originID);
        Vertex endNode = findVertexByID(graph, destinationID);

        if (startNode == null || endNode == null) {
            return new ArrayList<>();
        }

        String canonicalOriginID = startNode.stationID;
        String canonicalDestID = endNode.stationID;

        Map<String, Double> distance = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        PriorityQueue<PQItem> PQ = new PriorityQueue<>(Comparator.comparingDouble(a -> a.time));

        for (Vertex vertex : graph.values()) {
            distance.put(vertex.stationID, Double.POSITIVE_INFINITY);
            parent.put(vertex.stationID, null);
        }

        distance.put(canonicalOriginID, 0.0);
        PQ.add(new PQItem(canonicalOriginID, 0.0));

        while (!PQ.isEmpty()) {
            PQItem item = PQ.poll();
            String currentID = item.id;
            double currentTime = item.time;

            if (currentID.equalsIgnoreCase(canonicalDestID)) {
                break;
            }

            if (currentTime > distance.get(currentID)) {
                continue;
            }

            Vertex currentVertex = findVertexByID(graph, currentID);
            if (currentVertex == null || !currentVertex.isWorking || currentVertex.edge == null) continue;

            for (Edge edge : currentVertex.edge) {
                if (!edge.isActive) continue;

                Vertex neighborVertex = findVertexByID(graph, edge.destID);
                if (neighborVertex == null || !neighborVertex.isWorking) continue;

                String neighborCanonicalID = neighborVertex.stationID;
                double newTime = distance.get(currentID) + edge.timeCost;

                if (newTime < distance.get(neighborCanonicalID)) {
                    distance.put(neighborCanonicalID, newTime);
                    parent.put(neighborCanonicalID, currentID);
                    PQ.add(new PQItem(neighborCanonicalID, newTime));
                }
            }
        }

        if (distance.get(canonicalDestID) == Double.POSITIVE_INFINITY) {
            return new ArrayList<>();
        }

        return reconstructPath(parent, canonicalOriginID, canonicalDestID);
    }

    public List<String> reconstructPath(Map<String, String> parent, String originID, String destinationID) {
        List<String> path = new ArrayList<>();
        String currentID = destinationID;

        while (currentID != null && !currentID.equalsIgnoreCase(originID)) {
            path.add(0, currentID);
            currentID = parent.get(currentID);
        }

        if (currentID != null) {
            path.add(0, originID);
        }
        return path;
    }

    public double calculateTotalTime(Map<String, Vertex> graph, List<String> path) {
        double totalTime = 0;

        for (int i = 0; i <= path.size() - 2; i++) {
            Vertex currentVertex = findVertexByID(graph, path.get(i));
            if (currentVertex == null) continue;
            Edge edge = findEdgeByDestID(currentVertex, path.get(i + 1));
            if (edge == null) continue;
            totalTime += edge.timeCost;
        }
        return totalTime;
    }

    public void displayRoute(Map<String, Vertex> graph, List<String> path, double totalTime) {
        System.out.println(Main.GREEN + "\nBest route found: " + Main.RESET);

        for (int i = 0; i <= path.size() - 1; i++) {
            Vertex stationVertex = findVertexByID(graph, path.get(i));
            if (stationVertex == null) continue;

            if (i == path.size() - 1) {
                System.out.println(Main.BLUE + stationVertex.stationName + Main.RESET);
            } else {
                System.out.print(Main.BLUE + stationVertex.stationName + Main.RESET + " -> ");
            }
        }
        System.out.println("Estimated Arrival Time: " + Main.YELLOW + totalTime + " minutes\n" + Main.RESET);
    }

    private Vertex findVertexByName(Map<String, Vertex> graph, String name) {
        if (name == null) return null;
        for (Vertex v : graph.values()) {
            if (v.stationName != null && v.stationName.equalsIgnoreCase(name)) return v;
        }
        return null;
    }

    private Vertex findVertexByID(Map<String, Vertex> graph, String id) {
        if (id == null) return null;
        for (Vertex v : graph.values()) {
            if (v.stationID != null && v.stationID.equalsIgnoreCase(id)) return v;
        }
        return null;
    }

    private Edge findEdgeByDestID(Vertex vertex, String destID) {
        if (vertex == null || vertex.edge == null || destID == null) return null;
        for (Edge e : vertex.edge) {
            if (e.destID != null && e.destID.equalsIgnoreCase(destID)) return e;
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
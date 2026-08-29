import java.util.*;

public class Graph {
    public static Map<String, Vertex> graphMap = new HashMap<>();

    public boolean addVertex(String stationID, String stationName, boolean isWorking) {

        if (graphMap.values().stream().anyMatch(v -> v.stationID.equalsIgnoreCase(stationID))) {
            System.out.println("[Error] Station Already Exists!");
            return false;
        }
        if (stationID.isEmpty()) {
            System.out.println("[Error] Station ID Is Empty!");
            return false;
        }

        if (stationName.isEmpty()){
            System.out.println("[Error] Station Name Is Empty!");
            return false;
        }

        if (!isWorking){
            System.out.println("[Error] Status Is Empty!");
            return false;
        }
        graphMap.put(stationID, new Vertex(stationID, stationName, isWorking, new ArrayList<>()));
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Station Added Successfully!");
        return true;
    }

    public boolean addEdge(String stationID, String destID, int time) {
        // 1. Check for empty inputs FIRST
        if (stationID.isEmpty()) {
            System.out.println("[Error] Starting Station ID Is Empty!");
            return false;
        }
        if (destID.isEmpty()) {
            System.out.println("[Error] Destination Station ID Is Empty!");
            return false;
        }

        // 2. Safely find the actual case-matching keys in the graphMap
        String actualStationID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(stationID))
                .findFirst()
                .orElse(null);

        String actualDestID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(destID))
                .findFirst()
                .orElse(null);

        // 3. Now check if they actually exist
        if (actualStationID == null || actualDestID == null) {
            System.out.println("[Error] One/Two Of The Station Do Not Exists!");
            return false;
        }

        // 4. Create edges using the safely matched IDs
        Edge toDest = new Edge(actualDestID, time, true);
        Edge toSource = new Edge(actualStationID, time, true);

        List<Edge> stationIDEdge = graphMap.get(actualStationID).edge;
        List<Edge> destIDEdge = graphMap.get(actualDestID).edge;

        if (stationIDEdge == null) graphMap.get(actualStationID).edge = new ArrayList<>();
        if (destIDEdge == null) graphMap.get(actualDestID).edge = new ArrayList<>();

        graphMap.get(actualStationID).edge.add(toDest);
        graphMap.get(actualDestID).edge.add(toSource);

        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Connection Successfully Created!");
        return true;
    }

    public boolean removeVertex(String stationID) {
        if (stationID.isEmpty()) {
            System.out.println("[Error] Station ID Is Empty!");
            return false;
        }

        String actualStationID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(stationID))
                .findFirst()
                .orElse(null);

        if (actualStationID == null) {
            System.out.println("[Error] Station Does Not Exists!");
            return false;
        }

        for (Vertex v : graphMap.values()) {
            if (v.edge != null) {
                v.edge.removeIf(e -> e.destID.equalsIgnoreCase(actualStationID));
            }
        }

        graphMap.remove(actualStationID);

        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Station And Associated Connections Deleted Successfully!");
        return true;
    }

    public void removeEdge(String stationID, String destID) {
        if (stationID.isEmpty()) {
            System.out.println("[Error] Starting Station ID Is Empty!");
            return;
        }
        if (destID.isEmpty()) {
            System.out.println("[Error] Destination Station ID Is Empty!");
            return;
        }

        String actualStationID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(stationID))
                .findFirst()
                .orElse(null);

        String actualDestID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(destID))
                .findFirst()
                .orElse(null);

        if (actualStationID == null) {
            System.out.println("[Error] Starting Station Does Not Exist!");
            return;
        }
        if (actualDestID == null) {
            System.out.println("[Error] Destination Station Does Not Exist!");
            return;
        }

        boolean edgeRemoved = false;

        if (graphMap.get(actualStationID).edge != null) {

            if (graphMap.get(actualStationID).edge.removeIf(e -> e.destID.equalsIgnoreCase(actualDestID))) {
                edgeRemoved = true;
            }
        }
        if (graphMap.get(actualDestID).edge != null) {
            if (graphMap.get(actualDestID).edge.removeIf(e -> e.destID.equalsIgnoreCase(actualStationID))) {
                edgeRemoved = true;
            }
        }

        if (!edgeRemoved) {
            System.out.println("[Error] No Connection Exists Between These Two Stations!");
            return;
        }

        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Connection Successfully Removed!");
    }

    public void updateEdgeStatus(String stationID, boolean isWorking) {
        if (stationID.isEmpty()) {
            System.out.println("[Error] Station ID Is Empty!");
            return;
        }
        for (Vertex v : graphMap.values()) {
            // Updated to use equalsIgnoreCase
            if (!v.stationID.equalsIgnoreCase(stationID)) continue;
            if (v.edge == null) continue;
            v.edge.forEach(edge -> edge.isActive = isWorking);
        }
        graphMap.forEach((k, vertex) -> {
            if (vertex.edge == null) return;
            for (Edge edge : vertex.edge) {
                // Updated to use equalsIgnoreCase
                if (edge.destID.equalsIgnoreCase(stationID)) edge.isActive = isWorking;
            }
        });
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Update Successful!");
    }

    public boolean updateStatus(String stationID, boolean isWorking) {
        if (stationID.isEmpty()) {
            System.out.println("[Error] Station ID Is Empty!");
            return false;
        }

        // Safely capture the exact key to prevent null errors on case mismatch
        String actualStationID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(stationID))
                .findFirst()
                .orElse(null);

        if (actualStationID == null) {
            System.out.println("[Error] Station Does Not Exists!");
            return false;
        }

        // Use the safely captured actualStationID
        Vertex currentVertex = graphMap.get(actualStationID);
        currentVertex.isWorking = isWorking;

        // Pass the safe ID down to the edge updater as well
        updateEdgeStatus(actualStationID, isWorking);
        return true;
    }
}

class Edge {
    public String destID;
    public int timeCost;
    public boolean isActive;

    public Edge(String destID, int timeCost, boolean isActive) {
        this.destID = destID;
        this.timeCost = timeCost;
        this.isActive = isActive;
    }

    public String getIDByName(Collection<Vertex> vertex) {
        // Updated to use equalsIgnoreCase
        return Objects.requireNonNull(vertex.stream()
                .filter(v -> v.stationID.equalsIgnoreCase(destID))
                .findFirst()
                .orElse(null)).stationName;
    }
}
class Vertex {
    public String stationID;
    public String stationName;
    public boolean isWorking;
    public List<Edge> edge;

    public Vertex(String stationID, String stationName, boolean isWorking, List<Edge> edge) {
        this.stationID = stationID;
        this.stationName = stationName;
        this.isWorking = isWorking;
        this.edge = edge;
    }
}

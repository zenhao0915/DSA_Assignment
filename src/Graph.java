import java.util.*;

public class Graph {
    public static Map<String, Vertex> graphMap = new HashMap<>();

    public boolean addVertex(String stationID, String stationName, boolean isWorking) {
        if (stationID.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station ID Is Empty!" + Main.RESET);
            return false;
        }
        if (stationName.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station Name Is Empty!" + Main.RESET);
            return false;
        }
        if (graphMap.values().stream().anyMatch(v -> v.stationID.equalsIgnoreCase(stationID))) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station Already Exists!" + Main.RESET);
            return false;
        }
        if (graphMap.values().stream().anyMatch(v -> v.stationName.equalsIgnoreCase(stationName))) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station Name Already Exists!" + Main.RESET);
            return false;
        }
//        if (!isWorking) {
//            System.out.println("[Error] Status Is Empty!");
//            return false;
//        }
        graphMap.put(stationID, new Vertex(stationID, stationName, isWorking, new ArrayList<>()));
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println(Main.BOLD + Main.GREEN + "[Success] Station Added Successfully!" + Main.RESET);
        return true;
    }

    public boolean addEdge(String stationID, String destID, int time) {
        if (stationID.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Starting Station ID Is Empty!" + Main.RESET);
            return false;
        }
        if (destID.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Destination Station ID Is Empty!" + Main.RESET);
            return false;
        }

        String actualStationID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(stationID))
                .findFirst()
                .orElse(null);

        String actualDestID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(destID))
                .findFirst()
                .orElse(null);

        if (actualStationID == null || actualDestID == null) {
            System.out.println(Main.BOLD + Main.RED + "[Error] One/Two Of The Station Do Not Exists!" + Main.RESET);
            return false;
        }

        if (actualStationID.equalsIgnoreCase(actualDestID)) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Cannot connect a station to itself!" + Main.RESET);
            return false;
        }

        Vertex sourceVertex = graphMap.get(actualStationID);
        Vertex destVertex = graphMap.get(actualDestID);

        if (sourceVertex.edge == null) sourceVertex.edge = new ArrayList<>();
        if (destVertex.edge == null) destVertex.edge = new ArrayList<>();

        boolean alreadyExists = sourceVertex.edge.stream()
                .anyMatch(e -> e.destID.equalsIgnoreCase(actualDestID));
        if (alreadyExists) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Connection between these stations already exists!" + Main.RESET);
            return false;
        }
        boolean isEdgeActive = sourceVertex.isWorking && destVertex.isWorking;

        Edge toDest = new Edge(actualDestID, time, isEdgeActive);
        Edge toSource = new Edge(actualStationID, time, isEdgeActive);

        sourceVertex.edge.add(toDest);
        destVertex.edge.add(toSource);

        FileManager.INSTANCE.saveGraphToFile();
        System.out.println(Main.BOLD + Main.GREEN + "[Success] Connection Successfully Created!" + Main.RESET);
        return true;
    }

    public boolean removeVertex(String stationID) {
        if (stationID.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station ID Is Empty!" + Main.RESET);
            return false;
        }

        String actualStationID = graphMap.keySet().stream().filter(k -> k.equalsIgnoreCase(stationID)).findFirst().orElse(null);

        if (actualStationID == null) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station Does Not Exists!" + Main.RESET);
            return false;
        }

        for (Vertex v : graphMap.values()) {
            if (v.edge != null) {
                v.edge.removeIf(e -> e.destID.equalsIgnoreCase(actualStationID));
            }
        }

        graphMap.remove(actualStationID);

        FileManager.INSTANCE.saveGraphToFile();
        System.out.println(Main.BOLD + Main.GREEN + "[Success] Station And Associated Connections Deleted Successfully!" + Main.RESET);
        return true;
    }

    public void removeEdge(String stationID, String destID) {
        if (stationID.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Starting Station ID Is Empty!" + Main.RESET);
            return;
        }
        if (destID.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Destination Station ID Is Empty!" + Main.RESET);
            return;
        }

        String actualStationID = graphMap.keySet().stream().filter(k -> k.equalsIgnoreCase(stationID)).findFirst().orElse(null);

        String actualDestID = graphMap.keySet().stream().filter(k -> k.equalsIgnoreCase(destID)).findFirst().orElse(null);

        if (actualStationID == null) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Starting Station Does Not Exist!" + Main.RESET);
            return;
        }
        if (actualDestID == null) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Destination Station Does Not Exist!" + Main.RESET);
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
            System.out.println(Main.BOLD + Main.RED + "[Error] No Connection Exists Between These Two Stations!" + Main.RESET);
            return;
        }

        FileManager.INSTANCE.saveGraphToFile();
        System.out.println(Main.BOLD + Main.GREEN + "[Success] Connection Successfully Removed!" + Main.RESET);
    }

    public void updateEdgeStatus(String stationID, boolean isWorking) {
        if (stationID.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station ID Is Empty!" + Main.RESET);
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
        System.out.println(Main.BOLD + Main.GREEN + "[Success] Update Successful!" + Main.RESET);
    }

    public boolean updateStatus(String stationID, boolean isWorking) {
        if (stationID.isEmpty()) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station ID Is Empty!" + Main.RESET);
            return false;
        }

        // Safely capture the exact key to prevent null errors on case mismatch
        String actualStationID = graphMap.keySet().stream().filter(k -> k.equalsIgnoreCase(stationID)).findFirst().orElse(null);

        if (actualStationID == null) {
            System.out.println(Main.BOLD + Main.RED + "[Error] Station Does Not Exists!" + Main.RESET);
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
        return Objects.requireNonNull(vertex.stream().filter(v -> v.stationID.equalsIgnoreCase(destID)).findFirst().orElse(null)).stationName;
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

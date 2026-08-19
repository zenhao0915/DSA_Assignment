import java.util.*;

public class Graph {
    public static Map<String, Vertex> graphMap = new HashMap<>();

    public boolean addVertex(String stationID, String stationName, boolean isWorking) {
        if (graphMap.values().stream().anyMatch(v -> Objects.equals(v.stationID.toLowerCase(), stationID.toLowerCase()))) {
            System.out.println("[Error] Station Already Exists!");
            return false;
        }
        graphMap.put(stationID, new Vertex(stationID, stationName, isWorking, new ArrayList<>()));
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Station Added Successfully!");
        return true;
    }

    public boolean addEdge(String stationID, String destID, int time) {
        if (graphMap.values().stream().noneMatch(v -> Objects.equals(v.stationID.toLowerCase(), stationID.toLowerCase())) || graphMap.values().stream().noneMatch(v -> Objects.equals(v.stationID.toLowerCase(), destID.toLowerCase()))) {
            System.out.println("[Error] One/Two Of The Station Do Not Exists!");
            return false;
        }
        Edge toDest = new Edge(destID, time, true);
        Edge toSource = new Edge(stationID, time, true);
        graphMap.get(stationID).edge.add(toDest);
        graphMap.get(destID).edge.add(toSource);
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Connection Successfully Created!");
        return true;
    }

    public boolean removeVertex(String stationID) {
        if (graphMap.values().stream().noneMatch(v -> Objects.equals(v.stationID.toLowerCase(), stationID.toLowerCase()))) {
            System.out.println("[Error] Station Does Not Exists!");
            return false;
        }
        graphMap.remove(stationID);
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Station And Associated Connections Deleted Successfully!");
        return true;
    }

    public void removeEdge(String stationID, String destID) {
        if (graphMap.get(stationID) != null && Objects.equals(graphMap.get(stationID).stationID.toLowerCase(), stationID.toLowerCase())) {
            graphMap.get(stationID).edge = null;
        }
        if (graphMap.get(destID) != null && Objects.equals(graphMap.get(destID).stationID.toLowerCase(), destID.toLowerCase())) {
            graphMap.get(destID).edge = null;
        }
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Connection Successfully Removed!");
    }

    public void updateEdgeStatus(String stationID, boolean isWorking) {
        for (Vertex v: graphMap.values()) {
            if (!Objects.equals(v.stationID.toLowerCase(), stationID.toLowerCase())) continue;
            v.edge.forEach(edge -> edge.isActive = isWorking);
        }
        graphMap.forEach((k, vertex) -> {
            for (Edge edge: vertex.edge) {
                if (Objects.equals(edge.destID.toLowerCase(), stationID.toLowerCase())) edge.isActive = isWorking;
            }
        });
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Update Successful!");
    }

    public boolean updateStatus(String stationID, boolean isWorking) {
        Vertex currentVertex = graphMap.get(stationID);
        if (currentVertex == null) {
            System.out.println("[Error] Station Does Not Exists!");
            return false;
        }
        currentVertex.isWorking = isWorking;
        updateEdgeStatus(stationID, isWorking);
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
        return Objects.requireNonNull(vertex.stream().filter(v -> Objects.equals(v.stationID.toLowerCase(), destID.toLowerCase())).findFirst().orElse(null)).stationName;
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

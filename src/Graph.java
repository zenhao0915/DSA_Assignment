import java.util.*;

public class Graph {
    public static Map<String, Vertex> graphMap = new HashMap<>();

    public boolean addVertex(String stationID, String stationName, boolean isWorking) {
        if (stationID == null || stationID.isEmpty()) {
            System.out.println("[Error] Station ID Is Empty!");
            return false;
        }
        if (graphMap.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(stationID))) {
            System.out.println("[Error] Station Already Exists!");
            return false;
        }
        graphMap.put(stationID, new Vertex(stationID, stationName, isWorking, new ArrayList<>()));
        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Station Added Successfully!");
        return true;
    }

    public boolean addEdge(String stationID, String destID, int time) {
        if (stationID == null || stationID.isEmpty()) {
            System.out.println("[Error] Station ID Is Empty!");
            return false;
        }
        if (destID == null || destID.isEmpty()) {
            System.out.println("[Error] Destination ID Is Empty!");
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
            System.out.println("[Error] One/Two Of The Station Do Not Exists!");
            return false;
        }

        Vertex sourceVertex = graphMap.get(actualStationID);
        Vertex destVertex = graphMap.get(actualDestID);

        if (sourceVertex.edge == null) sourceVertex.edge = new ArrayList<>();
        if (destVertex.edge == null) destVertex.edge = new ArrayList<>();

        sourceVertex.edge.add(new Edge(actualDestID, time, true));
        destVertex.edge.add(new Edge(actualStationID, time, true));

        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Connection Successfully Created!");
        return true;
    }

    public boolean removeVertex(String stationID) {
        if (stationID == null || stationID.isEmpty()) {
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
        if (stationID == null || stationID.isEmpty()) {
            System.out.println("[Error] Start Station ID Is Empty!");
            return;
        }
        if (destID == null || destID.isEmpty()) {
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
            System.out.println("[Error] Start Station Does Not Exist!");
            return;
        }
        if (actualDestID == null) {
            System.out.println("[Error] Destination Station Does Not Exist!");
            return;
        }

        boolean edgeRemoved = false;
        Vertex sourceVertex = graphMap.get(actualStationID);
        Vertex destVertex = graphMap.get(actualDestID);

        if (sourceVertex.edge != null) {
            if (sourceVertex.edge.removeIf(e -> e.destID.equalsIgnoreCase(actualDestID))) {
                edgeRemoved = true;
            }
        }
        if (destVertex.edge != null) {
            if (destVertex.edge.removeIf(e -> e.destID.equalsIgnoreCase(actualStationID))) {
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
        if (stationID == null || stationID.isEmpty()) {
            System.out.println("[Error] Station ID Is Empty!");
            return;
        }

        String actualStationID = graphMap.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(stationID))
                .findFirst()
                .orElse(null);

        if (actualStationID == null) {
            return;
        }

        Vertex targetVertex = graphMap.get(actualStationID);
        if (targetVertex.edge != null) {
            targetVertex.edge.forEach(edge -> edge.isActive = isWorking);
        }

        graphMap.forEach((k, vertex) -> {
            if (vertex.edge == null) return;
            for (Edge edge : vertex.edge) {
                if (edge.destID.equalsIgnoreCase(actualStationID)) {
                    edge.isActive = isWorking;
                }
            }
        });

        FileManager.INSTANCE.saveGraphToFile();
        System.out.println("[Success] Update Successful!");
    }

    public boolean updateStatus(String stationID, boolean isWorking) {
        if (stationID == null || stationID.isEmpty()) {
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

        Vertex currentVertex = graphMap.get(actualStationID);
        currentVertex.isWorking = isWorking;
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
        Vertex found = vertex.stream()
                .filter(v -> v.stationID.equalsIgnoreCase(destID))
                .findFirst()
                .orElse(null);
        return found != null ? found.stationName : null;
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
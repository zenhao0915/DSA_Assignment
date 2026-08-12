public class StationNetwork {
    public static void startStationNetwork() {
        if (Graph.graphMap.isEmpty()) {
            System.out.println("[Error] No Stations In The Network!");
            return;
        }
        System.out.println("""
                =======================================
                     Generic Train Station Network
                =======================================
                """);
        Graph.graphMap.forEach((stationID, vertex) -> {
            System.out.println("\nStationID: " + vertex.stationID);
            System.out.println("Station Name: " + vertex.stationName);
            String status = vertex.isWorking ? "Working" : "Under Maintenance";
            System.out.println("Status: " + status);

            if (!vertex.isWorking) {
                System.out.println("Warning! Station Closed For Maintenance!");
            }
            System.out.println("Connected Stations: ");
            if (vertex.edge.isEmpty()) {
                System.out.println("No Connections!");
            } else {
                vertex.edge.forEach(edge -> {
                    Vertex tempVertex = Graph.graphMap.get(edge.destID);
                    if (tempVertex == null) return;
                    String trackStatus = edge.isActive ? "Active" : "Inactive";

                    System.out.println("---> To: " + edge.getIDByName(Graph.graphMap.values()) + "(" + edge.destID + ")");
                    System.out.println("     Time: " + edge.timeCost + "mins");
                    System.out.println("     Status: " + trackStatus);
                });
            }
        });
    }
}

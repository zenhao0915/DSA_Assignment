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
            System.out.println("\nStationID: " + Main.YELLOW + vertex.stationID + Main.RESET);
            System.out.println("Station Name: " + Main.YELLOW + vertex.stationName + Main.RESET);
            String status = vertex.isWorking ? Main.GREEN + "Working" : Main.RED + "Under Maintenance";
            System.out.println("Status: " + status + Main.RESET);

            if (!vertex.isWorking) {
                System.out.println(Main.BOLD + Main.RED + "Warning! Station Closed For Maintenance!" + Main.RESET);
            }
            System.out.println("Connected Stations: ");
            if (vertex.edge.isEmpty()) {
                System.out.println("No Connections!");
            } else {
                for (Edge edge : vertex.edge) {
                    Vertex tempVertex = Graph.graphMap.get(edge.destID);
                    if (tempVertex == null) return;
                    String trackStatus = edge.isActive ? Main.GREEN + "Active" : Main.RED + "Inactive";

                    System.out.println("---> To: " + Main.PURPLE + edge.getIDByName(Graph.graphMap.values()) + "(" + edge.destID + ")" + Main.RESET);
                    System.out.println("     Time: " + Main.CYAN + edge.timeCost + "mins" + Main.RESET);
                    System.out.println("     Status: " + trackStatus + Main.RESET);
                }
            }
        });
    }
}

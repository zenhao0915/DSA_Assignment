import java.util.List;

public class Graph {
    record Edge(String destination, int timeCost, LineType lineType) {

        public String getDestination() {
            return destination;
        }

        public int getTimeCost() {
            return timeCost;
        }

        public LineType getLineType() {
            return lineType;
        }

        public String getLineTypeName() {
            return lineType.name();
        }
    }

    record Vertex(String stationID, String stationName, boolean isWorking, List<Edge> edgeList) {

    }

    public void addVertex(String stationID, String stationName, boolean isWorking) {

    }
}
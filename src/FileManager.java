import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class FileManager {
    public static FileManager INSTANCE = new FileManager();

    public void saveGraphToFile() {
        File file = new File("metro.txt");
        try {
            if (file.exists()) file.delete();
            file.createNewFile();
            Graph.graphMap.forEach((k, v) -> {
                StringBuilder edgeString = new StringBuilder();
                if (v.edge != null) {
                    v.edge.forEach(edge -> {
                        edgeString.append(edge.destID).append(":").append(edge.timeCost).append(":").append(edge.isActive)
                                .append("/");// To Split If More Than 1 Edge
                    });
                }
                writeDataToFile(file, v.stationID, v.stationName, String.valueOf(v.isWorking), edgeString.toString());
            });
        } catch (Exception e) {
            System.out.println("[Error] File I/O Caused Unexpected Error!");
            e.printStackTrace();
        }
    }

    public Map<String, Vertex> loadGraphFromFile() {
        Map<String, Vertex> tempGraphMap = new HashMap<>();
        File file = new File("metro.txt");
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.endsWith("/")) {
                    line = line.substring(0, line.length() - 1); // Data Cleaning
                }
                String[] data =  line.split(","); //StationID, StationName, IsWorking, List<Edge>
                List<Edge> edges = new ArrayList<>();
                if (data.length > 3) {
                    for (String parent : data[3].split("/")) {
                        String[] childString = parent.split(":");
                        edges.add(new Edge(childString[0], Integer.parseInt(childString[1]), Boolean.parseBoolean(childString[2])));
                    }
                }
                Vertex vertex = new Vertex(data[0], data[1], Boolean.parseBoolean(data[2]), edges);
                tempGraphMap.put(data[0], vertex);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempGraphMap;

    }

    public void saveUsers(HashSet<User> userSet) {
        File file = new File("users.txt");
        try {
            if (file.exists()) file.delete(); // Delete Everytime To Save New Data
            file.createNewFile();
            for (User u : userSet) {
                writeDataToFile(file, u.userName(), u.password(), String.valueOf(u.isAdmin()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashSet<User> loadUsers() {
        HashSet<User> userSets = new HashSet<>();
        File file = new File("users.txt");
        if (!file.exists()) {
            return userSets; //empty array list
        }
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String[] data = reader.nextLine().split(","); //admin,admin123,true
                User u = new User(data[0], data[1], Boolean.parseBoolean(data[2]));
                userSets.add(u);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userSets;
    }

    public void writeDataToFile(File file, String... stringVar) {
        if (!file.canRead() || !file.canWrite()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (String data : stringVar) {
            stringBuilder.append(data).append(",");
        }
        if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1); // Remove Last String Of ,
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(stringBuilder.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

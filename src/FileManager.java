import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Scanner;

public class FileManager {
    public static FileManager INSTANCE = new FileManager();
    private int currentCount = 0;

    public void saveGraphToFile() {
        currentCount = 0;
        File file = new File("metro.txt");
        try {
            if (file.exists()) file.delete();
            file.createNewFile();
            Graph.graphMap.forEach((k, v) -> {
                StringBuilder edgeString = new StringBuilder();
                v.edge.forEach(edge -> edgeString.append(edge.destID).append(":").append(edge.timeCost).append(":").append(edge.isActive));
                if (v.edge.size() > 1 && currentCount < v.edge.size())
                    edgeString.append("|"); // To Split If More Than 1 Edge
                writeDataToFile(file, v.stationID, v.stationName, String.valueOf(v.isWorking), edgeString.toString());
                currentCount++;
            });
        } catch (Exception e) {
            System.out.println("[Error] File I/O Caused Unexpected Error!");
            e.fillInStackTrace();
        }
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
            e.fillInStackTrace();
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
            e.fillInStackTrace();
        }
        return userSets;
    }

    public void writeDataToFile(File file, String... stringVar) {
        if (!file.canRead() || !file.canWrite()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (String data : stringVar) {
            stringBuilder.append(data).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1); // Remove Last String Of ,
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(stringBuilder.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}

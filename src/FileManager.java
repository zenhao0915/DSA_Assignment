import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileManager {
    public static FileManager INSTANCE = new FileManager();
    private int currentCount = 0;

    public void saveGraphToFile() {
        currentCount = 0;
        File file = new File("metro.dat");
        try {
            if (!file.exists()) {
                file.getAbsoluteFile().mkdirs();
                file.createNewFile();
            }
            Graph.graphMap.forEach((k, v) -> {
                StringBuilder edgeString = new StringBuilder();
                v.edge.forEach(edge -> edgeString.append(edge.destID).append(":").append(edge.timeCost).append(":").append(edge.isActive));
                if (v.edge.size() > 1 && currentCount < v.edge.size()) edgeString.append("|"); // To Split If More Than 1 Edge
                writeDataToFile(file, v.stationID, v.stationName, String.valueOf(v.isWorking), edgeString.toString());
                currentCount++;
            });
        } catch (Exception e) {
            System.out.println("[Error] File I/O Caused Unexpected Error!");
            e.fillInStackTrace();
        }
    }

    public void writeDataToFile(File file, String... stringVar) {
        if (!file.canExecute() || !file.canRead() || !file.canWrite()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (String data: stringVar) {
            stringBuilder.append(data).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1); // Remove Last String Of ,
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(stringBuilder.toString());
            writer.newLine();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}

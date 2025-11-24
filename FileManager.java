import java.io.*;
import java.util.*;

public class FileManager {

    public static List<String[]> loadCSV(String filePath) {
        List<String[]> data = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) return data;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return data;
    }

    public static void saveCSV(String filePath, List<String> rows) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (String r : rows) {
                pw.println(r);
            }
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public static void exportCSV(String originalPath, String exportPath) {
        try {
            FileInputStream fis = new FileInputStream(originalPath);
            FileOutputStream fos = new FileOutputStream(exportPath);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fis.close();
            fos.close();

            System.out.println("Data exported successfully to: " + exportPath);

        } catch (Exception e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
}

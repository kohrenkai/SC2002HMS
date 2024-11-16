import java.io.*;
import java.util.*;

public class CSVUtility {
    private String filePath;

    public CSVUtility(String filePath) {
        this.filePath = filePath;
    }

    public List<String[]> readCSV() {
        List<String[]> data = new ArrayList<>();
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = parseCSVLine(line, csvSplitBy);
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void writeCSV(List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) { // false to overwrite file
            for (String[] rowData : data) {
                bw.write(String.join(",", rowData));
                bw.newLine();
            }
            bw.flush(); // Ensure all data is written
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> readCSVWithoutHeader() {
        List<String[]> data = new ArrayList<>();
        String line;
        String csvSplitBy = ",";
        boolean isFirstRow = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip the header row
                }
                String[] values = parseCSVLine(line, csvSplitBy);
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void writeCSVWithoutHeader(List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) { // false to overwrite file
            for (int i = 1; i < data.size(); i++) { // Start from index 1 to skip the header row
                String[] rowData = data.get(i);
                bw.write(String.join(",", rowData));
                bw.newLine();
            }
            bw.flush(); // Ensure all data is written
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String[]> readCSVToMap() {
        Map<String, String[]> dataMap = new HashMap<>();
        String line;
        String csvSplitBy = ",";
        boolean isFirstRow = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip the header row
                }
                String[] values = parseCSVLine(line, csvSplitBy);
                dataMap.put(values[0], values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataMap;
    }

    private String[] parseCSVLine(String line, String csvSplitBy) {
        // Handle quoted values
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // Toggle the inQuotes flag
            } else if (c == csvSplitBy.charAt(0) && !inQuotes) {
                values.add(sb.toString().trim());
                sb.setLength(0); // Clear the StringBuilder
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString().trim()); // Add the last value

        return values.toArray(new String[0]);
    }

    public void printCSV() {
        List<String[]> data = readCSV();
        System.out.println("\nCSV Contents:");
        for (String[] row : data) {
            System.out.println(String.join(", ", row));
        }
    }
}

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


class PrescriptionManager {
	
	
    private static final String PRESCRIPTION_STATUS_FILE = "PrescriptionStatus.csv"; // Relative path
    private CSVUtility csvUtility;

    public PrescriptionManager() {
        this.csvUtility = new CSVUtility(PRESCRIPTION_STATUS_FILE);
        createPrescriptionStatusFileIfNotExists(); // Check and create file if not exists
    }

    private void createPrescriptionStatusFileIfNotExists() {
        // Check if the file exists
        if (!Files.exists(Paths.get(PRESCRIPTION_STATUS_FILE))) {
            List<String[]> header = new ArrayList<>();
            header.add(new String[]{"Prescription ID", "Status"});
            csvUtility.writeCSV(header);
            System.out.println("Created PrescriptionStatus.csv with header.");
        }
    }

    public void updatePrescriptionStatus(String prescriptionId, String status) {
        List<String[]> data = csvUtility.readCSV();
        data.add(new String[]{prescriptionId, status});
        csvUtility.writeCSV(data);
        System.out.println("Prescription status updated successfully.");
    }
}

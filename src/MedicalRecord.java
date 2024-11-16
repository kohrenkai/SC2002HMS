
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {
    // Attributes for the medical record
    private String patientId;
    private String name;
    private String dateOfBirth;
    private String gender;
    private String contactInformation;
    private String bloodType;
    private List<String> pastDiagnoses;
    private List<String> treatments;

    // Constructor to initialize the MedicalRecord object
    public MedicalRecord(String patientId, String name, String dateOfBirth, String gender, 
                         String contactInformation, String bloodType, 
                         List<String> pastDiagnoses, List<String> treatments) {
        this.patientId = patientId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contactInformation = contactInformation;
        this.bloodType = bloodType;
        
        // Initialize the lists, avoiding null values
        this.pastDiagnoses = pastDiagnoses != null ? pastDiagnoses : new ArrayList<>();
        this.treatments = treatments != null ? treatments : new ArrayList<>();
    }

    // Getters for medical record attributes
    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public String getBloodType() {
        return bloodType;
    }

    public List<String> getPastDiagnoses() {
        return pastDiagnoses;
    }

    public List<String> getTreatments() {
        return treatments;
    }
    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
        // Automatically update the CSV when the contact information is changed
        updateContactInformationInCSV();
    }

    // Method to update the contact information in patient_list.csv
    private void updateContactInformationInCSV() {
        List<String[]> updatedPatients = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("patient_list.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                
                // Check if the current line is for the current patient
                if (details[0].equals(getPatientId())) {
                    // Update the contact information in the CSV entry
                    details[5] = this.contactInformation;
                }
                updatedPatients.add(details);
            }
        } catch (IOException e) {
            System.out.println("Error reading patient data: " + e.getMessage());
        }

        // Write the updated list back to the CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter("patient_list.csv"))) {
            for (String[] patient : updatedPatients) {
                writer.println(String.join(",", patient));
            }
        } catch (IOException e) {
            System.out.println("Error writing to patient CSV file: " + e.getMessage());
        }
    }
    
    // Method to display the medical record
    public void displayMedicalRecord() {
        System.out.println("Patient Name: " + name);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Blood Type: " + bloodType);
        
        // Only display past diagnoses and treatments if available
        if (!pastDiagnoses.isEmpty()) {
            System.out.println("Past Diagnoses: " + String.join(", ", pastDiagnoses));
        } else {
            System.out.println("No past diagnoses available.");
        }

        if (!treatments.isEmpty()) {
            System.out.println("Treatments: " + String.join(", ", treatments));
        } else {
            System.out.println("No treatments available.");
        }
    }
    
    public void displayMedicalRecordForPatient() {
        System.out.println("Patient ID: " + patientId);
        System.out.println("Name: " + name);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Gender: " + gender);
        System.out.println("Contact Information: " + contactInformation);
        System.out.println("Blood Type: " + bloodType);
        
        // Show past diagnoses if available
        if (!pastDiagnoses.isEmpty()) {
            System.out.println("Past Diagnoses: " + String.join(", ", pastDiagnoses));
        } else {
            System.out.println("No past diagnoses available.");
        }

        // Show treatments if available
        if (!treatments.isEmpty()) {
            System.out.println("Treatments: " + String.join(", ", treatments));
        } else {
            System.out.println("No treatments available.");
        }
    }
}


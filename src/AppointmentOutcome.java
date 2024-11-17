
public class AppointmentOutcome {
    private String service;
    private String medication;
    private String medStatus;
    private String notes;

    // Constructor
    
    public AppointmentOutcome() {
        this.service = "";
        this.medication = "";
        this.medStatus = "Pending";
        this.notes = "";
    }
 
    // Getters and Setters
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getMedStatus() {
        return medStatus;
    }

    public void setMedStatus(String medStatus) {
        this.medStatus = medStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    

    
    public void displayOutcome() {
        System.out.println("Service Provided: " + service);
        System.out.println("Prescribed Medication: " + medication);
        System.out.println("Consultation Notes: " + notes);
    }
    
    public String toCSV() {
        StringBuilder line = new StringBuilder();
        // Append outcome fields (service, medication, medStatus, notes)
        line.append(service != null ? service : "").append(",")
            .append(medication != null ? medication : "").append(",")
            .append(medStatus != null ? medStatus : "").append(",")
            .append(notes != null ? notes : "");

        return line.toString();
    }

    public static AppointmentOutcome fromCSV(String csvLine) {
        String[] tokens = csvLine.split(",");

        // Validate and parse the CSV line
        if (tokens.length != 4) {
            throw new IllegalArgumentException("Invalid AppointmentOutcome CSV line: " + csvLine);
        }

        AppointmentOutcome outcome = new AppointmentOutcome();
        outcome.setService(tokens[0].trim());
        outcome.setMedication(tokens[1].trim());
        outcome.setMedStatus(tokens[2].trim());
        outcome.setNotes(tokens[3].trim());

        return outcome;
    }


    @Override
    public String toString() {
        return "Service: " + service + ", Medication: " + medication + ", Med Status: " + medStatus + ", Notes: " + notes;
    }
}

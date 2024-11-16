
public class AppointmentOutcome {
    private String service;
    private String medication;
    private String medStatus;
    private String notes;

    // Constructor
    
    public AppointmentOutcome() {
        this.service = "";
        this.medication = "";
        this.medStatus = "";
        this.notes = "";
    }
    public AppointmentOutcome(String service, String medication, String medStatus, String notes) {
        this.service = service;
        this.medication = medication;
        this.medStatus = medStatus;
        this.notes = notes;
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
    
    public String toCSV() {
        return service + "," + medication + "," + medStatus + "," + notes;
    }
    
    public void displayOutcome() {
        System.out.println("Service Provided: " + service);
        System.out.println("Prescribed Medication: " + medication);
        System.out.println("Consultation Notes: " + notes);
    }



    @Override
    public String toString() {
        return "Service: " + service + ", Medication: " + medication + ", Med Status: " + medStatus + ", Notes: " + notes;
    }
}

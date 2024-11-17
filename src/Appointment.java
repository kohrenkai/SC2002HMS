
public class Appointment {
    private String patientId;
    private String doctorId;
    private String date;
    private String timeSlot;
    private Status status;
    private AppointmentOutcome outcome;

    public enum Status {
        PENDING,
        CONFIRMED,
        CANCELED,
        COMPLETED,
        DECLINED,
        REQUESTED
    }

    public Appointment(String date, String timeSlot, Status status) {
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
        this.patientId = null;  // Initially null, will be set when the patient schedules
        this.doctorId = null;   // Initially null, will be set when doctor states availability
        this.outcome = null;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDate() {
        return date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public AppointmentOutcome getOutcome() {
        return outcome;
    }
    
    public void setOutcome(AppointmentOutcome outcome) {
        this.outcome = outcome;
    }
    
 // Convert Appointment object to CSV string
    public String toCSV() {
        StringBuilder line = new StringBuilder();
        line.append(doctorId != null ? doctorId : "").append(",")
            .append(patientId != null ? patientId : "").append(",")
            .append(date).append(",")
            .append(timeSlot).append(",")
            .append(status);

        // Append outcome details only if it's not null
        if (outcome != null) {
            line.append(",").append(outcome.toCSV()); // outcome is serialized here
        } else {
            line.append(",,,,"); // If no outcome, add 4 empty fields
        }

        return line.toString();
    }





    public static Appointment fromCSV(String csvLine) {
        // Split the line by commas
        String[] tokens = csvLine.split(",");

        // Validate that there are at least 5 tokens for the essential fields (doctorId, patientId, date, timeSlot, status)
        if (tokens.length < 5) {
            throw new IllegalArgumentException("Invalid CSV line, not enough tokens: " + csvLine);
        }

        // Extract basic appointment fields
        String doctorId = tokens[0].isEmpty() ? null : tokens[0].trim();
        String patientId = tokens[1].isEmpty() ? null : tokens[1].trim();
        String date = tokens[2].trim();
        String timeSlot = tokens[3].trim();
        Status status;
        try {
            status = Status.valueOf(tokens[4].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            status = Status.PENDING;  // Default to PENDING if the status is invalid
        }

        // Create a new Appointment object
        Appointment appointment = new Appointment(date, timeSlot, status);
        appointment.setDoctorId(doctorId);
        appointment.setPatientId(patientId);

        // Extract outcome details if they exist (service, medication, medStatus, notes)
        if (tokens.length > 5) {
            AppointmentOutcome outcome = new AppointmentOutcome();

            // If there are missing outcome fields, set default empty values
            outcome.setService(tokens.length > 5 ? tokens[5].trim() : "");
            outcome.setMedication(tokens.length > 6 ? tokens[6].trim() : "");
            outcome.setMedStatus(tokens.length > 7 ? tokens[7].trim() : "");
            outcome.setNotes(tokens.length > 8 ? tokens[8].trim() : "");

            appointment.setOutcome(outcome);
        }

        return appointment;
    }





    public boolean isAvailable() {
        return patientId == null && status == Status.PENDING;
    }
    

    // Additional methods for handling appointment status transitions (e.g., canceling, confirming) can be added here
}



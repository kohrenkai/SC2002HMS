
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
        return (patientId != null ? patientId : "") + "," + 
               (doctorId != null ? doctorId : "") + "," + 
               date + "," + timeSlot + "," + status;
    }

    // Create Appointment object from CSV string
    public static Appointment fromCSV(String csvLine) {
        String[] tokens = csvLine.split(",");
        String patientId = tokens[0].isEmpty() ? null : tokens[0];
        String doctorId = tokens[1].isEmpty() ? null : tokens[1];
        String date = tokens[2];
        String timeSlot = tokens[3];
        Status status = Status.valueOf(tokens[4].trim().toUpperCase());

        Appointment appointment = new Appointment(date, timeSlot, status);
        appointment.setPatientId(patientId);
        appointment.setDoctorId(doctorId);
        return appointment;
    }

    public boolean isAvailable() {
        return patientId == null && status == Status.PENDING;
    }

    // Additional methods for handling appointment status transitions (e.g., canceling, confirming) can be added here
}



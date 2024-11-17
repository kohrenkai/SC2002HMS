import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Doctor extends User {
    private String gender;
    private int age;
    
    private static List<Doctor> doctorsList = new ArrayList<>();
    private static List<User> patientUsers = new ArrayList<>();

    
    // Constructor to initialize the Doctor object
    public Doctor(String id, String hashedPassword, String salt, String name, String gender, int age) {
        super(id, hashedPassword, salt, name, Role.DOCTOR);  // Role is set to DOCTOR
        this.gender = gender;
        this.age = age;
        doctorsList.add(this);
        patientUsers.add(this);
    }
    
    public String getDoctorId() {
    	return userID;
    }

    @Override
    public void displayRoleSpecificMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nDoctor Menu:");
            System.out.println("1. View Patient Medical Records");
            System.out.println("2. Update Patient Medical Records");
            System.out.println("3. View Personal Schedule");
            System.out.println("4. Set Availability for Appointments");
            System.out.println("5. Accept or Decline Appointment Requests");
            System.out.println("6. View Upcoming Appointments");
            System.out.println("7. Record Appointment Outcome");
            System.out.println("8. Return to main menu");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            switch (choice) {
                case 1:
                    viewPatientMedicalRecords();
                    break;
                case 2:
                    updatePatientMedicalRecords();
                    break;
                case 3:
                    viewPersonalSchedule();
                    break;
                case 4:
                    setAvailability();
                    break;
                case 5:
                    handleAppointmentRequests();
                    break;
                case 6:
                    viewUpcomingAppointments(); 
                    break;
                case 7:
                    recordAppointmentOutcome();
                    break;
                case 8:
                    logout();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    
    private Patient findPatientById(String patientId) {
        for (User user : Main.getPatientsList()) {
            if (user.getUserID().equals(patientId)) {
                return (Patient) user;
            }
        }
        return null;
    }



    public void viewPatientMedicalRecords() {
        Scanner scanner = new Scanner(System.in);

        // Display available patients (from the confirmed appointments list)
        List<Appointment> confirmedAppointments = new ArrayList<>();
        for (Appointment appointment : AppointmentManager.getAppointments()) {
            if (this.getUserID().equals(appointment.getDoctorId()) && appointment.getStatus() == Appointment.Status.CONFIRMED) {
                confirmedAppointments.add(appointment);
            }
        }

        if (confirmedAppointments.isEmpty()) {
            System.out.println("No confirmed appointments found.");
            return;
        }

        // Display the confirmed appointments
        System.out.println("Select an appointment to view the patient's medical record:");
        for (int i = 0; i < confirmedAppointments.size(); i++) {
            Appointment appointment = confirmedAppointments.get(i);
            System.out.println((i + 1) + ". Patient ID: " + appointment.getPatientId() + 
                               " | Date: " + appointment.getDate() + " | Time: " + appointment.getTimeSlot());
        }

        // Get doctor's selection of the appointment
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        if (choice < 1 || choice > confirmedAppointments.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }
        
        // Get the selected appointment
        Appointment selectedAppointment = confirmedAppointments.get(choice - 1);
        String patientId = selectedAppointment.getPatientId();

        // Find the Patient object using the patientId
        Patient patient = findPatientById(patientId);

        if (patient != null) {
            // Display the patient's relevant medical record
            System.out.println("\nViewing medical record for Patient ID: " + patientId);
            patient.getMedicalRecord().displayMedicalRecord();
        } else {
            System.out.println("Patient not found.");
        }
    }


    public void updatePatientMedicalRecords() {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Display available patients (from the confirmed appointments list)
        List<Appointment> confirmedAppointments = new ArrayList<>();
        for (Appointment appointment : AppointmentManager.getAppointments()) {
            if (this.getUserID().equals(appointment.getDoctorId()) && appointment.getStatus() == Appointment.Status.CONFIRMED) {
                confirmedAppointments.add(appointment);
            }
        }

        if (confirmedAppointments.isEmpty()) {
            System.out.println("No confirmed appointments found.");
            return;
        }

        // Step 2: Display the confirmed appointments
        System.out.println("Select an appointment to view the patient's medical record:");
        for (int i = 0; i < confirmedAppointments.size(); i++) {
            Appointment appointment = confirmedAppointments.get(i);
            System.out.println((i + 1) + ". Patient ID: " + appointment.getPatientId() + 
                               " | Date: " + appointment.getDate() + " | Time: " + appointment.getTimeSlot());
        }

        // Get doctor's selection of the appointment
        int choice = scanner.nextInt();

        if (choice < 1 || choice > confirmedAppointments.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        // Get the selected appointment
        Appointment selectedAppointment = confirmedAppointments.get(choice - 1);
        String patientId = selectedAppointment.getPatientId();

        // Step 3: Find the Patient object using the patientId
        Patient selectedPatient = findPatientById(patientId);
        if (selectedPatient == null) {
            System.out.println("Patient not found.");
            return;
        }

        MedicalRecord medicalRecord = selectedPatient.getMedicalRecord();

        // Step 4: Ask for new diagnosis and treatment
        scanner.nextLine(); // consume newline
        System.out.print("Enter the new diagnosis: ");
        String diagnosis = scanner.nextLine();

        System.out.print("Enter the treatment plan: ");
        String treatment = scanner.nextLine();

        // Step 5: Update medical record
        medicalRecord.getPastDiagnoses().add(diagnosis);
        medicalRecord.getTreatments().add(treatment);

        // Step 6: Confirm update
        System.out.println("Medical record updated successfully.");
        System.out.println("Updated Diagnoses: " + String.join(", ", medicalRecord.getPastDiagnoses()));
        System.out.println("Updated Treatments: " + String.join(", ", medicalRecord.getTreatments()));
    }


    private void viewPersonalSchedule() {
        List<Appointment> appointments = AppointmentManager.getAppointments();

        // Filter appointments for the doctor calling this method
        List<Appointment> confirmedAppointments = new ArrayList<>();
        List<Appointment> availableSlots = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (this.getUserID().equals(appointment.getDoctorId())) {
                if (appointment.getStatus()  == Appointment.Status.CONFIRMED) {
                    confirmedAppointments.add(appointment);
                } else {
                    availableSlots.add(appointment);
                }
            }
        }

        // Display confirmed appointments
        System.out.println("\nConfirmed Appointments:");
        if (confirmedAppointments.isEmpty()) {
            System.out.println("No confirmed appointments found.");
        } else {
            for (Appointment appointment : confirmedAppointments) {
                System.out.println("Patient ID: " + appointment.getPatientId() +
                        " | Date: " + appointment.getDate() +
                        " | Time Slot: " + appointment.getTimeSlot() +
                        " | Status: " + appointment.getStatus());
            }
        }

        // Display available slots
        System.out.println("\nAvailable Slots:");
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots found.");
        } else {
            for (Appointment slot : availableSlots) {
                System.out.println("Date: " + slot.getDate() +
                        " | Time Slot: " + slot.getTimeSlot() +
                        " | Status: " + slot.getStatus());
            }
        }
    }


    public void setAvailability() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the date for availability (e.g., 2024-11-20): ");
        String date = scanner.nextLine();

        System.out.println("Enter the time slot (from 10:00 hrs to 18:00 hrs, e.g., 10:00): ");
        String timeSlot = scanner.nextLine();

        if (AppointmentManager.validateTimeSlot(timeSlot)) {
            AppointmentManager.handleDoctorAvailability(getUserID(), date, timeSlot);
            
        } else {
            System.out.println("Invalid time slot. Please enter a time between 10:00 and 18:00.");
        }
    }


 // Inside Doctor class
    private void handleAppointmentRequests() {
        AppointmentManager.processDoctorAppointmentRequests(this.getUserID());
    }





    private void viewUpcomingAppointments() {
        System.out.println("Viewing upcoming appointments...");

        String doctorId = this.userID;
        List<Appointment> confirmedAppointments = new ArrayList<>();

        for (Appointment appointment : AppointmentManager.getAppointments()) {
            if (doctorId.equals(appointment.getDoctorId()) && appointment.getStatus() == Appointment.Status.CONFIRMED) {
                confirmedAppointments.add(appointment);
            }
        }

        if (confirmedAppointments.isEmpty()) {
            System.out.println("No upcoming appointments.");
        } else {
            for (Appointment appointment : confirmedAppointments) {
                System.out.println("Date: " + appointment.getDate() + " | Time: " + appointment.getTimeSlot() +" | Time: " + appointment.getPatientId());
            }
        }
    }

    public void recordAppointmentOutcome() {
        List<Appointment> confirmedAppointments = new ArrayList<>();
        
        for (Appointment appointment : AppointmentManager.getAppointments()) {
            if (appointment.getDoctorId().equals(this.getUserID()) && appointment.getStatus() == Appointment.Status.CONFIRMED) {
                confirmedAppointments.add(appointment);
            }
        }
       

        // Check if there are any confirmed appointments
        if (confirmedAppointments.isEmpty()) {
            System.out.println("No confirmed appointments available for recording outcome.");
       
            return;
        }

        // Display confirmed appointments to the doctor
        System.out.println("Select an appointment to record outcome:");
        for (int i = 0; i < confirmedAppointments.size(); i++) {
            Appointment appointment = confirmedAppointments.get(i);
            System.out.println((i + 1) + ". Patient " + appointment.getPatientId() +
                               " on " + appointment.getDate() + " at " + appointment.getTimeSlot());
        }

        // Get doctor's selection
        Scanner scanner = new Scanner(System.in);
        int choice;
        while (true) {
            System.out.print("Enter the appointment number (or 0 to cancel): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) return; // Cancel operation
                if (choice > 0 && choice <= confirmedAppointments.size()) break;
                System.out.println("Invalid choice. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        // Get the selected appointment
        Appointment selectedAppointment = confirmedAppointments.get(choice - 1);

        // Get the AppointmentOutcome object from the selected appointment
        AppointmentOutcome outcome = selectedAppointment.getOutcome();
        if (outcome == null) {
            outcome = new AppointmentOutcome(); // Initialize if null
            selectedAppointment.setOutcome(outcome);
        }

        // Allow doctor to record service, medication, and notes
        System.out.print("Enter the service provided: ");
        String service = scanner.nextLine();
        outcome.setService(service);

        System.out.print("Enter prescribed medication: ");
        String medication = scanner.nextLine();
        outcome.setMedication(medication);

        System.out.print("Enter consultation notes: ");
        String notes = scanner.nextLine();
        outcome.setNotes(notes);
        

        // Step 1: Update appointment status to COMPLETED
        selectedAppointment.setStatus(Appointment.Status.COMPLETED);

        // Save the updated appointment outcome to the CSV
        AppointmentManager.saveAppointmentsToCSV();
        

        System.out.println("Appointment outcome recorded successfully!");
    }

    public void logout() {
        System.out.println("Logging out...");
    }
    
 
    public static List<Doctor> getDoctors() {
        return doctorsList;
    }

}


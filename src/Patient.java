
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Patient extends User {
    private MedicalRecord medicalRecord;

    public Patient(String id, String name, String password, String dateOfBirth, String gender, 
                   String bloodType, String contactInformation) {
        super(id, name, password, Role.PATIENT); 
        this.medicalRecord = new MedicalRecord(id, name, dateOfBirth, gender, contactInformation, bloodType, null, null);
    }

    @Override
    public void displayRoleSpecificMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nPatient Menu:");
            System.out.println("1. View Medical Record");
            System.out.println("2. Update Personal Information");
            System.out.println("3. View Available Appointment Slots");
            System.out.println("4. Schedule an Appointment");
            System.out.println("5. Reschedule an Appointment");
            System.out.println("6. Cancel an Appointment");
            System.out.println("7. View Scheduled Appointments");
            System.out.println("8. View Past Appointment Outcome Records");
            System.out.println("9. Logout");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewMedicalRecord();
                    break;
                case 2:
                    updatePersonalInfo();
                    break;
                case 3:
                    AppointmentManager.viewAvailableSlots();
                    break;
                case 4:
                    scheduleAppointment();
                    break;
                case 5:
                    rescheduleAppointment();
                    break;
                case 6:
                    cancelAppointment();
                    break;
                case 7:
                    viewScheduledAppointments();
                    break;
                case 8:
                    viewPastAppointments();
                    break;
                case 9:
                    logout();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    private void viewMedicalRecord() {
        System.out.println("\nViewing your medical record...");
        medicalRecord.displayMedicalRecordForPatient();
    }
    
    private void updatePersonalInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nUpdate Personal Information:");
        System.out.print("Enter new contact information (email/phone): ");
        String newContact = scanner.nextLine();
        medicalRecord.setContactInformation(newContact);
        System.out.println("Contact information updated successfully.");
    }


    public void scheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        List<Appointment> availableAppointments = new ArrayList<>();
        
        for (Appointment appointment : AppointmentManager.getAppointments()) {
            if (appointment.getPatientId() == null && appointment.getStatus() == Appointment.Status.PENDING) {
                availableAppointments.add(appointment);
                System.out.println((availableAppointments.size()) + ". Doctor: " + appointment.getDoctorId() 
                    + " Date: " + appointment.getDate() + " Time: " + appointment.getTimeSlot());
            }
        }

        if (availableAppointments.isEmpty()) {
            System.out.println("No available appointments.");
            return;
        }

        System.out.print("Enter the number of the slot you want to request: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > availableAppointments.size()) {
            System.out.println("Invalid slot number. Please try again.");
            return;
        }

        Appointment selectedAppointment = availableAppointments.get(choice - 1);
        AppointmentManager.handlePatientScheduling(getUserID(), selectedAppointment);

        System.out.println("Appointment request sent successfully to Doctor: " + selectedAppointment.getDoctorId() +
                " for " + selectedAppointment.getDate() + " at " + selectedAppointment.getTimeSlot());
    }

    public void rescheduleAppointment() {
        List<Appointment> appointments = AppointmentManager.getAppointments();

        // Step 1: Display patient's scheduled appointments
        System.out.println("Your scheduled appointments:");
        List<Appointment> patientAppointments = new ArrayList<>();
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            if (this.getUserID().equals(appointment.getPatientId())) {
                patientAppointments.add(appointment);
                System.out.println((patientAppointments.size()) + ". " + appointment.getDate() + " " + appointment.getTimeSlot()
                        + " Doctor: " + appointment.getDoctorId() + " Status: " + appointment.getStatus());
            }
        }

        // If no appointments found, return
        if (patientAppointments.isEmpty()) {
            System.out.println("You have no scheduled appointments to reschedule.");
            return;
        }

        // Step 2: Prompt user to select an appointment to reschedule
        System.out.print("Enter the number of the appointment to reschedule: ");
        Scanner scanner = new Scanner(System.in);
        int appointmentIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline character

        if (appointmentIndex < 0 || appointmentIndex >= patientAppointments.size()) {
            System.out.println("Invalid appointment selection.");
            return;
        }

        Appointment appointmentToReschedule = patientAppointments.get(appointmentIndex);

        // Step 3: Validate if the appointment belongs to the current patient
        if (!this.getUserID().equals(appointmentToReschedule.getPatientId())) {
            System.out.println("You can only reschedule your own appointments.");
            return;
        }

        // Step 4: Display available slots
        System.out.println("Available appointment slots:");
        List<Appointment> availableAppointments = new ArrayList<>();

        // Find all available slots (appointments without a patient assigned)
        for (Appointment appointment : appointments) {
            if (appointment.getPatientId() == null && appointment.getStatus() == Appointment.Status.PENDING) {
                availableAppointments.add(appointment);
                System.out.println((availableAppointments.size()) + ". Doctor: " + appointment.getDoctorId() 
                        + " Date: " + appointment.getDate() + " Time: " + appointment.getTimeSlot());
            }
        }

        // If no available slots, inform the user
        if (availableAppointments.isEmpty()) {
            System.out.println("No available appointments.");
            return;
        }

        // Step 5: Prompt the user to select a new slot
        System.out.print("Enter the number of the slot you want to request: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline character

        if (choice < 0 || choice >= availableAppointments.size()) {
            System.out.println("Invalid slot selection. Please try again.");
            return;
        }

     // Step 6: Update the selected slot with the current patient's ID
        Appointment newSlot = availableAppointments.get(choice);
        newSlot.setPatientId(this.getUserID()); // Assign the current patient to the new slot
        newSlot.setStatus(Appointment.Status.REQUESTED); // Mark the new appointment as PENDING

        // Step 7: Mark the old appointment as PENDING
        appointmentToReschedule.setStatus(Appointment.Status.PENDING);
        appointmentToReschedule.setPatientId(null); // Optionally clear the patient ID of the old appointment

        // Step 8: Save the updated appointments to the CSV file
        AppointmentManager.saveAppointmentsToCSV(); // Save changes

        // Step 9: Confirm the rescheduling to the user
        System.out.println("Appointment rescheduled successfully!");
        System.out.println("New Appointment Details:");
        System.out.println("Doctor: " + newSlot.getDoctorId() + " Date: " + newSlot.getDate() 
                + " Time: " + newSlot.getTimeSlot() + "Status: " + newSlot.getStatus());
    }




    private void cancelAppointment() {
        System.out.println("\nCancelling an appointment...");
        List<Appointment> patientAppointments = AppointmentManager.getAppointments();
        List<Appointment> scheduledAppointments = new ArrayList<>();

        // Display all appointments for the patient
        for (int i = 0; i < patientAppointments.size(); i++) {
            Appointment appointment = patientAppointments.get(i);
            if (getUserID().equals(appointment.getPatientId()) ) {
                scheduledAppointments.add(appointment);
                System.out.println((scheduledAppointments.size()) + ". Doctor: " + appointment.getDoctorId() +
                        " Date: " + appointment.getDate() + " Time: " + appointment.getTimeSlot());
            }
        }

        // Check if there are no appointments to cancel
        if (scheduledAppointments.isEmpty()) {
            System.out.println("You have no appointments to cancel.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the number of the appointment you want to cancel: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume the newline character

        if (choice < 1 || choice > scheduledAppointments.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        // Cancel the selected appointment
        Appointment appointmentToCancel = scheduledAppointments.get(choice - 1);
        AppointmentManager.cancelAppointment(getUserID(), appointmentToCancel);
        System.out.println("Appointment cancelled successfully.");
    }



    public void viewScheduledAppointments() {
        AppointmentManager.viewPatientAppointments(getUserID());
    }

    public void viewPastAppointments() {
        List<Appointment> patientAppointments = AppointmentManager.getAppointments();
        boolean hasOutcomes = false;

        System.out.println("Appointment Outcomes for Patient ID: " + this.getUserID());
        for (Appointment appointment : patientAppointments) {
            if (appointment.getPatientId() != null && appointment.getPatientId().equals(this.getUserID())) {
                // Check if the appointment has an outcome
                if (appointment.getOutcome() != null) {
                    hasOutcomes = true;
                    System.out.println("\nAppointment on " + appointment.getDate() + " at " + appointment.getTimeSlot());
                    // Display outcome details
                    appointment.getOutcome().displayOutcome();
                }
            }
        }

        if (!hasOutcomes) {
            System.out.println("No appointment outcomes available for your past appointments.");
        }
    }

    
}


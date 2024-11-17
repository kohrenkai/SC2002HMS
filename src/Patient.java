
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Patient extends User {
    private MedicalRecord medicalRecord;

    public Patient(String id, String hashedPassword, String salt, String name, String dateOfBirth, 
                   String gender, String bloodType, String contactInformation) {
        super(id, hashedPassword, salt, name, Role.PATIENT); 
        this.medicalRecord = new MedicalRecord(id, name, dateOfBirth, gender, contactInformation, bloodType, null, null);
    }

    @Override
    public void displayRoleSpecificMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
        	System.out.println("\n" + "=".repeat(50));
    	    System.out.println(" ".repeat(15) + "PATIENT MENU");
    	    System.out.println("=".repeat(50));
    	    
    	    System.out.println(" ".repeat(10) + "╔════════════════════════════════════╗ ");
    	    System.out.println(" ".repeat(10) + "║   1. View Medical Record           ║ ");
    	    System.out.println(" ".repeat(10) + "║   2. Update Personal Information   ║ ");
    	    System.out.println(" ".repeat(10) + "║   3. View Available Appointments   ║ ");
    	    System.out.println(" ".repeat(10) + "║   4. Schedule an Appointment       ║ ");
    	    System.out.println(" ".repeat(10) + "║   5. Reschedule an Appointment     ║ ");
    	    System.out.println(" ".repeat(10) + "║   6. Cancel an Appointment         ║ ");
    	    System.out.println(" ".repeat(10) + "║   7. View Scheduled Appointments   ║ ");
    	    System.out.println(" ".repeat(10) + "║   8. View Past Appointment Records ║ ");
    	    System.out.println(" ".repeat(10) + "║   9. Logout                        ║ ");
    	    System.out.println(" ".repeat(10) + "╚════════════════════════════════════╝ ");
    	    
    	    System.out.print("\nEnter your choice (1-9): ");
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
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
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

        String newContact;
        while (true) {
            System.out.print("Enter new contact information (email only) or type 'exit' to cancel: ");
            newContact = scanner.nextLine();

            // Allow the user to exit
            if (newContact.equalsIgnoreCase("exit")) {
                System.out.println("Update cancelled.");
                return; // Exit the method
            }

            // Validate email using regex pattern
            if (isValidEmail(newContact)) {
                break;
            } else {
                System.out.println("Invalid email format. Please try again.");
            }
        }

        medicalRecord.setContactInformation(newContact);
        System.out.println("Contact information updated successfully.");
    }

    public void scheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        List<Appointment> availableAppointments = new ArrayList<>();

     // Load the appointments from CSV once
        List<Appointment> allAppointments = AppointmentManager.getAppointments();

        // Print the header for available appointment slots
        System.out.println("\n---------------- Available Appointment Slots ----------------");
        System.out.printf("%-5s %-15s %-15s %-15s %-15s%n", "No.", "Doctor ID", "Date", "Time Slot", "Status");
        System.out.println("---------------------------------------------------------------");

        // List available slots
        for (int i = 0; i < allAppointments.size(); i++) {
            Appointment appointment = allAppointments.get(i);
            if (appointment.getPatientId() == null && appointment.getStatus() == Appointment.Status.PENDING) {
                availableAppointments.add(appointment);
                
                // Print each available appointment slot in aligned columns
                System.out.printf("%-5d %-15s %-15s %-15s %-15s%n",
                                  availableAppointments.size(),
                                  appointment.getDoctorId(),
                                  appointment.getDate(),
                                  appointment.getTimeSlot(),
                                  "Available");
            }
        }
        
        if (availableAppointments.isEmpty()) {
            System.out.println("\nNo available appointments to schedule.");
            return;
        }

        // Input validation for slot selection
        int choice = -1;
        while (choice < 1 || choice > availableAppointments.size()) {
            System.out.print("Enter the number of the slot you want to request: ");
            
            // Check if the user entered an integer
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                // Check if the choice is valid (within the available range)
                if (choice < 1 || choice > availableAppointments.size()) {
                    System.out.println("Invalid slot number. Please enter a valid number from the list.");
                }
            } else {
                // Handle invalid input (non-integer value)
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume the invalid input
            }
        }

        // Proceed with scheduling the selected appointment
        Appointment selectedAppointment = availableAppointments.get(choice - 1);
        AppointmentManager.handlePatientScheduling(getUserID(), selectedAppointment);

        // Confirm the appointment request
        System.out.println("\nAppointment request sent successfully to Doctor: " + selectedAppointment.getDoctorId() +
                " for " + selectedAppointment.getDate() + " at " + selectedAppointment.getTimeSlot());
    }


    public void rescheduleAppointment() {
        // Get the updated list of appointments (from memory)
        List<Appointment> appointments = AppointmentManager.getAppointments(); 

        // Step 1: Display patient's scheduled appointments and track available slots
        List<Appointment> availableAppointments = new ArrayList<>();

        // Displaying the header for appointments
        System.out.println("\n--- Your Scheduled Appointments ---");
        System.out.println("====================================");

        for (Appointment appointment : appointments) {
            if (this.getUserID().equals(appointment.getPatientId())) {
                // Formatting appointment details
                System.out.println("Appointment #" + (appointments.indexOf(appointment) + 1));
                System.out.println("Doctor: " + appointment.getDoctorId());
                System.out.println("Date: " + appointment.getDate());
                System.out.println("Time: " + appointment.getTimeSlot());
                System.out.println("Status: " + appointment.getStatus());
                System.out.println("------------------------------------");
            }
            if (appointment.getPatientId() == null && appointment.getStatus() == Appointment.Status.PENDING) {
                availableAppointments.add(appointment);
            }
        }

        List<Appointment> patientAppointments = new ArrayList<>();
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            if (this.getUserID().equals(appointment.getPatientId())) {
                patientAppointments.add(appointment);
            }
        }

        if (patientAppointments.isEmpty()) {
            System.out.println("You have no scheduled appointments to reschedule.");
            return;
        }

        int appointmentIndex = -1;
        while (appointmentIndex < 0) {
            System.out.print("\nEnter the appointment index you want to reschedule: ");
            try {
                Scanner scanner = new Scanner(System.in);
                appointmentIndex = Integer.parseInt(scanner.nextLine()) - 1; // Convert to 0-based index

                if (appointmentIndex < 0 || appointmentIndex >= appointments.size()) {
                    System.out.println("Invalid appointment selection. Please try again.");
                    appointmentIndex = -1; // Reset to force re-prompt
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        Appointment appointmentToReschedule = appointments.get(appointmentIndex);

        // Step 4: Validate if the appointment belongs to the current patient
        if (!this.getUserID().equals(appointmentToReschedule.getPatientId())) {
            System.out.println("\nYou can only reschedule your own appointments.");
            return;
        }

        // Step 5: Validate the appointment's status
        if (!isReschedulableStatus(appointmentToReschedule.getStatus())) {
            System.out.println("\nYou can only reschedule appointments that are in 'CONFIRMED', 'PENDING', 'DECLINED', or 'REQUESTED' status.");
            return;
        }

        // Step 6: Display available slots
        if (availableAppointments.isEmpty()) {
            System.out.println("\nNo available appointments.");
            return;
        }

        // Display available slots with proper header
        System.out.println("\n--- Available Appointment Slots ---");
        System.out.println("====================================");

        for (int i = 0; i < availableAppointments.size(); i++) {
            Appointment appointment = availableAppointments.get(i);
            System.out.println("Slot #" + (i + 1));
            System.out.println("Doctor: " + appointment.getDoctorId());
            System.out.println("Date: " + appointment.getDate());
            System.out.println("Time: " + appointment.getTimeSlot());
            System.out.println("------------------------------------");
        }

        // Step 7: Prompt the user to select a new slot
        int choice = -1;
        while (choice < 0) {
            System.out.print("\nEnter the number of the slot you want to request: ");
            try {
                Scanner scanner = new Scanner(System.in);
                choice = Integer.parseInt(scanner.nextLine()) - 1; // Convert to 0-based index

                if (choice < 0 || choice >= availableAppointments.size()) {
                    System.out.println("Invalid slot selection. Please try again.");
                    choice = -1; // Reset to force re-prompt
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        // Step 8: Update the selected slot with the current patient's ID
        Appointment newSlot = availableAppointments.get(choice);
        newSlot.setPatientId(this.getUserID()); // Assign the current patient to the new slot
        newSlot.setStatus(Appointment.Status.REQUESTED); // Mark the new appointment as REQUESTED

        // Step 9: Mark the old appointment as PENDING
        appointmentToReschedule.setStatus(Appointment.Status.PENDING);
        appointmentToReschedule.setPatientId(null); // Optionally clear the patient ID of the old appointment

        // Step 10: Save the updated appointments to the CSV file
        AppointmentManager.saveAppointmentsToCSV(); // Save changes

        // Step 11: Confirm the rescheduling to the user
        System.out.println("\n--- Appointment Rescheduled Successfully! ---");
        System.out.println("=============================================");
        System.out.println("\nNew Appointment Details:");
        System.out.println("--------------------------");
        System.out.println("Doctor: " + newSlot.getDoctorId());
        System.out.println("Date: " + newSlot.getDate());
        System.out.println("Time: " + newSlot.getTimeSlot());
        System.out.println("Status: " + newSlot.getStatus());
        System.out.println("--------------------------------------------");
    }

    // Helper method to check if the appointment status allows rescheduling
    private boolean isReschedulableStatus(Appointment.Status status) {
        return status == Appointment.Status.PENDING ||
               status == Appointment.Status.CONFIRMED ||
               status == Appointment.Status.DECLINED ||
               status == Appointment.Status.REQUESTED;
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
        System.out.println("Your scheduled appointments:");
        boolean found = false;
        
        // Retrieve all appointments
        List<Appointment> appointments = AppointmentManager.getAppointments();

        // Loop through the appointments to find those matching the patient's ID
        for (Appointment appointment : appointments) {
            if (this.getUserID().equals(appointment.getPatientId())) {
                System.out.println("Date: " + appointment.getDate() +
                        " | Time: " + appointment.getTimeSlot() +
                        " | Doctor ID: " + appointment.getDoctorId() +
                        " | Status: " + appointment.getStatus());
                found = true;
            }
        }

        // If no appointments are found, inform the patient
        if (!found) {
            System.out.println("You have no scheduled appointments.");
        }
    }


    public void viewPastAppointments() {
        List<Appointment> patientAppointments = AppointmentManager.getAppointments();  // Assuming this is the list of all appointments
        boolean hasOutcomes = false;

        System.out.println("Appointment Outcomes for Patient ID: " + this.getUserID());
        
        // Loop through all the appointments
        for (Appointment appointment : patientAppointments) {
            // Check if the appointment belongs to the current patient
            if (appointment.getPatientId() != null && appointment.getPatientId().equals(this.getUserID())) {
                // Check if the appointment has an outcome
                if (appointment.getOutcome() != null) {
                    hasOutcomes = true;
                    // Print appointment details
                    System.out.println("\nAppointment on " + appointment.getDate() + " at " + appointment.getTimeSlot());
                    
                    // Print the outcome details
                    AppointmentOutcome outcome = appointment.getOutcome();
                    System.out.println("Service: " + outcome.getService());
                    System.out.println("Medication: " + outcome.getMedication());
                    System.out.println("Medication Status: " + outcome.getMedStatus());
                    System.out.println("Notes: " + outcome.getNotes());
                    
                    // If needed, you can still show the medical record
                }
            }
        }

        if (!hasOutcomes) {
            System.out.println("No appointment outcomes available for your past appointments.");
        }
    }

    
    public static Patient fromCSV(String csvLine) {
        String[] fields = csvLine.split(",");

        if (fields.length != 8) { // Check if the expected number of fields is present
            throw new IllegalArgumentException("Invalid CSV format for Patient: " + csvLine);
        }

        // Assuming the CSV format is:
        // Patient ID, Name, Date of Birth, Gender, Blood Type, Contact Information, Hashed Password, Salt
        String id = fields[0].trim();
        String name = fields[1].trim();
        String dateOfBirth = fields[2].trim();
        String gender = fields[3].trim();
        String bloodType = fields[4].trim();
        String contactInformation = fields[5].trim();
        String hashedPassword = fields[6].trim(); // Assuming this is the correct field for the password
        String salt = fields[7].trim();  // Assuming this is the correct field for the salt

        // Create and return a new Patient object with the parsed fields
        return new Patient(id, hashedPassword, salt, name, dateOfBirth, gender, bloodType, contactInformation);
    }

    
}


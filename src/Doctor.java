import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            System.out.println("\n" + "=".repeat(55));
            System.out.println(" ".repeat(16) + "DOCTOR MENU");
            System.out.println("=".repeat(55));

            System.out.println(" ".repeat(12) + "╔══════════════════════════════════════════╗");
            System.out.println(" ".repeat(12) + "║   1. View Patient Medical Records        ║");
            System.out.println(" ".repeat(12) + "║   2. Update Patient Medical Records      ║");
            System.out.println(" ".repeat(12) + "║   3. View Personal Schedule              ║");
            System.out.println(" ".repeat(12) + "║   4. Set Availability for Appointments   ║");
            System.out.println(" ".repeat(12) + "║   5. Accept or Decline Appointment       ║");
            System.out.println(" ".repeat(12) + "║   6. View Upcoming Appointments          ║");
            System.out.println(" ".repeat(12) + "║   7. Record Appointment Outcome          ║");
            System.out.println(" ".repeat(12) + "║   8. Return to Main Menu                 ║");
            System.out.println(" ".repeat(12) + "╚══════════════════════════════════════════╝");

            int choice = -1;  // Initialize with an invalid value
            while (choice < 1 || choice > 8) {  // Keep looping until valid choice is made
                System.out.print("\nEnter your choice (1-8): ");
                
                // Check if the input is a valid integer
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();  // Consume the newline character

                    // Check if the input is within the valid range
                    if (choice < 1 || choice > 8) {
                        System.out.println("Invalid choice. Please enter a number between 1 and 8.");
                    }
                } else {
                    // If the input is not an integer, prompt the user again
                    System.out.println("Invalid input. Please enter a valid number between 1 and 8.");
                    scanner.nextLine();  // Consume the invalid input
                }
            }

            // Execute the selected action based on the valid choice
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
                    // This shouldn't be hit due to the input validation above
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

        while (true) {
            // Prompt the user to enter the date with format guidance
            System.out.println("\n------------------ Set Doctor Availability ------------------");
            System.out.print("Enter the date for availability (format: yyyy-MM-dd, e.g., 2024-12-03): ");
            String date = scanner.nextLine();

            // Validate the date format using a regex pattern (yyyy-MM-dd)
            if (!isValidDate(date)) {
                System.out.println("Invalid date. Please enter the date in yyyy-MM-dd format.");
                continue; // Prompt again for the date if it's invalid
            }

            // Prompt the user to enter the time slot
            System.out.print("Enter the time slot (from 10:00 hrs to 18:00 hrs, 1 hour intervals. e.g., 10:00): ");
            String timeSlot = scanner.nextLine();

            // Validate the time slot
            if (!isValidTimeSlot(timeSlot)) {
                System.out.println("\nInvalid time slot. Please enter a time between 10:00 and 18:00 in hourly increments (e.g., 10:00, 11:00, ...).");
                continue; // Prompt again for the time slot if it's invalid
            }

            // If both inputs are valid, handle doctor availability and break out of the loop
            AppointmentManager.handleDoctorAvailability(getUserID(), date, timeSlot);
            break; // Exit the loop after setting availability
        }
    }
    
    private boolean isValidDate(String date) {
        // Regex for validating yyyy-MM-dd format
        String regex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);

        // Check if the date matches the regex
        if (!matcher.matches()) {
            return false; // Return false if the format is invalid
        }

        // Extract month and day from the date
        String[] parts = date.split("-");
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        // Check the number of days in the month
        if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
            return false; // April, June, September, November have 30 days
        }
        if (month == 2 && day > 29) {
            return false; // February has 29 days max (ignoring leap year for simplicity)
        }
        if (month == 2 && day > 28) {
            return false; // February has 28 days max if not leap year
        }

        return true; // If all checks pass, the date is valid
    }


    // Method to check if the time slot is one of the valid 1-hour increments between 10:00 and 18:00
    private boolean isValidTimeSlot(String timeSlot) {
        // Valid time slots in hourly increments from 10:00 to 18:00
        String[] validTimeSlots = {
            "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        };
        
        // Check if the entered timeSlot matches one of the valid time slots
        for (String validSlot : validTimeSlots) {
            if (timeSlot.equals(validSlot)) {
                return true;
            }
        }
        return false; // Return false if the timeSlot is not valid
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


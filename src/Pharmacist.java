import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Pharmacist extends User {
    private InventoryManagement inventoryManagement;
    private PrescriptionManager prescriptionManager;

    public Pharmacist(String userID, String hashedPassword, String salt, String name) {
        super(userID, hashedPassword, salt, name, Role.PHARMACIST);
        inventoryManagement = new InventoryManagement("Medicine_List.csv"); // Use relative path
        prescriptionManager = new PrescriptionManager();
    }

    @Override
    public void displayRoleSpecificMenu() {
        showMenu();
    }

    private void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        try {
            do {
                System.out.println("\nPharmacist Menu:");
                System.out.println("1. View Appointment Outcome Record");
                System.out.println("2. Update Prescription Status");
                System.out.println("3. View Medication Inventory");
                System.out.println("4. Check Stock Levels");
                System.out.println("5. Submit Replenishment Request");
                System.out.println("6. Logout");
                System.out.print("Enter your choice: ");

                while (!scanner.hasNextInt()) { // Input validation
                    System.out.print("Invalid input. Please enter a number: ");
                    scanner.next(); // Clear invalid input
                }

                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        viewAppointmentOutcomeRecord();
                        break;
                    case 2:
                        updatePrescriptionStatus(scanner);
                        break;
                    case 3:
                        viewMedicationInventory();
                        break;
                    case 4:
                        checkStockLevels();
                        break;
                    case 5:
                        submitReplenishmentRequest(scanner);
                        break;
                    case 6:
                        logout();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 6);
        } finally {
            
        }
    }

    private void viewAppointmentOutcomeRecord() {
        // Placeholder for viewing appointment outcome records
        System.out.println("Displaying appointment outcome records...");
    }

    private void updatePrescriptionStatus(Scanner scanner) {
        // Step 1: Filter appointments that have a non-null and non-empty medication
        List<Appointment> appointmentsWithPrescriptions = new ArrayList<>();
        for (Appointment appointment : AppointmentManager.getAppointments()) {
            if (appointment.getOutcome() != null && appointment.getOutcome().getMedication() != null) {
                appointmentsWithPrescriptions.add(appointment);
            }
        }

        // Step 2: Check if there are any appointments with prescriptions
        if (appointmentsWithPrescriptions.isEmpty()) {
            System.out.println("No appointments with prescriptions to update.");
            return;
        }

        // Step 3: Display the filtered appointments with prescription details
        System.out.println("\nAppointments with Prescriptions:");
        for (int i = 0; i < appointmentsWithPrescriptions.size(); i++) {
            Appointment appointment = appointmentsWithPrescriptions.get(i);
            System.out.println((i + 1) + ". Medication: " + appointment.getOutcome().getMedication());
            System.out.println("   Current Medication Status: " + appointment.getOutcome().getMedStatus());
            System.out.println("--------------------------------------");
        }

        // Step 4: Let the user select which appointment to update
        System.out.print("Select the appointment number to update the medication status (or 0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        // Step 5: Validate the user's choice
        if (choice == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (choice < 1 || choice > appointmentsWithPrescriptions.size()) {
            System.out.println("Invalid selection. Returning to menu.");
            return;
        }

        // Step 6: Update the selected appointment's medication status
        Appointment selectedAppointment = appointmentsWithPrescriptions.get(choice - 1);
        System.out.print("Enter the new medication status: ");
        String newStatus = scanner.nextLine();

        // Update the status in the appointment outcome
        selectedAppointment.getOutcome().setMedStatus(newStatus);
        System.out.println("Medication status updated successfully!");
    }


    private void viewMedicationInventory() {
        inventoryManagement.printInventory();
    }

    private void checkStockLevels() {
        inventoryManagement.checkStockLevels(); // Call to check stock levels
    }

    private void submitReplenishmentRequest(Scanner scanner) {
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Medicine Name for replenishment request: ");
        String name = scanner.nextLine();

        inventoryManagement.submitReplenishmentRequest(name); // Call to submit replenishment request
    }

    
}

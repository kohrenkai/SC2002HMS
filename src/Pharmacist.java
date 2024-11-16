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

        
            do {
                System.out.println("\nPharmacist Menu:");
                System.out.println("1. View and Update Appointment Outcome Record");
                System.out.println("2. View Medication Inventory");
                System.out.println("3. Check Stock Levels");
                System.out.println("4. Submit Replenishment Request");
                System.out.println("5. Logout");
                System.out.print("Enter your choice: ");

                while (!scanner.hasNextInt()) { // Input validation
                    System.out.print("Invalid input. Please enter a number: ");
                    scanner.next(); // Clear invalid input
                }

                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        viewAndUpdateAppointmentOutcomeRecord(scanner);
                        break;
                    case 2:
                        viewMedicationInventory();
                        break;
                    case 3:
                        checkStockLevels();
                        break;
                    case 4:
                        submitReplenishmentRequest(scanner);
                        break;
                    case 5:
                        logout();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 5);
        
    }

    private void viewAndUpdateAppointmentOutcomeRecord(Scanner scanner) {
        List<Appointment> appointmentsWithPrescriptions = new ArrayList<>();
        for (Appointment appointment : AppointmentManager.getAppointments()) {
            if (appointment.getOutcome() != null && appointment.getOutcome().getMedication() != null && !appointment.getOutcome().getMedication().isEmpty()) {
                appointmentsWithPrescriptions.add(appointment);
            }
        }

        if (appointmentsWithPrescriptions.isEmpty()) {
            System.out.println("No appointments with prescriptions to update.");
            return;
        }

        System.out.println("\nAppointments with Prescriptions:");
        for (int i = 0; i < appointmentsWithPrescriptions.size(); i++) {
            Appointment appointment = appointmentsWithPrescriptions.get(i);
            System.out.println((i + 1) + ". Medication: " + appointment.getOutcome().getMedication());
            System.out.println("   Current Medication Status: " + appointment.getOutcome().getMedStatus());
            System.out.println("   Patient ID: " + appointment.getPatientId());
            System.out.println("   Doctor ID: " + appointment.getDoctorId());
            System.out.println("   Date: " + appointment.getDate());
            System.out.println("   Time: " + appointment.getTimeSlot());
            System.out.println("--------------------------------------");
        }

        System.out.print("Select the appointment number to update the medication status (or 0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (choice == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (choice < 1 || choice > appointmentsWithPrescriptions.size()) {
            System.out.println("Invalid selection. Returning to menu.");
            return;
        }

        Appointment selectedAppointment = appointmentsWithPrescriptions.get(choice - 1);
        String medicationName = selectedAppointment.getOutcome().getMedication();

        System.out.print("Enter the quantity to dispense: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (inventoryManagement.checkStock(medicationName, quantity)) {
            inventoryManagement.dispenseMedication(medicationName, quantity);
            selectedAppointment.getOutcome().setMedStatus("Dispensed");
            System.out.println("Medication status updated to 'Dispensed' and quantity adjusted in inventory.");
            AppointmentManager.saveAppointmentsToCSV(); // Save the updated status to CSV
        } else {
            System.out.println("Insufficient stock for " + medicationName + ". Please check inventory and try again.");
        }
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
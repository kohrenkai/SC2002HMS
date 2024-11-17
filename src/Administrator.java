import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class Administrator extends User {
    private CSVUtility csvUtility;
    private InventoryManagement inventoryManagement;
    private StaffManager staffManager;
    public Administrator(String userID, String hashedPassword, String salt, String name) {
        super(userID, hashedPassword, salt, name, User.Role.ADMINISTRATOR);
        this.csvUtility = new CSVUtility("Staff_List.csv");
        this.inventoryManagement = new InventoryManagement("Medicine_List.csv");
        this.staffManager = new StaffManagement(csvUtility);
    }

    @Override

    public void displayRoleSpecificMenu() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("\nAdministrator Menu:");
                System.out.println("1. View and Manage Staff");
                System.out.println("2. View Appointments");
                System.out.println("3. Manage Inventory");
                System.out.println("4. Approve Replenishment Requests");
                System.out.println("5. Return to main menu");

                System.out.print("Enter your choice: ");
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1:
                        	staffManager.viewAndManageStaff();
                            break;
                        case 2:
                            viewAppointments();
                            break;
                        case 3:
                            manageInventory();
                            break;
                        case 4:
                            approveReplenishmentRequest();
                            break;
                        case 5:
                            logout();
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Consume the invalid input
                }
            }
        } finally {
            
        }
    }




    public void viewAppointments() {
        System.out.println("Viewing appointments...");
        List<Appointment> appointments = AppointmentManager.getAppointments();
        
        System.out.println("Scheduled Appointments:");
        for (Appointment appointment : appointments) {
            System.out.println("\nPatient ID: " + (appointment.getPatientId() != null ? appointment.getPatientId() : "N/A"));
            System.out.println("Doctor ID: " + (appointment.getDoctorId() != null ? appointment.getDoctorId() : "N/A"));
            System.out.println("Status: " + appointment.getStatus());
            System.out.println("Date: " + appointment.getDate());
            System.out.println("Time: " + appointment.getTimeSlot());
            
            // Display outcome details if the appointment is completed
            if (appointment.getStatus() == Appointment.Status.COMPLETED && appointment.getOutcome() != null) {
                System.out.println("Appointment Outcome:");
                appointment.getOutcome().displayOutcome();
            }
        }
    }
    
    

       

    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);
       
            while (true) {
                System.out.println("\nInventory Management Menu:");
                System.out.println("1. Add Medication");
                System.out.println("2. Remove Medication");
                System.out.println("3. Update Stock Level");
                System.out.println("4. Update Low Stock Level");
                System.out.println("5. Print Inventory");
                System.out.println("6. Exit");

                System.out.print("Enter your choice: ");
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1:
                            System.out.print("Enter Medication Name: ");
                            String name = scanner.nextLine();
                            System.out.print("Enter Initial Stock: ");
                            if (scanner.hasNextInt()) {
                                int initialStock = scanner.nextInt();
                                System.out.print("Enter Low Stock Level: ");
                                if (scanner.hasNextInt()) {
                                    int lowStockLevel = scanner.nextInt();
                                    scanner.nextLine(); // Consume newline
                                    inventoryManagement.addMedication(name, initialStock, lowStockLevel);
                                } else {
                                    System.out.println("Invalid input for Low Stock Level. Please enter a number.");
                                    scanner.next(); // Consume invalid input
                                }
                            } else {
                                System.out.println("Invalid input for Initial Stock. Please enter a number.");
                                scanner.next(); // Consume invalid input
                            }
                            break;

                        case 2:
                            System.out.print("Enter Medication Name to Remove: ");
                            String removeName = scanner.nextLine();
                            inventoryManagement.removeMedication(removeName);
                            break;

                        case 3:
                            System.out.print("Enter Medication Name to Update Stock Level: ");
                            String updateName = scanner.nextLine();
                            System.out.print("Enter New Stock Level: ");
                            if (scanner.hasNextInt()) {
                                int newStockLevel = scanner.nextInt();
                                scanner.nextLine(); // Consume newline
                                inventoryManagement.updateStockLevel(updateName, newStockLevel);
                            } else {
                                System.out.println("Invalid input for New Stock Level. Please enter a number.");
                                scanner.next(); // Consume invalid input
                            }
                            break;

                        case 4:
                            System.out.print("Enter Medication Name to Update Low Stock Level: ");
                            String updateLowName = scanner.nextLine();
                            System.out.print("Enter New Low Stock Level: ");
                            if (scanner.hasNextInt()) {
                                int newLowStockLevel = scanner.nextInt();
                                scanner.nextLine(); // Consume newline
                                inventoryManagement.updateLowStockLevel(updateLowName, newLowStockLevel);
                            } else {
                                System.out.println("Invalid input for New Low Stock Level. Please enter a number.");
                                scanner.next(); // Consume invalid input
                            }
                            break;

                        case 5:
                            inventoryManagement.printInventory();
                            break;

                        case 6:
                            System.out.println("Exiting inventory management...");
                            return;

                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Consume invalid input
                }
            }
         
    }


    public void approveReplenishmentRequest() {
    	 inventoryManagement.approveReplenishmentRequest();        
    }

    



    
}


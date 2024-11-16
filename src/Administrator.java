import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class Administrator extends User {
    private CSVUtility csvUtility;
    private InventoryManagement inventoryManagement;

    public Administrator(String userID, String hashedPassword, String salt, String name) {
        super(userID, hashedPassword, salt, name, User.Role.ADMINISTRATOR);
        this.csvUtility = new CSVUtility("Staff_List.csv");
        this.inventoryManagement = new InventoryManagement("Medicine_List.csv");
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
                            viewAndManageStaff();
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


    public void viewAndManageStaff() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("\nStaff Management Menu:");
                System.out.println("1. Add Staff Member");
                System.out.println("2. Update Staff Member");
                System.out.println("3. Remove Staff Member");
                System.out.println("4. Print Staff Data");
                System.out.println("5. Sort Staff by Gender");
                System.out.println("6. Sort Staff by Age");
                System.out.println("7. Sort Staff by Role");
                System.out.println("8. Exit");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter Staff ID: ");
                        String newID = scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter Role (Doctor/Pharmacist): ");
                        String newRole = scanner.nextLine();
                        System.out.print("Enter Gender: ");
                        String newGender = scanner.nextLine();
                        System.out.print("Enter Age: ");
                        String newAge = scanner.nextLine();
                        String[] newStaff = {newID, newName, newRole, newGender, newAge};
                        addStaffMember(newStaff);
                        break;

                    case 2:
                        System.out.print("Enter Staff ID to update: ");
                        String updateID = scanner.nextLine();
                        System.out.print("Enter New Name: ");
                        String updatedName = scanner.nextLine();
                        System.out.print("Enter New Role (Doctor/Pharmacist): ");
                        String updatedRole = scanner.nextLine();
                        System.out.print("Enter New Gender: ");
                        String updatedGender = scanner.nextLine();
                        System.out.print("Enter New Age: ");
                        String updatedAge = scanner.nextLine();
                        String[] updatedInfo = {updateID, updatedName, updatedRole, updatedGender, updatedAge};
                        updateStaffMember(updateID, updatedInfo);
                        break;

                    case 3:
                        System.out.print("Enter Staff ID to remove: ");
                        String removeID = scanner.nextLine();
                        removeStaffMember(removeID);
                        break;

                    case 4:
                        List<String[]> staffData = csvUtility.readCSV();
                        printStaffData(staffData);
                        break;

                    case 5:
                        sortStaffByGender();
                        break;

                    case 6:
                        sortStaffByAge();
                        break;

                    case 7:
                        sortStaffByRole();
                        break;

                    case 8:
                        System.out.println("Exiting staff management...");
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } finally {
            
        }
    }



    public void addStaffMember(String[] newStaff) {
        List<String[]> staffData = csvUtility.readCSV();
        boolean userIDExists = staffData.stream().anyMatch(staff -> staff[0].equals(newStaff[0]));

        if (userIDExists) {
            System.out.println("Error: UserID already exists.");
        } else {
            // Generate a default password
            String defaultPassword = "password";
            // Generate salt
            byte[] salt = PasswordUtils.generateSalt();
            String hashedPassword = null;
            try {
                // Hash the default password
                hashedPassword = PasswordUtils.hashPassword(defaultPassword, salt);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
            // Add hashed password and salt to newStaff array
            String[] newStaffWithPassword = Arrays.copyOf(newStaff, newStaff.length + 2);
            newStaffWithPassword[newStaff.length] = hashedPassword.split(":")[0]; // Store only the hash
            newStaffWithPassword[newStaff.length + 1] = hashedPassword.split(":")[1]; // Store only the salt
            staffData.add(newStaffWithPassword);
            csvUtility.writeCSV(staffData);
            System.out.println("Staff member added successfully!");
        }
    }




    public void updateStaffMember(String staffID, String[] updatedInfo) {
        List<String[]> staffData = csvUtility.readCSV();
        for (String[] staff : staffData) {
            if (staff[0].equals(staffID)) {
                // Ensure the array has enough space for hashed password and salt
                if (staff.length < updatedInfo.length + 2) {
                    staff = Arrays.copyOf(staff, updatedInfo.length + 2);
                }
                for (int i = 0; i < updatedInfo.length; i++) {
                    staff[i] = updatedInfo[i];
                }
                break;
            }
        }
        csvUtility.writeCSV(staffData);
        System.out.println("Staff member updated successfully!");
    }


    public void removeStaffMember(String staffID) {
        List<String[]> staffData = csvUtility.readCSV();
        staffData.removeIf(staff -> staff[0].equals(staffID));
        csvUtility.writeCSV(staffData);
        System.out.println("Staff member removed successfully!");
        // this one doesnt show if the the staff member wasnt there in the first placce
    }
    
    public void sortStaffByGender() {
        List<String[]> staffData = csvUtility.readCSV();
        if (staffData.size() > 1) {
            List<String[]> dataToSort = staffData.subList(1, staffData.size());
            dataToSort.sort((staff1, staff2) -> staff1[3].compareTo(staff2[3])); // Assuming gender is at index 3
        }
        csvUtility.writeCSV(staffData);
        System.out.println("Staff sorted by gender successfully!");
    }


    public void sortStaffByAge() {
        List<String[]> staffData = csvUtility.readCSV();
        if (staffData.size() > 1) {
            List<String[]> dataToSort = staffData.subList(1, staffData.size());
            dataToSort.sort((staff1, staff2) -> {
                try {
                    int age1 = Integer.parseInt(staff1[4]);
                    int age2 = Integer.parseInt(staff2[4]);
                    return Integer.compare(age1, age2);
                } catch (NumberFormatException e) {
                    // Handle the case where the age is not a valid integer
                    return 0;
                }
            });
        }
        csvUtility.writeCSV(staffData);
        System.out.println("Staff sorted by age successfully!");
    }

    
    public void sortStaffByRole() {
        List<String[]> staffData = csvUtility.readCSV();
        if (staffData.size() > 1) {
            List<String[]> dataToSort = staffData.subList(1, staffData.size());
            dataToSort.sort((staff1, staff2) -> staff1[2].compareTo(staff2[2])); // Assuming role is at index 2
        }
        csvUtility.writeCSV(staffData);
        System.out.println("Staff sorted by role successfully!");
    }


    public void viewAppointments() {
        System.out.println("Viewing appointments...");
        List<Appointment> patientAppointments = AppointmentManager.getAppointments();
        boolean hasOutcomes = false;
        
        System.out.println("Appointment Outcomes");
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
    
    

       

    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);
        try {
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
        } finally {
            scanner.close();
        }
    }


    public void approveReplenishmentRequest() {
    	 inventoryManagement.approveReplenishmentRequest();        
    }

    private void printStaffData(List<String[]> staffData) {
        if (staffData.isEmpty()) {
            System.out.println("No staff data available.");
            return;
        }

        // Get the headers
        String[] headers = staffData.get(0);
        List<Integer> excludeIndices = new ArrayList<>();

        // Find indices of "Hashed Password" and "Salt"
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase("Hashed Password") || headers[i].equalsIgnoreCase("Salt")) {
                excludeIndices.add(i);
            }
        }

        // Print headers excluding "Hashed Password" and "Salt"
        for (int i = 0; i < headers.length; i++) {
            if (!excludeIndices.contains(i)) {
                System.out.print(headers[i] + "\t");
            }
        }
        System.out.println();

        // Print staff data excluding "Hashed Password" and "Salt"
        for (int rowIndex = 1; rowIndex < staffData.size(); rowIndex++) {
            String[] row = staffData.get(rowIndex);
            for (int i = 0; i < row.length; i++) {
                if (!excludeIndices.contains(i)) {
                    System.out.print(row[i] + "\t");
                }
            }
            System.out.println();
        }
    }



    
}


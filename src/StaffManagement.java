import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * Implementation of the StaffManager interface for managing staff.
 */
public class StaffManagement implements StaffManager {
    private CSVUtility csvUtility;

    public StaffManagement(CSVUtility csvUtility) {
        this.csvUtility = csvUtility;
    }

    @Override
    public void viewAndManageStaff() {
        Scanner scanner = new Scanner(System.in);
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
                    addStaffMemberPrompt(scanner);
                    break;

                case 2:
                    updateStaffMemberPrompt(scanner);
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
    }

    private void addStaffMemberPrompt(Scanner scanner) {
        String[] newStaff = new String[5];

        // Validate staff ID and role
        while (true) {
            System.out.print("Enter Staff ID (must start with 'D' for Doctor or 'P' for Pharmacist): ");
            newStaff[0] = scanner.nextLine();
            if (newStaff[0].startsWith("D")) {
                newStaff[2] = "Doctor"; // Assuming role is at index 2
                break;
            } else if (newStaff[0].startsWith("P")) {
                newStaff[2] = "Pharmacist"; // Assuming role is at index 2
                break;
            } else {
                System.out.println("Invalid Staff ID. Please try again.");
            }
        }

        // Validate name
        while (true) {
            System.out.print("Enter Name: ");
            newStaff[1] = scanner.nextLine();
            if (newStaff[1].matches("[a-zA-Z\\s]+")) {
                break;
            } else {
                System.out.println("Invalid name. Please enter a valid name.");
            }
        }

        // Validate age
        while (true) {
            System.out.print("Enter Age: ");
            if (scanner.hasNextInt()) {
                newStaff[4] = String.valueOf(scanner.nextInt());
                scanner.nextLine(); // Consume newline
                break;
            } else {
                System.out.println("Invalid age. Please enter a valid integer.");
                scanner.next(); // Consume invalid input
            }
        }

        // Validate gender
        while (true) {
            System.out.print("Enter Gender (Male/Female): ");
            newStaff[3] = scanner.nextLine();
            if (newStaff[3].equalsIgnoreCase("Male") || newStaff[3].equalsIgnoreCase("Female")) {
                break;
            } else {
                System.out.println("Invalid gender. Please enter 'Male' or 'Female'.");
            }
        }

        addStaffMember(newStaff);
    }

    @Override
    public void addStaffMember(String[] newStaff) {
        List<String[]> staffData = csvUtility.readCSV();

        // Check if userID already exists
        boolean userIDExists = staffData.stream().anyMatch(staff -> staff[0].equals(newStaff[0]));
        if (userIDExists) {
            System.out.println("Error: UserID already exists.");
            return;
        }

        // Generate a default password
        String defaultPassword = "PASSWORD";
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

        // Add new staff member to the list and save to CSV
        staffData.add(newStaffWithPassword);
        csvUtility.writeCSV(staffData);
        System.out.println("Staff member added successfully!");
    }

    private void updateStaffMemberPrompt(Scanner scanner) {
        System.out.print("Enter Staff ID to update: ");
        String updateID = scanner.nextLine();
        String[] updatedInfo = new String[5];
        updatedInfo[0] = updateID;

        // Validate name
        while (true) {
            System.out.print("Enter New Name: ");
            updatedInfo[1] = scanner.nextLine();
            if (updatedInfo[1].matches("[a-zA-Z\\s]+")) {
                break;
            } else {
                System.out.println("Invalid name. Please enter a valid name.");
            }
        }

        // Validate role
        while (true) {
            System.out.print("Enter New Role (Doctor/Pharmacist): ");
            updatedInfo[2] = scanner.nextLine();
            if (updatedInfo[2].equalsIgnoreCase("Doctor") || updatedInfo[2].equalsIgnoreCase("Pharmacist")) {
                break;
            } else {
                System.out.println("Invalid role. Please enter 'Doctor' or 'Pharmacist'.");
            }
        }

        // Validate gender
        while (true) {
            System.out.print("Enter New Gender (Male/Female): ");
            updatedInfo[3] = scanner.nextLine();
            if (updatedInfo[3].equalsIgnoreCase("Male") || updatedInfo[3].equalsIgnoreCase("Female")) {
                break;
            } else {
                System.out.println("Invalid gender. Please enter 'Male' or 'Female'.");
            }
        }

        // Validate age
        while (true) {
            System.out.print("Enter New Age: ");
            if (scanner.hasNextInt()) {
                updatedInfo[4] = String.valueOf(scanner.nextInt());
                scanner.nextLine(); // Consume newline
                break;
            } else {
                System.out.println("Invalid age. Please enter a valid integer.");
                scanner.next(); // Consume invalid input
            }
        }

        updateStaffMember(updateID, updatedInfo);
    }

    @Override
    public void updateStaffMember(String staffID, String[] updatedInfo) {
        List<String[]> staffData = csvUtility.readCSV();
        boolean flag = false;
        for (String[] staff : staffData) {
            if (staff[0].equals(staffID)) {
            	flag = true;
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
        if(flag == false) {
        	System.out.println("Staff member does not exist!");
        	return;
        }
        csvUtility.writeCSV(staffData);
        System.out.println("Staff member updated successfully!");
    }

    @Override
    public void removeStaffMember(String staffID) {
        List<String[]> staffData = csvUtility.readCSV();
        boolean removed = staffData.removeIf(staff -> staff[0].equals(staffID));
        csvUtility.writeCSV(staffData);
        if (removed) {
            System.out.println("Staff member removed successfully!");
        } else {
            System.out.println("Staff member not found.");
        }
    }

    @Override
    public void sortStaffByGender() {
        List<String[]> staffData = csvUtility.readCSV();
        if (staffData.size() > 1) {
            List<String[]> dataToSort = staffData.subList(1, staffData.size());
            dataToSort.sort((staff1, staff2) -> staff1[3].compareTo(staff2[3])); // Assuming gender is at index 3
        }
        csvUtility.writeCSV(staffData);
        System.out.println("Staff sorted by gender successfully!");
    }

    @Override
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

    @Override
    public void sortStaffByRole() {
        List<String[]> staffData = csvUtility.readCSV();
        if (staffData.size() > 1) {
            List<String[]> dataToSort = staffData.subList(1, staffData.size());
            dataToSort.sort((staff1, staff2) -> staff1[2].compareTo(staff2[2])); // Assuming role is at index 2
        }
        csvUtility.writeCSV(staffData);
        System.out.println("Staff sorted by role successfully!");
    }

    @Override
    public void printStaffData(List<String[]> staffData) {
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

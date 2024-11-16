import java.util.Scanner;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public abstract class User {
    public enum Role { PATIENT, DOCTOR, PHARMACIST, ADMINISTRATOR }
    protected String userID;
    protected String hashedPassword;
    protected String salt;
    protected String name;
    protected Role role;

    // Constructor to initialize User object with hashed password and salt from CSV
    public User(String userID, String hashedPassword, String salt, String name, Role role) {
        this.userID = userID;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.name = name;
        this.role = role;
    }

    // Method to authenticate user login
    public boolean login(String userID, String password) {
        try {
            if (this.userID.equals(userID) && PasswordUtils.validatePassword(password, this.hashedPassword + ":" + this.salt)) {
                System.out.println("Login successful.");
                return true;
            } else {
                System.out.println("Invalid credentials. Login failed.");
                return false;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error validating user password", e);
        }
    }

    // Method to log out the user
    public void logout() {
        System.out.println("Logging out user: " + userID);
    }

    // Method to change the user's password
    public void changePassword(Scanner scanner, PasswordManager passwordManager) {
        try {
            System.out.println("Enter your current password:");
            String currentPassword = scanner.nextLine();

            if (!PasswordUtils.validatePassword(currentPassword, this.hashedPassword + ":" + this.salt)) {
                System.out.println("Incorrect password. Please try again.");
                return;
            }

            System.out.println("Enter your new password:");
            String newPassword1 = scanner.nextLine();
            System.out.println("Confirm your new password:");
            String newPassword2 = scanner.nextLine();

            if (!newPassword1.equals(newPassword2)) {
                System.out.println("Passwords do not match. Password change failed.");
                return;
            }

            passwordManager.changePassword(this, newPassword1);
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error changing user password", e);
        }
    }

    // Setters for hashed password and salt
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    // Getters for userID, name, and role
    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    // Abstract method to display role-specific menu (to be implemented by subclasses)
    public abstract void displayRoleSpecificMenu();
}

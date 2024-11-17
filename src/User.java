import java.util.Scanner;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * The User class represents a user in the system.
 * It provides methods for user authentication, password management, and role-specific menu display.
 */
public abstract class User {
    /**
     * Enumeration of user roles.
     */
    public enum Role { PATIENT, DOCTOR, PHARMACIST, ADMINISTRATOR }

    /**
     * The user ID of the user.
     */
    protected String userID;

    /**
     * The hashed password of the user.
     */
    protected String hashedPassword;

    /**
     * The salt used for hashing the password.
     */
    protected String salt;

    /**
     * The name of the user.
     */
    protected String name;

    /**
     * The role of the user.
     */
    protected Role role;

    /**
     * Constructs a User object with the specified details.
     *
     * @param userID The user ID of the user.
     * @param hashedPassword The hashed password of the user.
     * @param salt The salt used for hashing the password.
     * @param name The name of the user.
     * @param role The role of the user.
     */
    public User(String userID, String hashedPassword, String salt, String name, Role role) {
        this.userID = userID;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.name = name;
        this.role = role;
    }

    /**
     * Authenticates the user login.
     *
     * @param userID The user ID entered by the user.
     * @param password The password entered by the user.
     * @return true if the login is successful, false otherwise.
     */
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

    /**
     * Logs out the user.
     */
    public void logout() {
        System.out.println("Logging out user: " + userID);
    }

    /**
     * Changes the user's password.
     *
     * @param scanner The Scanner object for reading user input.
     * @param passwordManager The PasswordManager object for managing password changes.
     */
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

    /**
     * Sets the hashed password of the user.
     *
     * @param hashedPassword The new hashed password.
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * Sets the salt used for hashing the password.
     *
     * @param salt The new salt.
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Gets the user ID of the user.
     *
     * @return The user ID.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Gets the name of the user.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the role of the user.
     *
     * @return The role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Displays the role-specific menu for the user.
     * This method is abstract and must be implemented by subclasses.
     */
    public abstract void displayRoleSpecificMenu();
}
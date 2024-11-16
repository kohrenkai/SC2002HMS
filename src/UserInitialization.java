import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Base64;

public class UserInitialization {
    private CSVUtility patientCSV;
    private CSVUtility staffCSV;

    public UserInitialization(CSVUtility patientCSV, CSVUtility staffCSV) {
        this.patientCSV = patientCSV;
        this.staffCSV = staffCSV;
    }

    public void initializeUsers() {
        processUserData(patientCSV, "Patient");
        processUserData(staffCSV, "Staff");
    }

    private void processUserData(CSVUtility csvUtility, String userType) {
        List<String[]> userData = csvUtility.readCSV();
        String[] headers = userData.get(0);

        // Process user data
        for (int i = 1; i < userData.size(); i++) {
            String[] user = userData.get(i);
            String password = "password"; // Default password
            byte[] salt = PasswordUtils.generateSalt();
            String hashedPassword = null;
            try {
                hashedPassword = PasswordUtils.hashPassword(password, salt);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
            userData.set(i, addHashedPasswordAndSalt(user, hashedPassword, salt, headers));
            System.out.println(userType + " UserID: " + user[0] + ", Password: " + password);
        }

        // Write updated data back to CSV file
        csvUtility.writeCSV(userData);
    }

    private String[] addHashedPasswordAndSalt(String[] user, String hashedPassword, byte[] salt, String[] headers) {
        // Find the indices for "Hashed Password" and "Salt"
        int hashedPasswordIndex = -1;
        int saltIndex = -1;

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals("Hashed Password")) {
                hashedPasswordIndex = i;
            } else if (headers[i].equals("Salt")) {
                saltIndex = i;
            }
        }

        // Ensure the user array has enough space for the hashed password and salt
        if (hashedPasswordIndex == -1 || saltIndex == -1) {
            String[] newUser = new String[headers.length];
            System.arraycopy(user, 0, newUser, 0, user.length);
            user = newUser;
            if (hashedPasswordIndex == -1) {
                hashedPasswordIndex = headers.length;
                user[hashedPasswordIndex] = "Hashed Password";
            }
            if (saltIndex == -1) {
                saltIndex = headers.length + 1;
                user[saltIndex] = "Salt";
            }
        }

        // Ensure the user array has enough space for the hashed password and salt
        if (user.length <= hashedPasswordIndex || user.length <= saltIndex) {
            String[] newUser = new String[Math.max(hashedPasswordIndex, saltIndex) + 1];
            System.arraycopy(user, 0, newUser, 0, user.length);
            user = newUser;
        }

        user[hashedPasswordIndex] = hashedPassword;
        user[saltIndex] = Base64.getEncoder().encodeToString(salt);
        return user;
    }
}

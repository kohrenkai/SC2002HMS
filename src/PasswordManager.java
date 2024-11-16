import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

public class PasswordManager {
    private CSVUtility csvUtility;

    public PasswordManager(CSVUtility csvUtility) {
        this.csvUtility = csvUtility;
    }

    public void changePassword(User user, String newPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!isValidPassword(newPassword)) {
            System.out.println("Password must have at least 6 characters, including uppercase, lowercase, numbers, and special characters.");
            return;
        }

        byte[] salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(newPassword, salt);

        List<String[]> data = csvUtility.readCSV();
        String[] headers = data.get(0);
        int hashedPasswordIndex = -1;
        int saltIndex = -1;

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals("Hashed Password")) {
                hashedPasswordIndex = i;
            } else if (headers[i].equals("Salt")) {
                saltIndex = i;
            }
        }

        for (String[] row : data) {
            if (row[0].equals(user.getUserID())) {
                row[hashedPasswordIndex] = hashedPassword;
                row[saltIndex] = Base64.getEncoder().encodeToString(salt);
                break;
            }
        }

        csvUtility.writeCSV(data);
        user.setHashedPassword(hashedPassword);
        user.setSalt(Base64.getEncoder().encodeToString(salt));
    }

    public boolean isValidPassword(String password) {
        if (password.length() < 6) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}

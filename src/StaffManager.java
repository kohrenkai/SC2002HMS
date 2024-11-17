import java.util.List;

public interface StaffManager {
    void viewAndManageStaff();
    void addStaffMember(String[] newStaff);
    void updateStaffMember(String staffID, String[] updatedInfo);
    void removeStaffMember(String staffID);
    void sortStaffByGender();
    void sortStaffByAge();
    void sortStaffByRole();
    void printStaffData(List<String[]> staffData);
}
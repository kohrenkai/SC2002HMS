public class Medication {
    private String name;
    private int stockLevel;
    private int lowStockLevel;
    private boolean replenishmentRequest;

    public Medication(String name, int stockLevel, int lowStockLevel) {
        this.name = name;
        this.stockLevel = stockLevel;
        this.lowStockLevel = lowStockLevel;
        this.replenishmentRequest = false; // Default to false
    }

    // Getters and setters for all fields
    public String getName() {
        return name;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public int getLowStockLevel() {
        return lowStockLevel;
    }

    public void setLowStockLevel(int lowStockLevel) {
        this.lowStockLevel = lowStockLevel;
    }

    public boolean isReplenishmentRequest() {
        return replenishmentRequest;
    }

    public void setReplenishmentRequest(boolean replenishmentRequest) {
        this.replenishmentRequest = replenishmentRequest;
    }
}

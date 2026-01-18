import java.util.ArrayList;

public class Customer {
    private String id;
    private int totalSpent;
    private String loyaltyTier;
    private int employmentCount;
    private ArrayList<String> activeFreelancers;
    private ArrayList<String> blacklist;

    // Fast lookup for blacklist and cancellation tracking
    private MyHashMap blacklistMap;
    private int cancelCount;

    public Customer(String id) {
        this.id = id;
        this.totalSpent = 0;
        this.loyaltyTier = "BRONZE"; // Default tier
        this.employmentCount = 0;
        this.activeFreelancers = new ArrayList<>();
        this.blacklist = new ArrayList<>();
        this.blacklistMap = new MyHashMap();
        this.cancelCount = 0;
    }

    public String getId() { return id; }
    public int getTotalSpent() { return totalSpent; }
    public String getLoyaltyTier() { return loyaltyTier; }
    public int getEmploymentCount() { return employmentCount; }
    public ArrayList<String> getActiveFreelancers() { return activeFreelancers; }

    public void addSpending(int currspend) {
        totalSpent += currspend;
        // Loyalty tier update is deferred to simulate_month
    }

    public void updateLoyaltyTier() {
        // Calculate effective spent by subtracting penalty for cancellations ($250 each)
        long effectiveSpent = (long) totalSpent - ((long) cancelCount * 250);

        if (effectiveSpent < 500) loyaltyTier = "BRONZE";
        else if (effectiveSpent < 2000) loyaltyTier = "SILVER";
        else if (effectiveSpent < 5000) loyaltyTier = "GOLD";
        else loyaltyTier = "PLATINUM";
    }

    public void incrementEmploymentCount() {
        employmentCount++;
    }

    public void addActiveFreelancer(String id) {
        if (!activeFreelancers.contains(id)) activeFreelancers.add(id);
    }

    public void removeActiveFreelancer(String id) {
        activeFreelancers.remove(id);
    }

    public boolean isCurrentlyWorkingWith(String id) {
        return activeFreelancers.contains(id);
    }

    public void blacklistFreelancer(String freelancerID) {
        if (!blacklist.contains(freelancerID)) {
            blacklist.add(freelancerID);
            blacklistMap.put(freelancerID, true);
        }
    }

    public void unblacklistFreelancer(String freelancerID) {
        blacklist.remove(freelancerID);
        blacklistMap.remove(freelancerID);
    }

    public boolean isBlacklisted(String freelancerID) {
        return blacklistMap.containsKey(freelancerID);
    }

    public void incrementCancelCount() {
        cancelCount++;
    }

    public int getSubsidyPercent() {
        switch (loyaltyTier) {
            case "PLATINUM": return 15;
            case "GOLD": return 10;
            case "SILVER": return 5;
            default: return 0;
        }
    }

    public void addToBlacklist(String id) { blacklistFreelancer(id); }
    public void removeFromBlacklist(String id) { unblacklistFreelancer(id); }

    @Override
    public String toString() {
        return id + ": total spent: $" + totalSpent + ", loyalty tier: " + loyaltyTier
                + ", blacklisted freelancer count: " + blacklist.size()
                + ", total employment count: " + employmentCount;
    }
}
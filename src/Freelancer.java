public class Freelancer {
    private String id;
    private String service;
    private int price;

    // Skills
    private int T, C, R, E, A;

    // Stats
    private double avgRating;
    private int ratingCount;
    private int completedCount;
    private int cancelledCountByFreelancer;

    // Monthly tracking
    private int completedThisMonth;
    private int cancelledThisMonth;

    // Status
    private boolean burnout;
    private boolean platformBanned;
    private String currentCustomerId;
    private boolean available;

    // Pending Service Change
    private boolean hasPendingServiceChange;
    private String pendingService;
    private int pendingPrice;

    public Freelancer(String id, String service, int price, int T, int C, int R, int E, int A) {
        this.id = id;
        this.service = service;
        this.price = price;
        this.T = T;
        this.C = C;
        this.R = R;
        this.E = E;
        this.A = A;

        this.avgRating = 5.0; // Default starting rating
        this.ratingCount = 1;

        this.completedCount = 0;
        this.cancelledCountByFreelancer = 0;
        this.completedThisMonth = 0;
        this.cancelledThisMonth = 0;

        this.burnout = false;
        this.platformBanned = false;
        this.currentCustomerId = null;
        this.available = true;

        this.hasPendingServiceChange = false;
        this.pendingService = null;
        this.pendingPrice = 0;
    }

    @Override
    public String toString() {
        double roundedRating = Math.round(avgRating * 10) / 10.0;

        return id
                + ": " + service
                + ", price: " + price
                + ", rating: " + roundedRating
                + ", completed: " + completedCount
                + ", cancelled: " + cancelledCountByFreelancer
                + ", skills: (" + T + "," + C + "," + R + "," + E + "," + A + ")"
                + ", available: " + (available ? "yes" : "no")
                + ", burnout: " + (burnout ? "yes" : "no");
    }

    // --- Getters ---

    public String getId() { return id; }
    public boolean isAvailable() { return available; }
    public boolean isPlatformBanned() { return platformBanned; }
    public boolean isBurnout() { return burnout; }
    public String getService() { return service; }
    public int getPrice() { return price; }
    public double getAvgRating() { return avgRating; }
    public String getCurrentCustomerID() { return currentCustomerId; }

    // --- Job Management ---

    public void setEmployed(String customerId) {
        currentCustomerId = customerId;
        available = false;
    }

    public void setUnemployed() {
        currentCustomerId = null;
        if (!platformBanned) {
            available = true;
        }
    }

    public void completeJob() {
        completedCount++;
        completedThisMonth++;
        setUnemployed();
    }

    public void cancelJobByFreelancer() {
        cancelledCountByFreelancer++;
        cancelledThisMonth++;

        // Ban if cancellations >= 5 in a month
        if (cancelledThisMonth >= 5) {
            platformBanned = true;
            available = false;
        }

        setUnemployed();
    }

    public void addRating(int rating) {
        double sum = avgRating * ratingCount + rating;
        ratingCount++;
        avgRating = sum / ratingCount;
    }

    // --- Monthly Simulation ---

    public void resetMonthlyStatus() {
        cancelledThisMonth = 0;
        completedThisMonth = 0;
    }

    public void updateBurnoutStatus() {
        if (!burnout && completedThisMonth >= 5) {
            burnout = true;
        } else if (burnout && completedThisMonth <= 2) {
            burnout = false;
        }
    }

    // --- Service Change ---

    public void changeService(String newService, int newPrice) {
        this.service = newService;
        this.price = newPrice;
    }

    public void requestServiceChange(String newService, int newPrice) {
        this.pendingService = newService;
        this.pendingPrice = newPrice;
        this.hasPendingServiceChange = true;
    }

    public void applyServiceChange() {
        if (!hasPendingServiceChange) {
            return;
        }
        service = pendingService;
        price = pendingPrice;
        hasPendingServiceChange = false;
    }

    // --- Skills ---

    public void updateSkill(int T, int C, int R, int E, int A) {
        this.T = T;
        this.C = C;
        this.R = R;
        this.E = E;
        this.A = A;
    }

    public void increaseSkill(int index, int amount) {
        switch (index) {
            case 0: T = Math.min(100, T + amount); break;
            case 1: C = Math.min(100, C + amount); break;
            case 2: R = Math.min(100, R + amount); break;
            case 3: E = Math.min(100, E + amount); break;
            case 4: A = Math.min(100, A + amount); break;
        }
    }

    public void applyCancellationPenalty() {
        T = Math.max(0, T - 3);
        C = Math.max(0, C - 3);
        R = Math.max(0, R - 3);
        E = Math.max(0, E - 3);
        A = Math.max(0, A - 3);
    }

    // --- Composite Score ---

    public double calculateSkillScore(int sT, int sC, int sR, int sE, int sA) {
        double product = T * sT + C * sC + R * sR + E * sE + A * sA;
        double sumOfServiceRequirements = sT + sC + sR + sE + sA;

        if (sumOfServiceRequirements == 0) return 0.0;
        return product / (100.0 * sumOfServiceRequirements);
    }

    public double getRatingScore() {
        return avgRating / 5.0;
    }

    public double getReliabilityScore() {
        int totalJobs = completedCount + cancelledCountByFreelancer;
        if (totalJobs == 0) {
            return 1.0;
        } else {
            double fractionCancelled = (double) cancelledCountByFreelancer / totalJobs;
            return 1.0 - fractionCancelled;
        }
    }

    public int getCompositeScore(int sT, int sC, int sR, int sE, int sA) {
        double skillScore = calculateSkillScore(sT, sC, sR, sE, sA);
        double ratingScore = getRatingScore();
        double reliabilityScore = getReliabilityScore();

        double wS = 0.55;
        double wR = 0.25;
        double wL = 0.20;

        double burnoutPenalty = burnout ? 0.45 : 0.0;

        double compositeScoreDouble = 10000 * (wS * skillScore + wR * ratingScore + wL * reliabilityScore - burnoutPenalty);

        return (int) Math.floor(compositeScoreDouble);
    }
}
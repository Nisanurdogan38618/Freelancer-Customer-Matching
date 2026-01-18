import java.util.ArrayList;

public class Platform {

    private MyHashMap customers;
    private MyHashMap freelancers;
    private MyHashMap serviceIndex;

    public Platform() {
        customers = new MyHashMap();
        freelancers = new MyHashMap();
        serviceIndex = new MyHashMap();
    }

    // --- Helper Methods ---

    @SuppressWarnings("unchecked")
    private void addFreelancerSorted(String service, Freelancer f) {
        Object listObj = serviceIndex.get(service);
        ArrayList<Freelancer> list;
        if (listObj == null) {
            list = new ArrayList<>();
            serviceIndex.put(service, list);
        } else {
            list = (ArrayList<Freelancer>) listObj;
        }

        int[] profile = getServiceProfile(service);
        if (profile == null) return;

        int fScore = f.getCompositeScore(profile[0], profile[1], profile[2], profile[3], profile[4]);
        String fID = f.getId();

        // Binary search for insertion index to keep list sorted by score (descending)
        int low = 0;
        int high = list.size() - 1;
        int index = list.size();

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Freelancer midVal = list.get(mid);
            int midScore = midVal.getCompositeScore(profile[0], profile[1], profile[2], profile[3], profile[4]);

            if (fScore > midScore) {
                index = mid;
                high = mid - 1;
            } else if (fScore < midScore) {
                low = mid + 1;
            } else {
                // Tie-breaker: lexicographically smaller ID comes first
                if (fID.compareTo(midVal.getId()) < 0) {
                    index = mid;
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
        }
        list.add(index, f);
    }

    @SuppressWarnings("unchecked")
    private void removeFreelancer(String service, Freelancer f) {
        Object listObj = serviceIndex.get(service);
        if (listObj != null) {
            ArrayList<Freelancer> list = (ArrayList<Freelancer>) listObj;
            list.remove(f);
        }
    }

    private boolean idExists(String id) {
        return customers.containsKey(id) || freelancers.containsKey(id);
    }

    private int[] getServiceProfile(String s) {
        switch (s) {
            case "paint": return new int[]{70, 60, 50, 85, 90};
            case "web_dev": return new int[]{95, 75, 85, 80, 90};
            case "graphic_design": return new int[]{75, 85, 95, 70, 85};
            case "data_entry": return new int[]{50, 50, 30, 95, 95};
            case "tutoring": return new int[]{80, 95, 70, 90, 75};
            case "cleaning": return new int[]{40, 60, 40, 90, 85};
            case "writing": return new int[]{70, 85, 90, 80, 95};
            case "photography": return new int[]{85, 80, 90, 75, 90};
            case "plumbing": return new int[]{85, 65, 60, 90, 85};
            case "electrical": return new int[]{90, 65, 70, 95, 95};
            default: return null;
        }
    }

    private int[] getTop3SkillIndices(int[] svc) {
        int[] idx = {0, 1, 2, 3, 4}; // Indices for T, C, R, E, A
        // Simple selection sort for top 3
        for (int i = 0; i < 3; i++) {
            int best = i;
            for (int j = i + 1; j < 5; j++) {
                if (svc[j] > svc[best] || (svc[j] == svc[best] && j < best)) {
                    best = j;
                }
            }
            int tmp = svc[i]; svc[i] = svc[best]; svc[best] = tmp;
            int ti = idx[i]; idx[i] = idx[best]; idx[best] = ti;
        }
        return new int[]{idx[0], idx[1], idx[2]};
    }

    // --- Core Operations ---

    public String registerCustomer(String id) {
        if (idExists(id)) return "Some error occurred in register customer.";
        customers.put(id, new Customer(id));
        return "registered customer " + id;
    }

    public String registerFreelancer(String id, String service, int price, int T, int C, int R, int E, int A) {
        if (idExists(id)) return "Some error occurred in register freelancer.";
        Freelancer f = new Freelancer(id, service, price, T, C, R, E, A);
        freelancers.put(id, f);
        addFreelancerSorted(service, f);
        return "registered freelancer " + id;
    }

    @SuppressWarnings("unchecked")
    public String requestJob(String customerID, String service, int K) {
        if (!customers.containsKey(customerID)) return "Some error occurred in request_job.";
        int[] svcProfile = getServiceProfile(service);
        if (svcProfile == null) return "Some error occurred in request_job.";

        Customer c = (Customer) customers.get(customerID);
        Object listObj = serviceIndex.get(service);
        if (listObj == null) return "no freelancers available";

        ArrayList<Freelancer> sortedList = (ArrayList<Freelancer>) listObj;
        ArrayList<Freelancer> selected = new ArrayList<>();

        int found = 0;
        int size = sortedList.size();
        for (int i = 0; i < size; i++) {
            if (found >= K) break;
            Freelancer f = sortedList.get(i);
            if (f.isAvailable() && !f.isPlatformBanned() && !c.isBlacklisted(f.getId())) {
                selected.add(f);
                found++;
            }
        }

        if (selected.isEmpty()) return "no freelancers available";

        StringBuilder sb = new StringBuilder();
        sb.append("available freelancers for ").append(service).append(" (top ").append(K).append("):");

        for (Freelancer f : selected) {
            int score = f.getCompositeScore(svcProfile[0], svcProfile[1], svcProfile[2], svcProfile[3], svcProfile[4]);
            // Formatting matches PDF requirement: ID on one line, details on the next indented line
            sb.append("\n").append(f.getId())
                    .append(" - composite: ").append(score)
                    .append(", price: ").append(f.getPrice())
                    .append(", rating: ").append(String.format(java.util.Locale.US, "%.1f", f.getAvgRating()));
        }

        Freelancer best = selected.get(0);
        c.addActiveFreelancer(best.getId());
        c.incrementEmploymentCount();
        best.setEmployed(customerID);

        sb.append("\nauto-employed best freelancer: ").append(best.getId()).append(" for customer ").append(customerID);

        return sb.toString();
    }

    public String completeAndRate(String fID, int rating) {
        if (!freelancers.containsKey(fID)) return "Some error occurred in complete_and_rate.";
        Freelancer f = (Freelancer) freelancers.get(fID);

        String cID = f.getCurrentCustomerID();
        if (cID == null || !customers.containsKey(cID)) return "Some error occurred in complete_and_rate.";
        Customer c = (Customer) customers.get(cID);

        removeFreelancer(f.getService(), f);

        // Calculate payment with subsidy (integer arithmetic)
        int originalPrice = f.getPrice();
        int subsidyPercent = c.getSubsidyPercent();
        int customerPayment = (int) (((long) originalPrice * (100 - subsidyPercent)) / 100);

        c.addSpending(customerPayment);

        f.addRating(rating);
        if (rating >= 4) {
            int[] svc = getServiceProfile(f.getService());
            if (svc != null) {
                int[] top = getTop3SkillIndices(svc);
                f.increaseSkill(top[0], 2);
                f.increaseSkill(top[1], 1);
                f.increaseSkill(top[2], 1);
            }
        }

        f.completeJob();
        c.removeActiveFreelancer(fID);
        addFreelancerSorted(f.getService(), f);

        return fID + " completed job for " + cID + " with rating " + rating;
    }

    public String employFreelancer(String cID, String fID) {
        if (!customers.containsKey(cID) || !freelancers.containsKey(fID)) return "Some error occurred in employ.";
        Customer c = (Customer) customers.get(cID);
        Freelancer f = (Freelancer) freelancers.get(fID);

        if (c.isBlacklisted(fID) || !f.isAvailable() || c.isCurrentlyWorkingWith(fID)) {
            return "Some error occurred in employ.";
        }

        c.addActiveFreelancer(fID);
        c.incrementEmploymentCount();
        f.setEmployed(cID);
        return cID + " employed " + fID + " for " + f.getService();
    }

    public String cancelByCustomer(String cID, String fID) {
        if (!customers.containsKey(cID) || !freelancers.containsKey(fID)) return "Some error occurred in cancel_by_customer.";
        Customer c = (Customer) customers.get(cID);
        Freelancer f = (Freelancer) freelancers.get(fID);

        String currID = f.getCurrentCustomerID();
        if (currID == null || !currID.equals(cID) || !c.isCurrentlyWorkingWith(fID)) {
            return "Some error occurred in cancel_by_customer.";
        }

        c.removeActiveFreelancer(fID);
        c.incrementCancelCount();
        f.setUnemployed();
        return "cancelled by customer: " + cID + " cancelled " + fID;
    }

    public String cancelByFreelancer(String fID) {
        if (!freelancers.containsKey(fID)) return "Some error occurred in cancel_by_freelancer.";
        Freelancer f = (Freelancer) freelancers.get(fID);
        String cID = f.getCurrentCustomerID();

        if (cID == null || !customers.containsKey(cID)) return "Some error occurred in cancel_by_freelancer.";
        Customer c = (Customer) customers.get(cID);
        if (!c.isCurrentlyWorkingWith(fID)) return "Some error occurred in cancel_by_freelancer.";

        removeFreelancer(f.getService(), f);
        boolean wasBanned = f.isPlatformBanned();

        f.addRating(0);
        f.applyCancellationPenalty();
        f.cancelJobByFreelancer();
        c.removeActiveFreelancer(fID);

        if (!f.isPlatformBanned()) {
            addFreelancerSorted(f.getService(), f);
        }

        String res = "cancelled by freelancer: " + fID + " cancelled " + cID;
        if (!wasBanned && f.isPlatformBanned()) {
            res += "\nplatform banned freelancer: " + fID;
        }
        return res;
    }

    public String blacklist(String customerID, String freelancerID) {
        if (!customers.containsKey(customerID) || !freelancers.containsKey(freelancerID)) {
            return "Some error occurred in blacklist.";
        }

        Customer c = (Customer) customers.get(customerID);

        // Return error if already blacklisted
        if (c.isBlacklisted(freelancerID)) {
            return "Some error occurred in blacklist.";
        }

        c.blacklistFreelancer(freelancerID);
        return customerID + " blacklisted " + freelancerID;
    }

    public String unblacklist(String customerID, String freelancerID) {
        if (!customers.containsKey(customerID) || !freelancers.containsKey(freelancerID)) {
            return "Some error occurred in unblacklist.";
        }
        Customer c = (Customer) customers.get(customerID);
        if (!c.isBlacklisted(freelancerID)) {
            return "Some error occurred in unblacklist.";
        }

        c.unblacklistFreelancer(freelancerID);
        return customerID + " unblacklisted " + freelancerID;
    }

    public String changeService(String freelancerID, String newService, int newPrice) {
        if (!freelancers.containsKey(freelancerID)) return "Some error occurred in change_service.";
        if (getServiceProfile(newService) == null) return "Some error occurred in change_service.";

        Freelancer f = (Freelancer) freelancers.get(freelancerID);
        String oldService = f.getService();
        f.requestServiceChange(newService, newPrice);
        return "service change for " + freelancerID + " queued from " + oldService + " to " + newService;
    }

    public String updateSkill(String id, int T, int C, int R, int E, int A) {
        if (!freelancers.containsKey(id) || T < 0 || T > 100 || C < 0 || C > 100 || R < 0 || R > 100 || E < 0 || E > 100 || A < 0 || A > 100) {
            return "Some error occurred in update_skill.";
        }
        Freelancer f = (Freelancer) freelancers.get(id);
        removeFreelancer(f.getService(), f);
        f.updateSkill(T, C, R, E, A);
        addFreelancerSorted(f.getService(), f);
        return "updated skills of " + id + " for " + f.getService();
    }

    public String simulateMonth() {
        serviceIndex = new MyHashMap(); // Reset index
        ArrayList<Object> allF = freelancers.values();

        // Update freelancers and rebuild index
        for (Object o : allF) {
            Freelancer f = (Freelancer) o;
            f.updateBurnoutStatus();
            f.applyServiceChange();
            f.resetMonthlyStatus();
            addFreelancerSorted(f.getService(), f);
        }

        // Update customer loyalty tiers based on effective spending
        ArrayList<Object> allC = customers.values();
        for (Object o : allC) {
            Customer c = (Customer) o;
            c.updateLoyaltyTier();
        }
        return "month complete";
    }

    public String queryCustomer(String id) {
        if (!customers.containsKey(id)) return "Some error occurred in query customer.";
        return customers.get(id).toString();
    }

    public String queryFreelancer(String id) {
        if (!freelancers.containsKey(id)) return "Some error occurred in query freelancer.";
        return freelancers.get(id).toString();
    }

    public String handleCommand(String line) {
        // Redundant if-checks kept for safety, though logic is mostly handled in Main.java switch
        return "Unknown command";
    }
}

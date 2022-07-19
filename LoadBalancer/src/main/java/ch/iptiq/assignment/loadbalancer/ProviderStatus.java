package ch.iptiq.assignment.loadbalancer;

public class ProviderStatus {
    private boolean isExcluded;
    private int successfulConsecutiveHeartBeats;

    public ProviderStatus(boolean isExcluded) {
        this.isExcluded = isExcluded;
        this.successfulConsecutiveHeartBeats = 0;
    }

    public boolean isExcluded() {
        return isExcluded;
    }

    public void setExcluded(boolean excluded) {
        isExcluded = excluded;
    }

    public int getSuccessfulConsecutiveHeartBeats() {
        return successfulConsecutiveHeartBeats;
    }

    public void setSuccessfulConsecutiveHeartBeats(int successfulConsecutiveHeartBeats) {
        this.successfulConsecutiveHeartBeats = successfulConsecutiveHeartBeats;
    }
}

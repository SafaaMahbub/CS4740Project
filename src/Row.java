public class Row {
    long timestamp;
    String collectionID;
    int eventType;
    long startTime;
    long endTime;
    double avgUsageCPU;
    double avgUsageMemory;
    double maxUsageCpu;
    double maxUsageMemory;
    double assignedMemory;

    public Row(long timestamp ,int eventType, String collectionID, long startTime, long endTime, double avgUsageCPU, double avgUsageMemory, double maxUsageCpu, double maxUsageMemory, double assignedMemory) {
        this.timestamp = timestamp;
        this.collectionID = collectionID;
        this.eventType = eventType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.avgUsageCPU = avgUsageCPU;
        this.avgUsageMemory = avgUsageMemory;
        this.maxUsageCpu = maxUsageCpu;
        this.maxUsageMemory = maxUsageMemory;
        this.assignedMemory = assignedMemory;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(String collectionID) {
        this.collectionID = collectionID;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getAvgUsageCPU() {
        return avgUsageCPU;
    }

    public void setAvgUsageCPU(double avgUsageCPU) {
        this.avgUsageCPU = avgUsageCPU;
    }

    public double getAvgUsageMemory() {
        return avgUsageMemory;
    }

    public void setAvgUsageMemory(double avgUsageMemory) {
        this.avgUsageMemory = avgUsageMemory;
    }

    public double getMaxUsageCpu() {
        return maxUsageCpu;
    }

    public void setMaxUsageCpu(double maxUsageCpu) {
        this.maxUsageCpu = maxUsageCpu;
    }

    public double getMaxUsageMemory() {
        return maxUsageMemory;
    }

    public void setMaxUsageMemory(double maxUsageMemory) {
        this.maxUsageMemory = maxUsageMemory;
    }

    public double getAssignedMemory() {
        return assignedMemory;
    }

    public void setAssignedMemory(double assignedMemory) {
        this.assignedMemory = assignedMemory;
    }

    @Override
    public String toString() {
        return "Row{" +
                "timestamp=" + timestamp +
                ", collectionID=" + collectionID +
                ", eventType=" + eventType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", avgUsageCPU=" + avgUsageCPU +
                ", avgUsageMemory=" + avgUsageMemory +
                ", maxUsageCpu=" + maxUsageCpu +
                ", maxUsageMemory=" + maxUsageMemory +
                ", assignedMemory=" + assignedMemory +
                '}';
    }
}

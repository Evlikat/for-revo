package net.evlikat.revo.web;

/**
 * TransferTask.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class TransferTask {

    private long sourceId;
    private long destinationId;
    private long amount;

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(long destinationId) {
        this.destinationId = destinationId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}

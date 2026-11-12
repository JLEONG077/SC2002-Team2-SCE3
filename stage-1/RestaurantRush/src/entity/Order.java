package entity;

public class Order {
	private MenuItem item;
    private int lockedPriceCents;
    private int unitsDone;
    private int readyOnTurn;
    private OrderStatus status;

    public Order(MenuItem item, int lockedPriceCents) {
        this.item = item;
        this.lockedPriceCents = lockedPriceCents;
        this.unitsDone = 0;
        this.readyOnTurn = -1;
        this.status = OrderStatus.PREPARING;
    }

    public boolean advance(int turn) {
        if (status != OrderStatus.PREPARING) {
            return false;
        }
        unitsDone++;
        if (unitsDone >= item.getPrepUnits()) {
            status = OrderStatus.READY;
            readyOnTurn = turn;
            return true;
        }
        return false;
    }

    public void markServed() {
        this.status = OrderStatus.READY;
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getLockedPriceCents() {
        return lockedPriceCents;
    }

    public int getUnitsDone() {
        return unitsDone;
    }

    public int getReadyOnTurn() {
        return readyOnTurn;
    }

    public OrderStatus getStatus() {
        return status;
    }
}

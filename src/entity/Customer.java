package entity;

import java.util.List;

public abstract class Customer {
    private static int idCounter = 1;
    protected int id;
    protected int satisfaction;
    protected int arrivalTurn;
    protected CustomerState state;
    protected Order order;
    protected int servedTurn = -1;

    public Customer(int arrivalTurn) {
        this.id = idCounter++;
        this.satisfaction = 100;
        this.arrivalTurn = arrivalTurn;
        this.state = CustomerState.WAITING;
        this.order = null;
    }

    public abstract int lossPerTurn();
    public abstract int discount(int priceCents);
    public abstract MenuItem chooseItem(List<MenuItem> menu);

    public void loseSatisfaction() {
        satisfaction -= lossPerTurn();
        if (satisfaction < 0) {
            satisfaction = 0;
        }
    }

    public void addSatisfaction(int amount) {
        satisfaction += amount;
        if (satisfaction > 100) {
            satisfaction = 100;
        }
    }

    public void cancel() {
        this.state = CustomerState.CANCELLED;
        if (this.order != null) {
            this.order.cancel();
        }
    }

    public boolean hasLeft() {
        return satisfaction <= 0 || state == CustomerState.CANCELLED || state == CustomerState.PAID;
    }

    public int getId() {
        return id;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    public int getServedTurn() {
        return servedTurn;
    }

    public void markServed(int turn) {
        this.servedTurn = turn;
        this.state = CustomerState.SERVED;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    /** True while the customer is waiting for a table or for their food to be served. */
    public boolean isAwaitingService() {
        return state == CustomerState.WAITING || state == CustomerState.SEATED
            || state == CustomerState.ORDERED || state == CustomerState.PREPARING
            || state == CustomerState.READY;
    }

    public boolean hasLeftOrPaid() {
        return state == CustomerState.PAID || state == CustomerState.CANCELLED;
    }
}

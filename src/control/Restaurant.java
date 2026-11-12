package control;

import entity.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Restaurant {
    private int revenueCents;
    private List<Table> tables;
    private List<Staff> staff;
    private Queue<Customer> queue;
    private List<Customer> activeCustomers;
    private List<MenuItem> menu;

    public Restaurant() {
        this.revenueCents = 0;
        this.tables = new ArrayList<>();
        for (int i = 1; i <= GameConfig.TABLE_COUNT; i++) tables.add(new Table(i));
        this.staff = new ArrayList<>();
        this.staff.add(new Waiter("Alice"));
        this.staff.add(new Chef("Bob"));
        this.queue = new LinkedList<>();
        this.activeCustomers = new ArrayList<>();
        
        this.menu = new ArrayList<>();
        this.menu.add(new MenuItem("Burger", 1200, 2));
        this.menu.add(new MenuItem("Fries", 600, 1));
        this.menu.add(new MenuItem("Drink", 400, 1));
    }

    public void enqueue(Customer c) { queue.add(c); activeCustomers.add(c); }
    public Customer findCustomerInQueue(int id) {
        for (Customer c : queue) if (c.getId() == id) return c;
        return null;
    }
    public Table findFreeTable() {
        for (Table t : tables) if (t.isFree()) return t;
        return null;
    }
    public ActionResult seat(Customer c, Table t) {
        if (!queue.contains(c)) return new ActionResult(false, "Customer not in queue.");
        if (!t.isFree()) return new ActionResult(false, "Table not free.");
        queue.remove(c);
        t.occupy(c);
        c.setState(CustomerState.SEATED);
        return new ActionResult(true, "Seated " + c.getClass().getSimpleName() + " #" + c.getId() + " at Table #" + t.getId());
    }
    public Customer findSeatedUnorderedCustomer(int id) {
        for (Table t : tables) {
            if (!t.isFree() && t.getOccupant().getId() == id && t.getOccupant().getState() == CustomerState.SEATED) return t.getOccupant();
        }
        return null;
    }
    public Customer findCustomerWithReadyOrder(int id) {
        for (Table t : tables) {
            if (!t.isFree()) {
                Customer c = t.getOccupant();
                if (c.getId() == id && c.getState() == CustomerState.READY) return c;
            }
        }
        return null;
    }
    public List<Order> getPreparingOrders() {
        List<Order> list = new ArrayList<>();
        for (Customer c : activeCustomers) {
            if (c.getOrder() != null && c.getOrder().getStatus() == OrderStatus.PREPARING) list.add(c.getOrder());
        }
        return list;
    }
    public Customer findCustomerByOrder(Order o) {
        for (Customer c : activeCustomers) if (c.getOrder() == o) return c;
        return null;
    }
    public Customer getOldestReadyToPayCustomer() {
        Customer oldest = null;
        for (Table t : tables) {
            if (!t.isFree() && t.getOccupant().getState() == CustomerState.READY_TO_PAY) {
                if (oldest == null || t.getOccupant().getArrivalTurn() < oldest.getArrivalTurn()) oldest = t.getOccupant();
            }
        }
        return oldest;
    }
    public void recordPayment(Customer c) {
        if (c.getOrder() != null) revenueCents += c.getOrder().getLockedPriceCents();
        for (Table t : tables) { if (t.getOccupant() == c) { t.release(); break; } }
    }
    public float averageSatisfaction() {
        if (activeCustomers.isEmpty()) return 100.0f;
        int sum = 0;
        for (Customer c : activeCustomers) sum += c.getSatisfaction();
        return (float) sum / activeCustomers.size();
    }
    public int getPaidCount() {
        int count = 0;
        for (Customer c : activeCustomers) if (c.getState() == CustomerState.PAID) count++;
        return count;
    }
    public List<Table> getTables() { return tables; }
    public Queue<Customer> getQueue() { return queue; }
    public List<Customer> getActiveCustomers() { return activeCustomers; }
    public List<MenuItem> getMenu() { return menu; }
    public int getRevenueCents() { return revenueCents; }
    public Waiter getWaiter() { for (Staff s : staff) if (s instanceof Waiter) return (Waiter) s; return null; }
    public Chef getChef() { for (Staff s : staff) if (s instanceof Chef) return (Chef) s; return null; }
}
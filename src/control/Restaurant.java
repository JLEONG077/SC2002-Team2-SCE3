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
    private int happyHourTurnsRemaining = 0;
    private boolean happyHourUsed = false;

    public Restaurant() {
        this.revenueCents = 0;
        this.tables = new ArrayList<>();
        for (int i = 1; i <= GameConfig.TABLE_COUNT; i++) {
            tables.add(new Table(i));
        }
        this.staff = new ArrayList<>();
        this.staff.add(new Waiter("Alice"));
        this.staff.add(new Chef("Bob"));
        this.staff.add(new Host("Charlie")); // Host added for Stage 3
        
        this.queue = new LinkedList<>();
        this.activeCustomers = new ArrayList<>();
        
        this.menu = new ArrayList<>();
        MenuItem salad = new MenuItem("Salad", 900, 1);
        MenuItem burger = new MenuItem("Burger", 1500, 1);
        MenuItem pasta = new MenuItem("Pasta", 1800, 2);
        this.menu.add(salad);
        this.menu.add(burger);
        this.menu.add(pasta);
        this.menu.add(new ComboMeal(salad, pasta)); // Combo Meal

        this.happyHourTurnsRemaining = 0;
        this.happyHourUsed = false;
    }

    public void enqueue(Customer c) {
        queue.add(c);
        activeCustomers.add(c);
    }

    public Customer findCustomerInQueue(int id) {
        for (Customer c : queue) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public Table findFreeTable() {
        for (Table t : tables) {
            if (t.isFree()) return t;
        }
        return null;
    }

    public ActionResult seat(Customer c, Table t) {
        if (!queue.contains(c)) {
            return new ActionResult(false, "Customer is not in the queue.");
        }
        if (!t.isFree()) {
            return new ActionResult(false, "Table is not free.");
        }
        queue.remove(c);
        t.occupy(c);
        c.setState(CustomerState.SEATED);
        return new ActionResult(true, "Seated Customer #" + c.getId() + " at Table #" + t.getId());
    }

    public Customer findSeatedUnorderedCustomer(int id) {
        for (Table t : tables) {
            if (!t.isFree()) {
                Customer c = t.getOccupant();
                if (c.getId() == id && c.getState() == CustomerState.SEATED) return c;
            }
        }
        return null;
    }

    public Customer findCustomerWithReadyOrder(int id) {
        for (Table t : tables) {
            if (!t.isFree()) {
                Customer c = t.getOccupant();
                if (c.getId() == id && c.getState() == CustomerState.READY && c.getOrder() != null && c.getOrder().getStatus() == OrderStatus.READY) return c;
            }
        }
        return null;
    }

    public List<Order> getPreparingOrders() {
        List<Order> list = new ArrayList<>();
        for (Customer c : activeCustomers) {
            if (c.getOrder() != null && c.getOrder().getStatus() == OrderStatus.PREPARING) {
                list.add(c.getOrder());
            }
        }
        return list;
    }

    public Customer findCustomerByOrder(Order o) {
        for (Customer c : activeCustomers) {
            if (c.getOrder() == o) return c;
        }
        return null;
    }

    public Customer getOldestReadyToPayCustomer() {
        Customer oldest = null;
        for (Table t : tables) {
            if (!t.isFree()) {
                Customer c = t.getOccupant();
                if (c.getState() == CustomerState.READY_TO_PAY) {
                    if (oldest == null || c.getArrivalTurn() < oldest.getArrivalTurn()) {
                        oldest = c;
                    }
                }
            }
        }
        return oldest;
    }

    public void recordPayment(Customer c) {
        if (c.getOrder() != null) {
            revenueCents += c.getOrder().getLockedPriceCents();
        }
        for (Table t : tables) {
            if (t.getOccupant() == c) {
                t.release();
                break;
            }
        }
    }

    public float averageSatisfaction() {
        if (activeCustomers.isEmpty()) return 100.0f;
        int sum = 0;
        for (Customer c : activeCustomers) {
            sum += c.getSatisfaction();
        }
        return (float) sum / activeCustomers.size();
    }

    public float averagePaidSatisfaction() {
        int sum = 0;
        int count = 0;
        for (Customer c : activeCustomers) {
            if (c.getState() == CustomerState.PAID) {
                sum += c.getSatisfaction();
                count++;
            }
        }
        if (count == 0) return 0.0f;
        return (float) sum / count;
    }

    public int getPaidCount() {
        int count = 0;
        for (Customer c : activeCustomers) {
            if (c.getState() == CustomerState.PAID) count++;
        }
        return count;
    }

    public boolean activateHappyHour() {
        if (happyHourUsed || happyHourTurnsRemaining > 0) return false;
        happyHourUsed = true;
        happyHourTurnsRemaining = 2; // Lasts for that turn and the next
        return true;
    }

    public void decrementHappyHour() {
        if (happyHourTurnsRemaining > 0) {
            happyHourTurnsRemaining--;
        }
    }

    public boolean isHappyHourActive() {
        return happyHourTurnsRemaining > 0;
    }

    public int getHappyHourTurnsRemaining() {
        return happyHourTurnsRemaining;
    }

    public List<Table> getTables() { return tables; }
    public Queue<Customer> getQueue() { return queue; }
    public List<Customer> getActiveCustomers() { return activeCustomers; }
    public List<MenuItem> getMenu() { return menu; }
    public int getRevenueCents() { return revenueCents; }

    public Waiter getWaiter() {
        for (Staff s : staff) {
            if (s instanceof Waiter) return (Waiter) s;
        }
        return null;
    }

    public Chef getChef() {
        for (Staff s : staff) {
            if (s instanceof Chef) return (Chef) s;
        }
        return null;
    }

    public Host getHost() {
        for (Staff s : staff) {
            if (s instanceof Host) return (Host) s;
        }
        return null;
    }
}

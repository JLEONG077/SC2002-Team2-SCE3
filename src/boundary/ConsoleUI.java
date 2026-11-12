package boundary;

import entity.*;

import java.util.List;
import java.util.Scanner;

import control.GameConfig;
import control.Outcome;
import control.Restaurant;
import entity.ActionType;
import entity.Customer;
import entity.Order;
import entity.StaffAction;
import entity.Table;

public class ConsoleUI {
    private Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    public void showTurnStart(Restaurant restaurant, int turn) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("TURN " + turn + " / " + GameConfig.MAX_TURNS);
        System.out.println("--------------------------------------------------");
    }

    public StaffAction promptWaiterAction(Restaurant restaurant) {
        System.out.println("\n--- Waiter Action ---");
        printRestaurantStatus(restaurant);
        System.out.println("Choose Waiter Action:");
        System.out.println("1. SEAT customer (Enter customer ID)");
        System.out.println("2. TAKE_ORDER from seated customer (Enter customer ID)");
        System.out.println("3. SERVE ready order (Enter customer ID)");
        System.out.println("4. WAIT (Pass turn)");
        System.out.print("Enter choice (1-4): ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                System.out.print("Enter Customer ID to seat: ");
                return new StaffAction(ActionType.SEAT, getIntInput());
            case 2:
                System.out.print("Enter Customer ID to take order from: ");
                return new StaffAction(ActionType.TAKE_ORDER, getIntInput());
            case 3:
                System.out.print("Enter Customer ID to serve: ");
                return new StaffAction(ActionType.SERVE, getIntInput());
            case 4:
            default:
                return new StaffAction(ActionType.WAIT, 0);
        }
    }

    public StaffAction promptChefAction(Restaurant restaurant) {
        System.out.println("\n--- Chef Action ---");
        List<Order> preparing = restaurant.getPreparingOrders();
        if (preparing.isEmpty()) {
            System.out.println("No orders currently in preparation.");
        } else {
            System.out.println("Preparing Orders:");
            for (Order o : preparing) {
                Customer c = restaurant.findCustomerByOrder(o);
                System.out.println("  - Customer #" + (c != null ? c.getId() : "?") + " (" + o.getItem().getName() + ") Progress: " + o.getUnitsDone() + "/" + o.getItem().getPrepUnits());
            }
        }
        System.out.println("Choose Chef Action:");
        System.out.println("1. ADVANCE preparation (Enter customer ID, or 0 for first available)");
        System.out.println("2. WAIT (Pass turn)");
        System.out.print("Enter choice (1-2): ");

        int choice = getIntInput();
        if (choice == 1) {
            System.out.print("Enter Customer ID for order to advance (or 0): ");
            return new StaffAction(ActionType.ADVANCE, getIntInput());
        } else {
            return new StaffAction(ActionType.WAIT, 0);
        }
    }

    public void showError(String reason) {
        System.out.println("[ERROR] " + reason);
    }

    public void showTurnSummary(Restaurant restaurant, int turn) {
        System.out.println("\n--- Turn " + turn + " Summary ---");
        System.out.println("Revenue: $" + (restaurant.getRevenueCents() / 100.0) + " | Paid: " + restaurant.getPaidCount() + " | Avg Satisfaction: " + restaurant.averageSatisfaction());
    }

    public void showResult(Outcome outcome, Restaurant restaurant) {
        System.out.println("\n==================================================");
        System.out.println("GAME OVER - RESULT: " + outcome);
        System.out.println("==================================================");
        if (outcome == Outcome.VICTORY) {
            System.out.println("Congratulations! You successfully ran the restaurant!");
        } else {
            System.out.println("Defeat! You didn't meet the target goals for Stage 1.");
        }
    }

    private void printRestaurantStatus(Restaurant restaurant) {
        System.out.println("Waiting Queue:");
        if (restaurant.getQueue().isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            for (Customer c : restaurant.getQueue()) {
                System.out.println("  - Customer #" + c.getId() + " (Arrival: " + c.getArrivalTurn() + ", Sat: " + c.getSatisfaction() + ")");
            }
        }
        System.out.println("Tables:");
        for (Table t : restaurant.getTables()) {
            if (t.isFree()) {
                System.out.println("  - Table #" + t.getId() + ": [Free]");
            } else {
                Customer c = t.getOccupant();
                String orderInfo = c.getOrder() != null ? (c.getOrder().getItem().getName() + " [" + c.getOrder().getStatus() + "]") : "No Order";
                System.out.println("  - Table #" + t.getId() + ": Customer #" + c.getId() + " (Sat: " + c.getSatisfaction() + ", State: " + c.getState() + ", Order: " + orderInfo + ")");
            }
        }
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}

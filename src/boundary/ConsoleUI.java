package boundary;

import control.GameConfig;
import control.Outcome;
import control.Restaurant;
import entity.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner = new Scanner(System.in);

    public void showTurnStart(Restaurant restaurant, int turn) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("TURN " + turn + " / " + GameConfig.MAX_TURNS);
        System.out.println("--------------------------------------------------");
    }

    public StaffAction promptWaiterAction(Restaurant restaurant) {
        System.out.println("\n--- Waiter Action ---");
        printRestaurantStatus(restaurant);
        System.out.println("1. SEAT | 2. TAKE_ORDER | 3. SERVE | 4. WAIT");
        System.out.print("Enter choice (1-4): ");
        int choice = getIntInput();
        switch (choice) {
            case 1: System.out.print("Customer ID to seat: "); return new StaffAction(ActionType.SEAT, getIntInput());
            case 2: System.out.print("Customer ID to take order: "); return new StaffAction(ActionType.TAKE_ORDER, getIntInput());
            case 3: System.out.print("Customer ID to serve: "); return new StaffAction(ActionType.SERVE, getIntInput());
            default: return new StaffAction(ActionType.WAIT, 0);
        }
    }

    public StaffAction promptChefAction(Restaurant restaurant) {
        System.out.println("\n--- Chef Action ---");
        List<Order> preparing = restaurant.getPreparingOrders();
        if (!preparing.isEmpty()) {
            System.out.println("Preparing orders count: " + preparing.size());
        }
        System.out.println("1. ADVANCE preparation | 2. WAIT");
        System.out.print("Enter choice (1-2): ");
        int choice = getIntInput();
        if (choice == 1) {
            return new StaffAction(ActionType.ADVANCE, 0);
        }
        return new StaffAction(ActionType.WAIT, 0);
    }

    public void showError(String reason) { System.out.println("[ERROR] " + reason); }
    public void showTurnSummary(Restaurant restaurant, int turn) {
        System.out.println("Summary -> Revenue: $" + (restaurant.getRevenueCents()/100.0) + " | Paid: " + restaurant.getPaidCount() + " | Avg Sat: " + restaurant.averageSatisfaction());
    }
    public void showResult(Outcome outcome, Restaurant restaurant) {
        System.out.println("\n=== GAME OVER: " + outcome + " ===");
    }

    private void printRestaurantStatus(Restaurant restaurant) {
        System.out.println("Queue:");
        for (Customer c : restaurant.getQueue()) {
            System.out.println("  - " + c.getClass().getSimpleName() + " #" + c.getId() + " (Sat: " + c.getSatisfaction() + ")");
        }
        System.out.println("Tables:");
        for (Table t : restaurant.getTables()) {
            if (t.isFree()) System.out.println("  - Table #" + t.getId() + ": [Free]");
            else {
                Customer c = t.getOccupant();
                System.out.println("  - Table #" + t.getId() + ": " + c.getClass().getSimpleName() + " #" + c.getId() + " (Sat: " + c.getSatisfaction() + ", State: " + c.getState() + ")");
            }
        }
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) { scanner.next(); }
        return scanner.nextInt();
    }
}
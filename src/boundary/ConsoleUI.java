package boundary;

import control.Restaurant;
import control.PricingPolicy;
import entity.*;
import control.Outcome;
import control.GameConfig;
import static entity.StaffAction.ActionType.*;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    public StaffAction promptWaiterAction(Restaurant restaurant) {
        System.out.println("\n--- Waiter Action ---");
        printRestaurantState(restaurant);
        System.out.println("1. SEAT | 2. TAKE_ORDER | 3. SERVE | 4. WAIT");
        System.out.print("Enter choice (1-4): ");
        int choice = getIntInput();

        switch (choice) {
            case 1:
                System.out.print("Customer ID to seat: ");
                int seatId = getIntInput();
                return new StaffAction(StaffAction.ActionType.SEAT, seatId);
            case 2:
                System.out.print("Customer ID to take order: ");
                int orderId = getIntInput();
                return new StaffAction(StaffAction.ActionType.TAKE_ORDER, orderId);
            case 3:
                System.out.print("Customer ID to serve: ");
                int serveId = getIntInput();
                return new StaffAction(StaffAction.ActionType.SERVE, serveId);
            case 4:
            default:
                return new StaffAction(StaffAction.ActionType.WAIT, -1);
        }
    }

    public StaffAction promptHostAction(Restaurant restaurant) {
        System.out.println("\n--- Host Action ---");
        printRestaurantState(restaurant);
        System.out.println("1. SEAT (Queue to Table) | 2. WAIT");
        System.out.print("Enter choice (1-2): ");
        int choice = getIntInput();

        if (choice == 1) {
            System.out.print("Customer ID from queue to seat: ");
            int seatId = getIntInput();
            return new StaffAction(StaffAction.ActionType.SEAT, seatId);
        }
        return new StaffAction(StaffAction.ActionType.WAIT, -1);
    }

    public StaffAction promptChefAction(Restaurant restaurant) {
        System.out.println("\n--- Chef Action ---");
        List<Order> preparing = restaurant.getPreparingOrders();
        System.out.println("Preparing orders count: " + preparing.size());
        System.out.println("1. ADVANCE preparation | 2. WAIT");
        System.out.print("Enter choice (1-2): ");
        int choice = getIntInput();

        if (choice == 1) {
            return new StaffAction(StaffAction.ActionType.ADVANCE, -1);
        }
        return new StaffAction(StaffAction.ActionType.WAIT, -1);
    }
    
    public boolean promptActivateHappyHour() {
        System.out.print("Do you want to activate Happy Hour for this turn? (y/n): ");
        String input = scanner.nextLine().trim();
        return input.equalsIgnoreCase("y");
    }

    public void printRestaurantState(Restaurant restaurant) {
        System.out.println("Queue:");
        if (restaurant.getQueue().isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            for (Customer c : restaurant.getQueue()) {
                System.out.println("  - " + c.getClass().getSimpleName() + " #" + c.getId() + " (Sat: " + c.getSatisfaction() + ")");
            }
        }

        System.out.println("Tables:");
        for (Table t : restaurant.getTables()) {
            if (t.isFree()) {
                System.out.println("- Table #" + t.getId() + ": [Free]");
            } else {
                Customer c = t.getOccupant();
                System.out.println("- Table #" + t.getId() + ": " + c.getClass().getSimpleName() + 
                                   " #" + c.getId() + " (Sat: " + c.getSatisfaction() + ", State: " + c.getState() + ")");
            }
        }
    }

    public void showTurnSummary(Restaurant restaurant, int turn) {
        // Check if Happy Hour is active via PricingPolicy
        boolean happyHourActive = PricingPolicy.isHappyHour(turn);
        String happyHourTag = happyHourActive ? " [HAPPY HOUR ACTIVE - 10% OFF]" : "";

        System.out.printf("Summary%s -> Revenue: $%.1f | Paid: %d | Avg Sat: %.2f\n",
                happyHourTag,
                restaurant.getRevenueCents() / 100.0,
                restaurant.getPaidCount(),
                restaurant.averageSatisfaction());
    }

    public void showError(String message) {
        System.out.println("[ERROR] " + message);
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return val;
    }
    
    // Display the start of a turn
    public void showTurnStart(Restaurant restaurant, int turn) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("TURN " + turn + " / " + GameConfig.MAX_TURNS); 
        System.out.println("--------------------------------------------------");
    }

    // Display the final outcome / game result
    public void showResult(Outcome outcome, Restaurant restaurant) {
        System.out.println("\n=== Final Stage Evaluation ===");
        System.out.println("Total Revenue: $" + (restaurant.getRevenueCents() / 100.0));
        System.out.println("Paid Customers: " + restaurant.getPaidCount());
        System.out.println("Average Satisfaction: " + restaurant.averageSatisfaction());
        System.out.println("=== GAME RESULT: " + outcome + " ===");
    }
}
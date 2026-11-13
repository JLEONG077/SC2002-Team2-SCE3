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
        String avg = restaurant.getPaidCount() > 0 ? String.format("%.1f", restaurant.averagePaidSatisfaction()) : "N/A";
        System.out.println("Revenue: $" + String.format("%.2f", restaurant.getRevenueCents() / 100.0)
                + " | Avg Satisfaction (paid): " + avg);
        if (restaurant.isHappyHourActive()) {
            System.out.println("[HAPPY HOUR ACTIVE - 20% OFF, +15 satisfaction] Turns remaining (incl. this one): "
                    + restaurant.getHappyHourTurnsRemaining());
        } else if (!restaurant.canActivateHappyHour()) {
            System.out.println("[Happy Hour already used]");
        }
        System.out.println("--------------------------------------------------");
    }

    public boolean promptHappyHourActivation(Restaurant restaurant) {
        System.out.println("\n--- Manager Decision ---");
        System.out.println("Happy Hour is available to activate (Can be used once per game, lasts for 2 turns, gives 20% off orders & +15 satisfaction recovery).");
        System.out.println("Do you want to activate Happy Hour this turn?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.print("Enter choice (1-2): ");
        int choice = getIntInput();
        return choice == 1;
    }

    public StaffAction promptHostAction(Restaurant restaurant) {
        System.out.println("\n--- Host Action (seats at most 1 queued customer) ---");
        printRestaurantStatus(restaurant);
        System.out.println("Choose Host Action:");
        System.out.println("1. SEAT customer (Enter customer ID)");
        System.out.println("2. WAIT (Pass turn)");
        System.out.print("Enter choice (1-2): ");
        int choice = getIntInput();
        if (choice == 1) {
            System.out.print("Enter Customer ID to seat: ");
            return new StaffAction(ActionType.SEAT, getIntInput());
        }
        if (choice == 2) {
            return new StaffAction(ActionType.WAIT, 0);
        }
        return null; // invalid -> controller reports error and re-prompts
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
                return new StaffAction(ActionType.WAIT, 0);
            default:
                return null; // invalid -> controller reports error and re-prompts
        }
    }

    public StaffAction promptChefAction(Restaurant restaurant) {
        System.out.println("\n--- Chef Action ---");
        printRestaurantStatus(restaurant);
        List<Order> preparing = restaurant.getPreparingOrders();
        if (preparing.isEmpty()) {
            System.out.println("No orders currently in preparation.");
        } else {
            System.out.println("Preparing Orders:");
            for (Order o : preparing) {
                Customer c = restaurant.findCustomerByOrder(o);
                String typeLabel = getCustomerTypeLabel(c);
                System.out.println("  - " + typeLabel + " #" + (c != null ? c.getId() : "?") + " (" + o.getItem().getName() + ") Progress: " + o.getUnitsDone() + "/" + o.getItem().getPrepUnits());
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
        } else if (choice == 2) {
            return new StaffAction(ActionType.WAIT, 0);
        }
        return null; // invalid -> controller reports error and re-prompts
    }

    public void showError(String reason) {
        System.out.println("[ERROR] " + reason);
    }

    public void showTurnSummary(Restaurant restaurant, int turn) {
        System.out.println("\n--- Turn " + turn + " Summary ---");
        float avgSat = restaurant.averagePaidSatisfaction();
        String avgSatStr = restaurant.getPaidCount() > 0 ? String.format("%.1f", avgSat) : "N/A";
        System.out.println("Served: " + restaurant.getServedCount()
                + " | Paid: " + restaurant.getPaidCount()
                + " | Unhappy departures: " + restaurant.getDepartedCount());
        System.out.println("Revenue: $" + String.format("%.2f", restaurant.getRevenueCents() / 100.0)
                + " | Avg Satisfaction (paid): " + avgSatStr
                + " | Turns used: " + turn + "/" + GameConfig.MAX_TURNS);
    }

    public void showResult(Outcome outcome, Restaurant restaurant) {
        System.out.println("\n==================================================");
        System.out.println("GAME OVER - RESULT: " + outcome);
        System.out.println("==================================================");
        System.out.println("Paid customers: " + restaurant.getPaidCount() + " (need 4)");
        System.out.println("Revenue: $" + String.format("%.2f", restaurant.getRevenueCents() / 100.0) + " (need $45.00)");
        System.out.println("Avg satisfaction (paid): " + (restaurant.getPaidCount() > 0 ? String.format("%.1f", restaurant.averagePaidSatisfaction()) : "N/A") + " (need 60)");
        if (outcome == Outcome.VICTORY) {
            System.out.println("Congratulations! You successfully ran the restaurant!");
        } else {
            System.out.println("Defeat! You didn't meet the target goals.");
        }
    }

    private void printRestaurantStatus(Restaurant restaurant) {
        System.out.println("Waiting Queue:");
        if (restaurant.getQueue().isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            for (Customer c : restaurant.getQueue()) {
                String typeLabel = getCustomerTypeLabel(c);
                System.out.println("  - " + typeLabel + " #" + c.getId() + " (Arrival: " + c.getArrivalTurn() + ", Sat: " + c.getSatisfaction() + ")");
            }
        }
        System.out.println("Tables:");
        for (Table t : restaurant.getTables()) {
            if (t.isFree()) {
                System.out.println("  - Table #" + t.getId() + ": [Free]");
            } else {
                Customer c = t.getOccupant();
                String typeLabel = getCustomerTypeLabel(c);
                String orderInfo = c.getOrder() != null ? (c.getOrder().getItem().getName() + " [" + c.getOrder().getStatus() + "]") : "No Order";
                System.out.println("  - Table #" + t.getId() + ": " + typeLabel + " #" + c.getId() + " (Sat: " + c.getSatisfaction() + ", State: " + c.getState() + ", Order: " + orderInfo + ")");
            }
        }
    }

    private String getCustomerTypeLabel(Customer c) {
        if (c instanceof VIPCustomer) {
            return "VIP Customer";
        } else if (c instanceof CriticCustomer) {
            return "Food Critic";
        } else {
            return "Regular Customer";
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

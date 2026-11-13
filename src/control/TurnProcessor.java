package control;

import boundary.ConsoleUI;
import entity.*;

public class TurnProcessor {
    private Restaurant restaurant;
    private ConsoleUI ui;
    private Cashier cashier = new Cashier();

    public TurnProcessor(Restaurant restaurant, ConsoleUI ui) {
        this.restaurant = restaurant;
        this.ui = ui;
    }

    public void runTurn(int turn) {
        // ---- Start of turn: pricing policy tick, Step 1 arrivals, Happy Hour decision ----
        restaurant.onTurnStart();
        processArrivals(turn);

        if (restaurant.canActivateHappyHour() && ui.promptHappyHourActivation(restaurant)) {
            if (restaurant.activateHappyHour()) {
                System.out.println("[Manager] Happy Hour activated! 20% off new orders for this turn and the next.");
            }
        }
        // Happy Hour recovery happens at turn start, for every active turn
        restaurant.applyHappyHourRecovery();

        ui.showTurnStart(restaurant, turn);

        // ---- Step 2: manager choices (Host, Waiter, Chef). Invalid choices re-prompt. ----
        Host host = restaurant.getHost();
        boolean hostDone = (host == null);
        while (!hostDone) {
            StaffAction action = ui.promptHostAction(restaurant);
            ActionResult result = host.perform(action, restaurant, turn);
            if (result.isSuccess()) {
                System.out.println("[Host] " + result.getMessage());
                hostDone = true;
            } else {
                ui.showError(result.getMessage());
            }
        }

        // Step 3: Waiter acts before Chef, so a dish finished this turn cannot be served this turn
        boolean waiterDone = false;
        while (!waiterDone) {
            StaffAction action = ui.promptWaiterAction(restaurant);
            ActionResult result = restaurant.getWaiter().perform(action, restaurant, turn);
            if (result.isSuccess()) {
                System.out.println("[Waiter] " + result.getMessage());
                waiterDone = true;
            } else {
                ui.showError(result.getMessage());
            }
        }

        boolean chefDone = false;
        while (!chefDone) {
            StaffAction action = ui.promptChefAction(restaurant);
            ActionResult result = restaurant.getChef().perform(action, restaurant, turn);
            if (result.isSuccess()) {
                System.out.println("[Chef] " + result.getMessage());
                chefDone = true;
            } else {
                ui.showError(result.getMessage());
            }
        }

        processEatingAndPayment(turn);
        applyWaitingLoss();

        ui.showTurnSummary(restaurant, turn);
    }

    private void processArrivals(int turn) {
        Customer c = ArrivalSchedule.customerFor(turn);
        if (c != null) {
            restaurant.enqueue(c);
            System.out.println(c.getClass().getSimpleName() + " #" + c.getId() + " arrived at turn " + turn + ".");
        }
    }

    /**
     * Step 4. The Cashier pays out a customer who was ALREADY ready to pay at the start of this step
     * (so someone who becomes READY_TO_PAY this turn pays next turn at the earliest). Then customers
     * served on an EARLIER turn finish their turn of eating and become READY_TO_PAY.
     */
    private void processEatingAndPayment(int turn) {
        if (restaurant.getOldestReadyToPayCustomer() != null) {
            ActionResult paymentResult = cashier.collectPayment(restaurant, turn);
            if (paymentResult.isSuccess()) {
                System.out.println("[Cashier] " + paymentResult.getMessage());
            }
        }

        for (Table t : restaurant.getTables()) {
            if (!t.isFree()) {
                Customer c = t.getOccupant();
                if (c.getState() == CustomerState.SERVED && c.getServedTurn() < turn) {
                    c.setState(CustomerState.READY_TO_PAY);
                }
            }
        }
    }

    /** Step 5. Only customers who have not been served lose satisfaction; at 0 they leave. */
    private void applyWaitingLoss() {
        for (Customer c : restaurant.getActiveCustomers()) {
            if (c.isAwaitingService()) {
                c.loseSatisfaction();
                if (c.getSatisfaction() <= 0) {
                    restaurant.customerLeaves(c);
                    System.out.println(c.getClass().getSimpleName() + " #" + c.getId() + " lost all satisfaction and left!");
                }
            }
        }
    }
}

package control;

import boundary.ConsoleUI;
import entity.*;

public class TurnProcessor {
    private Restaurant restaurant;
    private ConsoleUI ui;

    public TurnProcessor(Restaurant restaurant, ConsoleUI ui) {
        this.restaurant = restaurant;
        this.ui = ui;
    }

    public void runTurn(int turn) {
        restaurant.onTurnStart(); // Trigger turn start policy updates
        processArrivals(turn);

        // Optional Manager Happy Hour activation prompt at turn start
        if (restaurant.canActivateHappyHour()) {
            boolean wantHappyHour = ui.promptHappyHourActivation(restaurant);
            if (wantHappyHour) {
                if (restaurant.activateHappyHour()) {
                    System.out.println("[Manager] Happy Hour activated successfully! 20% off active for 2 turns.");
                }
            }
        }

        // Host action
        Host host = restaurant.getHost();
        if (host != null) {
            StaffAction hostAction = ui.promptHostAction(restaurant);
            if (hostAction != null && hostAction.getType() == ActionType.SEAT) {
                ActionResult result = host.perform(hostAction, restaurant, turn);
                if (result.isSuccess()) {
                    System.out.println("[Host] " + result.getMessage());
                } else {
                    ui.showError(result.getMessage());
                }
            }
        }

        boolean waiterDone = false;
        while (!waiterDone) {
            StaffAction action = ui.promptWaiterAction(restaurant);
            Waiter waiter = restaurant.getWaiter();
            ActionResult result = waiter.perform(action, restaurant, turn);
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
            Chef chef = restaurant.getChef();
            ActionResult result = chef.perform(action, restaurant, turn);
            if (result.isSuccess()) {
                System.out.println("[Chef] " + result.getMessage());
                chefDone = true;
            } else {
                ui.showError(result.getMessage());
            }
        }

        processEatingAndPayment(turn);
        applyWaitingLoss(turn);

        ui.showTurnSummary(restaurant, turn);
    }

    private void processArrivals(int turn) {
        Customer c = ArrivalSchedule.customerFor(turn);
        if (c != null) {
            restaurant.enqueue(c);
            System.out.println(c.getClass().getSimpleName() + " #" + c.getId() + " arrived at turn " + turn + ".");
        }
    }

    private void processEatingAndPayment(int turn) {
        for (Table t : restaurant.getTables()) {
            if (!t.isFree()) {
                Customer c = t.getOccupant();
                if (c.getState() == CustomerState.SERVED) {
                    c.setState(CustomerState.EATING);
                } else if (c.getState() == CustomerState.EATING) {
                    c.setState(CustomerState.READY_TO_PAY);
                }
            }
        }

        Cashier cashier = new Cashier();
        Customer readyToPay = restaurant.getOldestReadyToPayCustomer();
        if (readyToPay != null) {
            ActionResult paymentResult = cashier.collectPayment(restaurant, turn);
            if (paymentResult.isSuccess()) {
                System.out.println("[Cashier] " + paymentResult.getMessage());
            }
        }
    }

    private void applyWaitingLoss(int turn) {
        for (Customer c : restaurant.getActiveCustomers()) {
            if (!c.hasLeftOrPaid()) {
                // Happy Hour recovery rule: +15 satisfaction if waiting for table or food
                if (restaurant.isHappyHourActive() && (c.getState() == CustomerState.WAITING || c.getState() == CustomerState.SEATED || c.getState() == CustomerState.ORDERED || c.getState() == CustomerState.PREPARING)) {
                    c.addSatisfaction(15);
                } else {
                    c.loseSatisfaction();
                }

                if (c.getSatisfaction() <= 0) {
                    c.cancel();
                    for (Table t : restaurant.getTables()) {
                        if (t.getOccupant() == c) {
                            t.release();
                            break;
                        }
                    }
                    System.out.println(c.getClass().getSimpleName() + " #" + c.getId() + " lost all satisfaction and left!");
                }
            }
        }
    }
}

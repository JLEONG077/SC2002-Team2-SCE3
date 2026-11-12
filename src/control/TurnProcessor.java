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
        Customer scheduledCustomer = ArrivalSchedule.customerFor(turn);
        if (scheduledCustomer != null) {
            restaurant.enqueue(scheduledCustomer);
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

        processKitchenAndEatingStates(turn);

        applyWaitingLoss();

        ui.showTurnSummary(restaurant, turn);
    }

    private void processKitchenAndEatingStates(int turn) {

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
        if (readyToPay != null && readyToPay.getState() == CustomerState.READY_TO_PAY) {
            ActionResult paymentResult = cashier.collectPayment(restaurant, turn);
            if (paymentResult.isSuccess()) {
                System.out.println("[Cashier] " + paymentResult.getMessage());
            }
        }
    }

    private void applyWaitingLoss() {
        for (Customer c : restaurant.getActiveCustomers()) {
            if (!c.hasLeftOrPaid() && (c.getState() == CustomerState.WAITING || c.getState() == CustomerState.SEATED)) {
                c.loseSatisfaction();
                if (c.getSatisfaction() <= 0) {
                    c.cancel();
                    for (Table t : restaurant.getTables()) {
                        if (t.getOccupant() == c) {
                            t.release();
                            break;
                        }
                    }
                    System.out.println("Customer #" + c.getId() + " reached 0 satisfaction and left without paying.");
                }
            }
        }
    }
}
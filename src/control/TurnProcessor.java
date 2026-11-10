package control;

import boundary.ConsoleUI;
import entity.ActionResult;
import entity.Cashier;
import entity.Chef;
import entity.Customer;
import entity.CustomerState;
import entity.Host;
import entity.StaffAction;
import entity.Table;
import entity.Waiter;
import entity.*;

public class TurnProcessor {
    private Restaurant restaurant;
    private ConsoleUI ui;

    public TurnProcessor(Restaurant restaurant, ConsoleUI ui) {
        this.restaurant = restaurant;
        this.ui = ui;
    }
    
    public void runTurn(int turn) {
        processArrivals(turn);

        // 1. Manager Happy Hour activation prompt
        PricingPolicy.handleManagerPrompt(ui);

        // 2. Apply Happy Hour satisfaction bonus (+15) for waiting/seated/ordered customers
        restaurant.applyHappyHourSatisfactionBonus();

        // 3. Host Action
        boolean hostDone = false;
        while (!hostDone) {
            StaffAction action = ui.promptHostAction(restaurant);
            Host host = restaurant.getHost();
            if (host != null) {
                ActionResult result = host.perform(action, restaurant, turn);
                if (result.isSuccess()) {
                    System.out.println("[Host] " + result.getMessage());
                    hostDone = true;
                } else {
                    ui.showError(result.getMessage());
                }
            } else {
                hostDone = true;
            }
        }

        // 4. Waiter Action
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

        // 5. Chef Action
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

        // 6. Tick down Happy Hour duration at turn end
        PricingPolicy.tickTurn();

        processEatingAndPayment(turn);
        applyWaitingLoss(turn);
        ui.showTurnSummary(restaurant, turn);
    }

    private void processArrivals(int turn) {
        Customer c = ArrivalSchedule.customerFor(turn);
        if (c != null) {
            restaurant.enqueue(c);
            System.out.println("Customer #" + c.getId() + " arrived at turn " + turn + ".");
        }
    }

    private void processEatingAndPayment(int turn) {
        // 1. Transition served customers to READY_TO_PAY
        for (Table table : restaurant.getTables()) {
            if (!table.isFree()) {
                Customer customer = table.getOccupant();
                
                if (customer.getState() == CustomerState.SERVED) {
                    customer.setState(CustomerState.READY_TO_PAY);
                }
            }
        }

        // 2. Handle Cashier payment collection for customers ready to pay
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
                c.loseSatisfaction();
                if (c.getSatisfaction() <= 0) {
                    c.cancel();
                    System.out.println("Customer #" + c.getId() + " lost all satisfaction and left!");
                }
            }
        }
    }
}
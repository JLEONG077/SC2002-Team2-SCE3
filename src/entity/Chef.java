package entity;

import control.Restaurant;
import java.util.List;

public class Chef extends Staff {

    public Chef(String name) { super(name); }

    public ActionResult perform(StaffAction action, Restaurant restaurant, int turn) {
        if (action == null) return new ActionResult(false, "No action specified.");
        switch (action.getType()) {
            case ADVANCE: {
                List<Order> preparingOrders = restaurant.getPreparingOrders();
                if (preparingOrders.isEmpty()) return new ActionResult(false, "No orders currently in preparation.");
                Order targetOrder = preparingOrders.get(0);
                boolean becameReady = targetOrder.advance(turn);
                Customer c = restaurant.findCustomerByOrder(targetOrder);
                if (becameReady && c != null) {
                    c.setState(CustomerState.READY);
                    return new ActionResult(true, "Chef advanced order for " + c.getClass().getSimpleName() + " #" + c.getId() + " -> READY!");
                }
                return new ActionResult(true, "Chef advanced order (" + targetOrder.getUnitsDone() + "/" + targetOrder.getItem().getPrepUnits() + ").");
            }
            case WAIT:
                return new ActionResult(true, "Chef waits.");
            default:
                return new ActionResult(false, "Invalid action for Chef.");
        }
    }
}
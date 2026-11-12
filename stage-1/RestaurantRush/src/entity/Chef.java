package entity;

import java.util.List;

import control.Restaurant;

public class Chef extends Staff {

    public Chef(String name) {
        super(name);
    }

    public ActionResult perform(StaffAction action, Restaurant restaurant, int turn) {
        if (action == null) {
            return new ActionResult(false, "No action specified.");
        }
        switch (action.getType()) {
            case ADVANCE: {
                List<Order> preparingOrders = restaurant.getPreparingOrders();
                if (preparingOrders.isEmpty()) {
                    return new ActionResult(false, "No orders currently in preparation.");
                }
                Order targetOrder = null;
                if (action.getTargetId() > 0) {
                    for (Order o : preparingOrders) {
                        if (restaurant.findCustomerByOrder(o) != null && restaurant.findCustomerByOrder(o).getId() == action.getTargetId()) {
                            targetOrder = o;
                            break;
                        }
                    }
                }
                if (targetOrder == null && !preparingOrders.isEmpty()) {
                    targetOrder = preparingOrders.get(0);
                }
                boolean becameReady = targetOrder.advance(turn);
                if (becameReady) {
                    Customer c = restaurant.findCustomerByOrder(targetOrder);
                    if (c != null) {
                        c.setState(CustomerState.READY);
                    }
                    return new ActionResult(true, "Chef advanced order for Customer #" + (c != null ? c.getId() : "?") + " -> READY!");
                } else {
                    return new ActionResult(true, "Chef advanced order (" + targetOrder.getUnitsDone() + "/" + targetOrder.getItem().getPrepUnits() + " units done).");
                }
            }
            case WAIT: {
                return new ActionResult(true, "Chef waits.");
            }
            default:
                return new ActionResult(false, "Invalid action type for Chef.");
        }
    }
}

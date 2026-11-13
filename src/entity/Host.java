package entity;

import control.Restaurant;

public class Host extends Staff {
    public Host(String name) {
        super(name);
    }

    @Override
    public ActionResult perform(StaffAction action, Restaurant restaurant, int turn) {
        if (action == null) {
            return new ActionResult(false, "No action specified for Host.");
        }
        switch (action.getType()) {
            case SEAT: {
                Customer c = restaurant.findCustomerInQueue(action.getTargetId());
                if (c == null) {
                    return new ActionResult(false, "Customer ID " + action.getTargetId() + " not found in waiting queue.");
                }
                Table t = restaurant.findFreeTable();
                if (t == null) {
                    return new ActionResult(false, "No free tables available.");
                }
                return restaurant.seat(c, t);
            }
            case WAIT: {
                return new ActionResult(true, "Host waits.");
            }
            default:
                return new ActionResult(false, "Invalid action type for Host.");
        }
    }
}

package entity;

import control.Restaurant;
import java.util.List;

public class Waiter extends Staff {

    public Waiter(String name) { super(name); }

    public ActionResult perform(StaffAction action, Restaurant restaurant, int turn) {
        if (action == null) return new ActionResult(false, "No action specified.");
        switch (action.getType()) {
            case SEAT: {
                Customer c = restaurant.findCustomerInQueue(action.getTargetId());
                if (c == null) return new ActionResult(false, "Customer ID not found.");
                Table t = restaurant.findFreeTable();
                if (t == null) return new ActionResult(false, "No free tables.");
                return restaurant.seat(c, t);
            }
            case TAKE_ORDER: {
                Customer c = restaurant.findSeatedUnorderedCustomer(action.getTargetId());
                if (c == null) return new ActionResult(false, "Customer not seated or ordered.");
                List<MenuItem> menu = restaurant.getMenu();
                MenuItem chosen = c.chooseItem(menu);
                int price = c.discount(chosen.getPriceCents());
                c.setOrder(new Order(chosen, price));
                c.setState(CustomerState.ORDERED);
                return new ActionResult(true, "Took order for " + c.getClass().getSimpleName() + " #" + c.getId() + ": " + chosen.getName() + " ($" + (price/100.0) + ")");
            }
            case SERVE: {
                Customer c = restaurant.findCustomerWithReadyOrder(action.getTargetId());
                if (c == null) return new ActionResult(false, "No ready order for customer.");
                c.getOrder().markServed();
                return new ActionResult(true, "Served order.");
            }
            case WAIT:
                return new ActionResult(true, "Waiter waits.");
            default:
                return new ActionResult(false, "Invalid action.");
        }
    }
}
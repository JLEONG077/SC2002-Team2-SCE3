package entity;

import java.util.List;

import control.Restaurant;

public class Waiter extends Staff {

    public Waiter(String name) {
        super(name);
    }

    public ActionResult perform(StaffAction action, Restaurant restaurant, int turn) {
        if (action == null) {
            return new ActionResult(false, "No action specified.");
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
            case TAKE_ORDER: {
                Customer c = restaurant.findSeatedUnorderedCustomer(action.getTargetId());
                if (c == null) {
                    return new ActionResult(false, "Customer ID " + action.getTargetId() + " is not seated or already ordered.");
                }
                return takeOrder(c, restaurant, turn);
            }
            case SERVE: {
                Customer c = restaurant.findCustomerWithReadyOrder(action.getTargetId());
                if (c == null) {
                    return new ActionResult(false, "Customer ID " + action.getTargetId() + " has no ready order to serve.");
                }
                return serve(c.getOrder(), turn);
            }
            case WAIT: {
                return new ActionResult(true, "Waiter waits.");
            }
            default:
                return new ActionResult(false, "Invalid action type for Waiter.");
        }
    }

    public ActionResult takeOrder(Customer customer, Restaurant restaurant, int turn) {
        List<MenuItem> menu = restaurant.getMenu();
        MenuItem chosen = customer.chooseItem(menu);
        int price = customer.discount(chosen.getPriceCents());
        Order order = new Order(chosen, price);
        customer.setOrder(order);
        customer.setState(CustomerState.ORDERED);
        return new ActionResult(true, "Took order for Customer #" + customer.getId() + ": " + chosen.getName() + " ($" + (price/100.0) + ")");
    }

    public ActionResult serve(Order order, int turn) {
        if (order.getStatus() != OrderStatus.READY) {
            return new ActionResult(false, "Order is not ready yet.");
        }
        order.markServed();
        return new ActionResult(true, "Served order.");
    }
}
package entity;

import control.Restaurant;

public class Cashier {
    
    public ActionResult collectPayment(Restaurant restaurant, int turn) {
        Customer oldestReadyToPay = restaurant.getOldestReadyToPayCustomer();
        if (oldestReadyToPay == null) {
            return new ActionResult(false, "No customers ready to pay.");
        }
        Order order = oldestReadyToPay.getOrder();
        if (order == null) {
            return new ActionResult(false, "Customer has no order.");
        }
        order.markPaid();
        oldestReadyToPay.setState(CustomerState.PAID);
        restaurant.recordPayment(oldestReadyToPay);
        return new ActionResult(true, "Collected payment of $" + (order.getLockedPriceCents()/100.0) + " from Customer #" + oldestReadyToPay.getId());
    }
}
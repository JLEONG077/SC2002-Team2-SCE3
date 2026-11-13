package entity;

import java.util.List;

public class VIPCustomer extends Customer {

    public VIPCustomer(int arrivalTurn) {
        super(arrivalTurn);
    }

    @Override
    public int lossPerTurn() {
        return 5; 
    }

    @Override
    public int discount(int priceCents) {
        // 10% discount applied to the locked price (90% of price)
        return (priceCents * 90 + 50) / 100;
    }

    @Override
    public MenuItem chooseItem(List<MenuItem> menu) {
        if (menu == null || menu.isEmpty()) return null;
        MenuItem premium = menu.get(0);
        for (MenuItem m : menu) {
            if (m.getPriceCents() > premium.getPriceCents()) premium = m;
        }
        return premium;
    }
}

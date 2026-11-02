package entity;

import java.util.List;

public class VIPCustomer extends Customer {

    public VIPCustomer(int arrivalTurn) {
        super(arrivalTurn);
    }

    @Override
    public int lossPerTurn() {
        return 4; 
    }

    @Override
    public int discount(int priceCents) {
        return (int)(priceCents * 1.25); 
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
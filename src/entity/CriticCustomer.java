package entity;

import java.util.List;
import java.util.Random;

public class CriticCustomer extends Customer {
    private static final Random random = new Random(99);

    public CriticCustomer(int arrivalTurn) {
        super(arrivalTurn);
    }

    @Override
    public int lossPerTurn() {
        return 12;
    }

    @Override
    public int discount(int priceCents) {
        return priceCents; // Full price
    }

    @Override
    public MenuItem chooseItem(List<MenuItem> menu) {
        if (menu == null || menu.isEmpty()) return null;
        return menu.get(random.nextInt(menu.size()));
    }
}

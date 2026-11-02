package entity;

import java.util.List;
import java.util.Random;

public class RegularCustomer extends Customer {
	private static final Random random = new Random(42);

    public RegularCustomer(int arrivalTurn) {
        super(arrivalTurn);
    }

    @Override
    public int lossPerTurn() {
        return 8;
    }

    @Override
    public int discount(int priceCents) {
        return priceCents;
    }

    @Override
    public MenuItem chooseItem(List<MenuItem> menu) {
        if (menu == null || menu.isEmpty()) return null;
        return menu.get(random.nextInt(menu.size()));
    }
}

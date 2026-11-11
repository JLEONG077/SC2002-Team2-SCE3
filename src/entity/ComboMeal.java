package entity;

import java.util.List;

public class ComboMeal extends MenuItem {
    private List<MenuItem> bundledItems;

    public ComboMeal(String name, List<MenuItem> bundledItems) {
        super(name, calculateComboPrice(bundledItems), calculateTotalPrep(bundledItems));
        this.bundledItems = bundledItems;
    }
    private static int calculateComboPrice(List<MenuItem> items) {
        int combinedPriceCents = 0;
        for (MenuItem item : items) {
            combinedPriceCents += item.getPriceCents();
        }
        return (int) Math.round(combinedPriceCents * 0.90);
    }

    private static int calculateTotalPrep(List<MenuItem> items) {
        int totalPrep = 0;
        for (MenuItem item : items) {
            totalPrep += item.getPrepUnits();
        }
        return totalPrep;
    }

    public List<MenuItem> getBundledItems() {
        return bundledItems;
    }
}

package entity;

import control.Restaurant;

public class HappyHourPricing implements PricingPolicy {
    private int turnsLeft;

    public HappyHourPricing(int turnsLeft) {
        this.turnsLeft = turnsLeft;
    }

    @Override
    public int finalPrice(int cents) {
        // 20% off during happy hour
        return (cents * 80 + 50) / 100;
    }

    @Override
    public void onTurnStart(Restaurant restaurant) {
        if (turnsLeft > 0) {
            turnsLeft--;
        }
    }

    @Override
    public int remainingTurns() {
        return turnsLeft;
    }
}

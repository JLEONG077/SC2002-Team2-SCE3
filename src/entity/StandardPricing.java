package entity;

import control.Restaurant;

public class StandardPricing implements PricingPolicy {
    @Override
    public int finalPrice(int cents) {
        return cents;
    }

    @Override
    public void onTurnStart(Restaurant restaurant) {
        // Standard pricing does nothing on turn start
    }

    @Override
    public int remainingTurns() {
        return 0;
    }
}

package entity;

import control.Restaurant;

public interface PricingPolicy {
    int finalPrice(int cents);
    void onTurnStart(Restaurant restaurant);
    int remainingTurns();
}

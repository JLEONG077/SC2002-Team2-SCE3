package control;

public class ResultEvaluator {
    public static Outcome evaluate(Restaurant restaurant) {
        int paidCount = restaurant.getPaidCount();
        int revenueCents = restaurant.getRevenueCents();
        float avgSatisfaction = restaurant.averagePaidSatisfaction(); // Make sure this averages only paid customers

        if (paidCount >= 4 && revenueCents >= 4500 && avgSatisfaction >= 60.0f) {
            return Outcome.VICTORY;
        }
        return Outcome.DEFEAT;
    }
}
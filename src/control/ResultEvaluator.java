package control;

public class ResultEvaluator {

    public Outcome evaluate(Restaurant restaurant) {
        int paidCount = restaurant.getPaidCount();
        int revenueCents = restaurant.getRevenueCents();
        float avgSat = restaurant.averageSatisfaction();

        if (paidCount >= GameConfig.MIN_PAID && 
            revenueCents >= GameConfig.MIN_REVENUE_CENTS && 
            avgSat >= GameConfig.MIN_AVG_SATISFACTION) {
            return Outcome.VICTORY;
        } else {
            return Outcome.DEFEAT;
        }
    }
}

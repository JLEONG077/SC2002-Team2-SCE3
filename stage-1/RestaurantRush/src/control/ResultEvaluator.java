package control;

public class ResultEvaluator {

    public Outcome evaluate(Restaurant restaurant) {
        int paidCount = restaurant.getPaidCount();
        int revenueCents = restaurant.getRevenueCents();
        float avgSat = restaurant.averageSatisfaction();

        System.out.println("\n=== Final Evaluation ===");
        System.out.println("Paid Customers: " + paidCount + " (Required >= " + GameConfig.MIN_PAID + ")");
        System.out.println("Total Revenue: $" + (revenueCents / 100.0) + " (Required >= $" + (GameConfig.MIN_REVENUE_CENTS / 100.0) + ")");
        System.out.println("Average Satisfaction: " + avgSat + " (Required >= " + GameConfig.MIN_AVG_SATISFACTION + ")");

        if (paidCount >= GameConfig.MIN_PAID && 
            revenueCents >= GameConfig.MIN_REVENUE_CENTS && 
            avgSat >= GameConfig.MIN_AVG_SATISFACTION) {
            return Outcome.VICTORY;
        } else {
            return Outcome.DEFEAT;
        }
    }
}

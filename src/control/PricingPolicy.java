package control;

import boundary.ConsoleUI;

public class PricingPolicy {
    private static boolean activatedOnce = false;
    private static int remainingDuration = 0; 
    public static boolean canActivate() {
        return !activatedOnce;
    }

    public static void activate() {
        if (!activatedOnce) {
            activatedOnce = true;
            remainingDuration = 2; 
        }
    }

    public static boolean isHappyHourActive() {
        return remainingDuration > 0;
    }

    public static boolean isHappyHour(int turn) {
        return isHappyHourActive();
    }

    public static int getRemainingDuration() {
        return remainingDuration;
    }

    public static void tickTurn() {
        if (remainingDuration > 0) {
            remainingDuration--;
        }
    }

    public static int applyHappyHourDiscount(int priceCents) {
        if (isHappyHourActive()) {
            return (int) (priceCents * 0.80);
        }
        return priceCents;
    }

    public static void handleManagerPrompt(ConsoleUI ui) {
        if (canActivate()) {
            if (ui.promptActivateHappyHour()) {
                activate();
                System.out.println("[Manager] Happy Hour activated! Remaining duration: " + getRemainingDuration() + " turns.");
            }
        } else if (isHappyHourActive()) {
            System.out.println("[Manager] Happy Hour is active. Remaining duration: " + getRemainingDuration() + " turns.");
        }
    }
}

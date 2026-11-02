package control;

import boundary.ConsoleUI;

public class GameController {
    private int currentTurn;
    private Restaurant restaurant;
    private GameConfig config;
    private ConsoleUI ui;
    private ResultEvaluator evaluator;

    public GameController() {
        this.currentTurn = 1;
        this.restaurant = new Restaurant();
        this.config = new GameConfig();
        this.ui = new ConsoleUI();
        this.evaluator = new ResultEvaluator();
    }

    public void run() {
        System.out.println("=== Welcome to Restaurant Rush: Stage 1 ===");
        TurnProcessor processor = new TurnProcessor(restaurant, ui);

        while (currentTurn <= GameConfig.MAX_TURNS) {
            ui.showTurnStart(restaurant, currentTurn);
            processor.runTurn(currentTurn);
            currentTurn++;
        }

        Outcome outcome = evaluator.evaluate(restaurant);
        ui.showResult(outcome, restaurant);
    }
}
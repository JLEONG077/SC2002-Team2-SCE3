package entity;

public class StaffAction {
	private ActionType type;
    private int targetId;

    public StaffAction(ActionType type, int targetId) {
        this.type = type;
        this.targetId = targetId;
    }

    public ActionType getType() {
        return type;
    }

    public int getTargetId() {
        return targetId;
    }
}

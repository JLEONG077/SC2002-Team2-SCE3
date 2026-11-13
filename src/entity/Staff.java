package entity;

public abstract class Staff {
	protected String name;

    public Staff(String name) {
        this.name = name;
    }

    public abstract ActionResult perform(StaffAction action, control.Restaurant restaurant, int turn);

    public String getName() {
        return name;
    }
}

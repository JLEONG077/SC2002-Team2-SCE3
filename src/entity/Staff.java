package entity;

public abstract class Staff {
	protected String name;

    public Staff(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

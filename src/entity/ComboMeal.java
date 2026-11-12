package entity;

public class ComboMeal extends MenuItem {
    private MenuItem item1;
    private MenuItem item2;

    public ComboMeal(MenuItem item1, MenuItem item2) {
        super(item1.getName() + " & " + item2.getName(), 
              (int) Math.round((item1.getPriceCents() + item2.getPriceCents()) * 0.90), 
              item1.getPrepUnits() + item2.getPrepUnits());
        this.item1 = item1;
        this.item2 = item2;
    }

    public MenuItem getItem1() { return item1; }
    public MenuItem getItem2() { return item2; }
}

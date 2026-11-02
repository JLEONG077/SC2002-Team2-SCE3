package control;

import entity.Customer;
import entity.RegularCustomer;

public class ArrivalSchedule {
    public static Customer customerFor(int turn) {
        if (turn == 1 || turn == 4 || turn == 7 || turn == 10 || turn == 13 || turn == 16) {
            return new RegularCustomer(turn);
        }
        return null;
    }
}
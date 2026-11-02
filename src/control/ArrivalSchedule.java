package control;

import entity.Customer;
import entity.RegularCustomer;
import entity.VIPCustomer;
import entity.CriticCustomer;

public class ArrivalSchedule {
    public static Customer customerFor(int turn) {
        if (turn == 1 || turn == 7 || turn == 13) {
            return new RegularCustomer(turn);
        } else if (turn == 4 || turn == 10) {
            return new VIPCustomer(turn);
        } else if (turn == 16) {
            return new CriticCustomer(turn);
        }
        return null;
    }
}
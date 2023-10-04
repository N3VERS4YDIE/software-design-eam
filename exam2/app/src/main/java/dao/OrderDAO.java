package dao;

import model.Order;

public class OrderDAO extends DAO<Order> {

    private static OrderDAO instance;

    private OrderDAO() {
        super("orders");
    }

    public static OrderDAO getInstance() {
        if (instance == null) {
            instance = new OrderDAO();
        }
        return instance;
    }
}

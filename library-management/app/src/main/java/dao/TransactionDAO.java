package dao;

import model.Transaction;

public class TransactionDAO extends DAO<Transaction> {
    private static TransactionDAO instance;

    private TransactionDAO() {
        super("transactions");
    }

    public static TransactionDAO getInstance() {
        if (instance == null) {
            instance = new TransactionDAO();
        }
        return instance;
    }
}

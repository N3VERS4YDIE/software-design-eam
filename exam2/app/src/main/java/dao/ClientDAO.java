package dao;

import model.Client;

public class ClientDAO extends DAO<Client> {

    private static ClientDAO instance;

    private ClientDAO() {
        super("clients");
    }

    public static ClientDAO getInstance() {
        if (instance == null) {
            instance = new ClientDAO();
        }
        return instance;
    }
}

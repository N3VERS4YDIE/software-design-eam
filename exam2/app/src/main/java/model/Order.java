package model;

import com.n3vers4ydie.autosqlcrud.SQLModel;
import java.time.LocalDate;

public class Order extends SQLModel {

    private LocalDate date;
    private double total;
    private String clientId;

    public Order(String id, LocalDate date, double total, String clientId) {
        super(id);
        setDate(date);
        setTotal(total);
        setClientId(clientId);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
        values.put("date", date);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
        values.put("total", total);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
        values.put("client_id", clientId);
    }
}

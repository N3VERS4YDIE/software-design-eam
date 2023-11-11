package model;

import com.n3vers4ydie.autosqlcrud.SQLModel;
import java.time.LocalDateTime;

public class Transaction extends SQLModel {

    private TransactionType type;
    private LocalDateTime dateTime;
    private String userId;
    private String tableName;
    private String otherId;

    public Transaction(TransactionType type, String userId, String tableName, String otherId) {
        super("0");
        setType(type);
        setDateTime(LocalDateTime.now());
        setUserId(userId);
        setTableName(tableName);
        setOtherId(otherId);
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
        values.put("type", type.toString());
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime date) {
        this.dateTime = date;
        values.put("datetime", date.toString());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        values.put("user_id", userId);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        values.put("tablename", tableName);
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
        values.put("other_id", otherId);
    }
}

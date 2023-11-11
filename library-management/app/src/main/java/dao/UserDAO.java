package dao;

import java.sql.SQLException;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import util.FieldChecker;
import util.PasswordHasher;

public class UserDAO extends DAO<User> {

    public UserDAO() {
        super("users");
    }

    @Override
    public boolean add(User user) throws SQLException {
        FieldChecker.checkEmail(user.getId());
        FieldChecker.checkName(user.getFirstname());
        FieldChecker.checkName(user.getLastname());

        final String SALT = BCrypt.gensalt();
        final String HASHED_PASSWORD = PasswordHasher.hashPassword(user.getPassword(), SALT);
        user.setPassword(HASHED_PASSWORD);
        user.setSalt(SALT);

        return super.add(user);
    }

    @Override
    public boolean updateRS(User user) throws SQLException {
        FieldChecker.checkName(user.getFirstname());
        FieldChecker.checkName(user.getLastname());

        final String SALT = user.getSalt();
        final String HASHED_PASSWORD = PasswordHasher.hashPassword(user.getPassword(), SALT);
        user.setPassword(HASHED_PASSWORD);

        return super.updateRS(user);
    }
}

package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO () throws DataAccessException {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void clear() {
        var userStatement = "TRUNCATE userData";
        ExecuteUpdate.executeUpdate(userStatement);
    }

    @Override
    public void addUser(UserData user) {
        var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        ExecuteUpdate.executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM pet WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
    } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }
}

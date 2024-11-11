package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {



    @Override
    public void clear() {
        var userStatement = "TRUNCATE users";
        ExecuteUpdate.executeUpdate(userStatement);
    }

    @Override
    public void addUser(UserData user) {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        ExecuteUpdate.executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM users WHERE username=?";
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
        System.out.println("In readUser");
        String username = rs.getString("username");
        System.out.println("Got username");
        String email = rs.getString("email");
        System.out.println("Set all");
        String password = rs.getString("password");
        System.out.println("Got password");
        return new UserData(username, password, email);
    }
}

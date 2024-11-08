package dataaccess;

import model.UserData;

public class MySQLUserDAO implements UserDAO {
    @Override
    public void clear() {
        var userStatement = "TRUNCATE userData";
        var gameStatement = "TRUNCATE gameData";
        var authStatement = "TRUNCATE authData";
        executeUpdate(userStatement);
        executeUpdate(gameStatement);
        executeUpdate(authStatement);
    }

    @Override
    public void addUser(UserData user) {
        var statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        var json = new Gson().toJson(user);
        var id = executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM pet WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readPet(rs);
                    }
                }
            }
    }
}

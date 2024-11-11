package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {



    @Override
    public void clear() {
        var statement = "TRUNCATE auth";
        ExecuteUpdate.executeUpdate(statement);
    }

    @Override
    public String createAuth(String authToken, AuthData authData) {
        var statement = "INSERT INTO auth (name, type, json) VALUES (?, ?, ?)";
        ExecuteUpdate.executeUpdate(statement, authToken, authData.username());
        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuthData(rs);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public AuthData readAuthData(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String authToken = rs.getString("authToken");
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM auth WHERE authToken=?";
        ExecuteUpdate.executeUpdate(statement, authToken);
    }
}

package dataaccess;

import model.AuthData;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void clear() {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public String createAuth(String authToken, AuthData authData) {
        var statement = "INSERT INTO auth (name, type, json) VALUES (?, ?, ?)";
        var json = new Gson().toJson(authData);
        var id = executeUpdate(statement, authToken, authData.username(), json);
        return authToken;
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readPet(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }
}

package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    @Override
    public void clear() {
        var statement = "TRUNCATE pet";
        executeUpdate(statement);
    }

    @Override
    public int createGame(GameData gameData) {
        var statement = "INSERT INTO games (gameID, game, json) VALUES (?, ?, ?)";
        var json = new Gson().toJson(gameData);
        var id = executeUpdate(statement, gameData.gameID(), gameData, json);
        return gameData.gameID();
    }

    @Override
    public GameData getGame(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, json FROM games WHERE id=?";
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
    public void updateGame(int gameID, GameData gameData) {

    }

    @Override
    public Collection<GameData> listGames() {
        var result = new Collection<GameData>() {
        };
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readPet(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }
}

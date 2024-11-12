package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.gson.Gson;

public class MySQLGameDAO implements GameDAO {



    @Override
    public void clear() {
        var statement = "TRUNCATE games";
        ExecuteUpdate.executeUpdate(statement);
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        String game = new Gson().toJson(gameData.game());
        return ExecuteUpdate.executeUpdate(statement, gameData.gameName(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("id");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        var jsonGame = rs.getString("game");
        ChessGame game = new Gson().fromJson(jsonGame, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?," +
                " gameName = ? Where id = ?";
        ExecuteUpdate.executeUpdate(statement, gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), gameData.gameID());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games;
        games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return games;
    }
}

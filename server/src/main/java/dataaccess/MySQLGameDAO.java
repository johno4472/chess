package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    @Override
    public void clear() {

    }

    @Override
    public int createGame(GameData gameData) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }
}

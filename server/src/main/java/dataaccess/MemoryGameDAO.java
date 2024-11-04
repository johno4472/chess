package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    final private HashMap<java.lang.Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int createGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
        return gameData.gameID();
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(int gameID) {
        //update string gameName anytime the game players or moves are changed
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }
}

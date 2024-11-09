package dataaccess;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public void clear();

    public int createGame(GameData gameData);

    public GameData getGame(int gameID) throws DataAccessException;

    public void updateGame(int gameID, GameData gameData);

    public Collection<GameData> listGames() throws DataAccessException;
}

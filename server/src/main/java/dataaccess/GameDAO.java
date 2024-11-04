package dataaccess;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public void clear();

    public int createGame(GameData gameData);

    public GameData getGame(int gameID);

    public void updateGame(int gameID);

    public Collection<GameData> listGames();
}

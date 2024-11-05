package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public class ChessService {

    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;
    private final MemoryUserDAO userDAO;

    public ChessService(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO, MemoryUserDAO userDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public void clear() {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

    public void addUser(UserData user){
        userDAO.addUser(user);
    }

    public UserData getUser(String username){
        return userDAO.getUser(username);
    }

    public int createGame(GameData gameData){
        gameDAO.createGame(gameData);
        return gameData.gameID();
    }

    public GameData getGame(int gameID){
        return gameDAO.getGame(gameID);
    }

    public void updateGame(int gameID){
        gameDAO.updateGame(gameID);
    }

    public Collection<GameData> listGames(){
        return gameDAO.listGames();
    }

    public AuthData createAuth(){
        return authDAO.createAuth();
    }

    public AuthData getAuth(String authToken){
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth(AuthData authData){
        authDAO.deleteAuth(authData);
    }

}

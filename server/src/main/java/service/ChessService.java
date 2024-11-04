package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public class ChessService {

    public void clear() {

    }

    public void addUser(UserData user){

    }

    public UserData getUser(String username){
        return null;
    }

    public int createGame(GameData gameData){
        return 0;
    }

    public GameData getGame(int gameID){
        return null;
    }

    public void updateGame(int gameID){

    }

    public Collection<GameData> listGames(){
        return null;
    }

    public AuthData createAuth(){
        return null;
    }

    public AuthData getAuth(String authToken){
        return null;
    }

    public void deleteAuth(AuthData authData){

    }

}

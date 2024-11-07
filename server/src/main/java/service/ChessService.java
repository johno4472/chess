package service;

import chess.ChessGame;
import dataaccess.*;

import model.AuthData;
import model.GameData;
import model.SimpleGameData;
import model.UserData;
import service.requestresult.*;

import java.util.*;

public class ChessService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public ChessService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public String generateAuthToken(){
        return UUID.randomUUID().toString();
    }

    public int generateGameID() {
        Random rand = new Random();
        return rand.nextInt(1000);
    }

    public void clear() {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

    public LoginResult login(LoginRequest loginRequest) {
        UserData user = userDAO.getUser(loginRequest.username());
        if (user != null){
            if (Objects.equals(user.password(), loginRequest.password())){
                String authToken = generateAuthToken();
                authDAO.createAuth(authToken, new AuthData(authToken, user.username()));
                return new LoginResult(user.username(), authToken, null);
            }
            else{
                return new LoginResult(null, null, "Error: unauthorized");
            }
        }
        else {
            return new LoginResult(null, null, "Error: No user found with this name");
        }
    }

    public RegisterResult addUser(UserData userData){
        if (userDAO.getUser(userData.username()) != null){
            return new RegisterResult(null, null, "Error: already taken");
        }
        else{
            userDAO.addUser(userData);
            String authToken = generateAuthToken();
            authDAO.createAuth(authToken, new AuthData(authToken, userData.username()));
            return new RegisterResult(userData.username(), authToken, null);
        }
    }

    public LogoutResponse logout(String authToken) {
        if (authDAO.getAuth(authToken) != null){
            authDAO.deleteAuth(authToken);
            return new LogoutResponse(null);
        }
        else{
            return new LogoutResponse("Error: unauthorized");
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest){
        // gameDAO.createGame(gameID);
        AuthData authData = authDAO.getAuth(createGameRequest.authToken());
        if (authData != null){
            int randID = generateGameID();
            String username = authData.username();
            GameData gameData;
            gameData = new GameData(randID, null, null, createGameRequest.gameName(), new ChessGame());
            gameDAO.createGame(gameData);
            return new CreateGameResult(randID, null);
        }
        else{
            return new CreateGameResult(null, "Error: unauthorized");
        }
    }

    public ListGamesResult listGames(String authToken){
        AuthData authData = authDAO.getAuth(authToken);
        if (authData != null){
            Collection<GameData> gamesList;
            gamesList = gameDAO.listGames();
            Collection<SimpleGameData> simpleGamesList = new ArrayList<>();
            for (GameData gameData: gamesList){
                int gameID = gameData.gameID();
                String whiteUsername = gameData.whiteUsername();
                String blackUsername = gameData.blackUsername();
                String gameName = gameData.gameName();
                simpleGamesList.add(new SimpleGameData(gameID, whiteUsername, blackUsername, gameName));
            }

            return new ListGamesResult(simpleGamesList, null);
        }
        else{
            return new ListGamesResult(null, "Error: unauthorized");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        AuthData authData = authDAO.getAuth(joinGameRequest.authToken());
        int gameID = joinGameRequest.gameID();
        if (authData != null) {
            if (gameDAO.getGame(gameID) != null){
                GameData gameData;
                gameData = gameDAO.getGame(gameID);
                ChessGame.TeamColor playerColor;
                playerColor = joinGameRequest.playerColor();

                if (gameData.blackUsername() == null && playerColor == ChessGame.TeamColor.BLACK){
                    GameData gameUpdate;
                    gameUpdate = new GameData(gameID, gameData.whiteUsername(), authData.username(),
                            gameData.gameName(), gameData.game());
                    gameDAO.updateGame(gameID, gameUpdate);
                    return new JoinGameResult(null);
                }
                else if(gameData.whiteUsername() == null && playerColor == ChessGame.TeamColor.WHITE) {
                    GameData gameUpdate;
                    gameUpdate = new GameData(gameID, authData.username(), gameData.blackUsername(),
                            gameData.gameName(), gameData.game());
                    gameDAO.updateGame(gameID, gameUpdate);
                    return new JoinGameResult(null);
                }
                else {
                    return new JoinGameResult("Error: already taken");
                }
            }
            else {
                return new JoinGameResult("Error: bad request");
            }
        }
        else {
            return new JoinGameResult("Error: unauthorized");
        }
    }

}

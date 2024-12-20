package service;

import chess.ChessGame;
import dataaccess.*;

import model.AuthData;
import model.GameData;
import model.SimpleGameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import model.requestresult.*;

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

    public LoginResponse login(LoginRequest loginRequest) {
        UserData user = userDAO.getUser(loginRequest.username());
        if (user != null){
            if (BCrypt.checkpw(loginRequest.password(), user.password())){
                String authToken = generateAuthToken();
                authDAO.createAuth(authToken, new AuthData(authToken, user.username()));
                return new LoginResponse(user.username(), authToken, null);
            }
            else{
                return new LoginResponse(null, null, "Error: unauthorized");
            }
        }
        else {
            return new LoginResponse(null, null, "Error: No user found with this name");
        }
    }

    public RegisterResponse addUser(UserData userData){
        if (userDAO.getUser(userData.username()) != null){
            System.out.println("Username taken");
            return new RegisterResponse(null, null, "Error: already taken");
        }
        else{
            String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
            userDAO.addUser(new UserData(userData.username(), hashedPassword, userData.email()));
            String authToken = generateAuthToken();
            authDAO.createAuth(authToken, new AuthData(authToken, userData.username()));
            return new RegisterResponse(userData.username(), authToken, null);
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

    public CreateGameResponse createGame(CreateGameRequest createGameRequest) {
        AuthData authData = authDAO.getAuth(createGameRequest.authToken());
        if (authData != null){
            int gameID = generateGameID();
            GameData gameData;
            gameData = new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame());
            try {
                gameID = gameDAO.createGame(gameData);
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
            return new CreateGameResponse(gameID, null);
        }
        else{
            return new CreateGameResponse(null, "Error: unauthorized");
        }
    }

    public ListGamesResponse listGames(String authToken) {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData != null){
            Collection<GameData> gamesList;
            try {
                gamesList = gameDAO.listGames();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
            Collection<SimpleGameData> simpleGamesList = new ArrayList<>();
            for (GameData gameData: gamesList){
                int gameID = gameData.gameID();
                String whiteUsername = gameData.whiteUsername();
                String blackUsername = gameData.blackUsername();
                String gameName = gameData.gameName();
                simpleGamesList.add(new SimpleGameData(gameID, whiteUsername, blackUsername, gameName));
            }

            return new ListGamesResponse(simpleGamesList, null);
        }
        else{
            return new ListGamesResponse(null, "Error: unauthorized");
        }
    }

    public JoinGameResponse joinGame(JoinGameRequest joinGameRequest) {
        AuthData authData = authDAO.getAuth(joinGameRequest.authToken());
        int gameID = joinGameRequest.gameID();
        if (authData != null) {
            try {
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
                        return new JoinGameResponse(null);
                    }
                    else if(gameData.whiteUsername() == null && playerColor == ChessGame.TeamColor.WHITE) {
                        GameData gameUpdate;
                        gameUpdate = new GameData(gameID, authData.username(), gameData.blackUsername(),
                                gameData.gameName(), gameData.game());
                        gameDAO.updateGame(gameID, gameUpdate);
                        return new JoinGameResponse(null);
                    }
                    else {
                        return new JoinGameResponse("Error: already taken");
                    }
                }
                else {
                    return new JoinGameResponse("Error: bad request");
                }
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            return new JoinGameResponse("Error: unauthorized");
        }
    }

}

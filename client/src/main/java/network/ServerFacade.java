package network;

import com.google.gson.Gson;
import model.UserData;
import model.requestresult.*;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerFacade {
    private HTTPCommunicator httpCommunicator = new HTTPCommunicator();



    public RegisterResponse register(UserData userData) {
        InputStreamReader json = httpCommunicator.post(null, new Gson().toJson(userData), "/user");
        return new Gson().fromJson(json, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) {
        InputStreamReader json = httpCommunicator.post(null, new Gson().toJson(request), "/session");
        return new Gson().fromJson(json, LoginResponse.class);

    }

    public CreateGameResponse createGame(CreateGameRequest request){
        InputStreamReader json = httpCommunicator.post(request.authToken(), new Gson().toJson(request), "/game");
        return new Gson().fromJson(json, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(ListGamesRequest request) {
        InputStreamReader json = httpCommunicator.get(request.authToken(), "/game");
        return new Gson().fromJson(json, ListGamesResponse.class);
    }

    public JoinGameResponse joinGame(JoinGameRequest request) {
        InputStreamReader json = httpCommunicator.put(request.authToken(), new Gson().toJson(request), "/game");
        return new Gson().fromJson(json, JoinGameResponse.class);
    }

    public void observeGame(int gameID) {
    }

    public LogoutResponse logout(String authToken) {
        InputStreamReader json = httpCommunicator.delete(authToken, "/session");
        return new Gson().fromJson(json, LogoutResponse.class);
    }
}


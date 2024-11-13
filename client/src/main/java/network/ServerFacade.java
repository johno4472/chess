package network;

import com.google.gson.Gson;
import model.UserData;
import model.requestresult.*;

public class ServerFacade {
    private HTTPCommunicator httpCommunicator = new HTTPCommunicator();



    public RegisterResponse register(UserData userData) {
        String json = httpCommunicator.post(null, new Gson().toJson(userData), "/user");
        return new Gson().fromJson(json, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) {
        String json = httpCommunicator.post(null, new Gson().toJson(request), "/session");
        return new Gson().fromJson(json, LoginResponse.class);

    }

    public CreateGameResponse createGame(CreateGameRequest request){
        String json = httpCommunicator.post(request.authToken(), new Gson().toJson(request), "/game");
        return new Gson().fromJson(json, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(ListGamesRequest request) {
        String json = httpCommunicator.get(request.authToken(), "/game");
        return new Gson().fromJson(json, ListGamesResponse.class);
    }

    public JoinGameResponse joinGame(JoinGameRequest request) {
        String json = httpCommunicator.put(request.authToken(), new Gson().toJson(request), "/game");
        return new Gson().fromJson(json, JoinGameResponse.class);
    }

    public void observeGame(int gameID) {
    }

    public LogoutResponse logout(String authToken) {
        String json = httpCommunicator.delete(authToken, "/session");
        return new Gson().fromJson(json, LogoutResponse.class);
    }
}


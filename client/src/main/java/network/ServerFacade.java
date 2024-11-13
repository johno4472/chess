package network;

import com.google.gson.Gson;
import model.UserData;
import model.requestresult.*;

public class ServerFacade {

    public RegisterResponse register(UserData userData) {
        String json = HTTPCommunicator.post(null, new Gson().toJson(userData), "/user");
        return new Gson().fromJson(json, RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) {
        String json = HTTPCommunicator.post(null, new Gson().toJson(request), "/session");
        return new Gson().fromJson(json, LoginResponse.class);

    }

    public CreateGameResponse createGame(CreateGameRequest request){
        String json = HTTPCommunicator.post(request.authToken(), new Gson().toJson(request), "/game");
        return new Gson().fromJson(json, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(ListGamesRequest request) {
        String json = HTTPCommunicator.get(request.authToken(), "/game");
        return new Gson().fromJson(json, ListGamesResponse.class);
    }

    public JoinGameResponse joinGame(JoinGameRequest request) {
        String json = HTTPCommunicator.put(request.authToken(), new Gson().toJson(request), "/game");
        return new Gson().fromJson(json, JoinGameResponse.class);
    }

    public void observeGame(int gameID) {
    }

    public LogoutResponse logout(String authToken) {
        String json = HTTPCommunicator.delete(authToken, "/session");
        return new Gson().fromJson(json, LogoutResponse.class);
    }
}


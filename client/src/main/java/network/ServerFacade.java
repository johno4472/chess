package network;

import com.google.gson.Gson;
import model.UserData;
import model.requestresult.*;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerFacade {
    private HTTPCommunicator httpCommunicator = new HTTPCommunicator();



    public RegisterResponse register(UserData userData) {
        InputStream json = httpCommunicator.post(null, new Gson().toJson(userData), "/user");
        return new Gson().fromJson(new InputStreamReader(json), RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) {
        InputStream json = httpCommunicator.post(null, new Gson().toJson(request), "/session");
        return new Gson().fromJson(new InputStreamReader(json), LoginResponse.class);

    }

    public CreateGameResponse createGame(CreateGameRequest request){
        InputStream json = httpCommunicator.post(request.authToken(), new Gson().toJson(request), "/game");
        return new Gson().fromJson(new InputStreamReader(json), CreateGameResponse.class);
    }

    public ListGamesResponse listGames(ListGamesRequest request) {
        InputStream json = httpCommunicator.get(request.authToken(), "/game");
        return new Gson().fromJson(new InputStreamReader(json), ListGamesResponse.class);
    }

    public JoinGameResponse joinGame(JoinGameRequest request) {
        InputStream json = httpCommunicator.put(request.authToken(), new Gson().toJson(request), "/game");
        return new Gson().fromJson(new InputStreamReader(json), JoinGameResponse.class);
    }

    public void observeGame(int gameID) {
    }

    public LogoutResponse logout(String authToken) {
        InputStream json = httpCommunicator.delete(authToken, "/session");
        return new Gson().fromJson(new InputStreamReader(json), LogoutResponse.class);
    }
}


package network;

import chess.ChessGame;
import com.google.gson.Gson;
import model.UserData;
import model.requestresult.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerFacade {
    private HTTPCommunicator httpCommunicator;
    private static int port;
    private WebSocketCommunicator ws;
    private ServerMessageObserver serverMessageObserver;
    String url;

    public ServerFacade (int porty) {
        port = porty;
        url = "http://localhost:" + port;
        httpCommunicator = new HTTPCommunicator(port);
    }

    public static int getPort() {
        return port;
    }


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

    public JoinGameResponse joinGame(JoinGameRequest request) throws Exception {
        InputStream json = httpCommunicator.put(request.authToken(), new Gson().toJson(request), "/game");
        JoinGameResponse response = new Gson().fromJson(new InputStreamReader(json), JoinGameResponse.class);
        if (response.message() == null){
            try {
                ws = new WebSocketCommunicator(url, serverMessageObserver);
            } catch (Exception e) {
                throw new Exception();
            }
        }
        return response;
    }

    public ListGamesResponse observeGame(int gameID, String authToken) {
        InputStream json = httpCommunicator.get(authToken, "/game");
        return new Gson().fromJson(new InputStreamReader(json), ListGamesResponse.class);
    }

    public LogoutResponse logout(String authToken) {
        InputStream json = httpCommunicator.delete(authToken, "/session");
        return new Gson().fromJson(new InputStreamReader(json), LogoutResponse.class);
    }
}


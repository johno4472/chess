package network;

import com.google.gson.Gson;
import model.requestresult.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Map;

public class ServerFacade {

    public void register(RegisterResult request) {
        // Specify the desired endpoint
        URI uri = null;
        try {
            uri = new URI("http://localhost:8080/name");
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod("PUT");

            // Specify that we are going to write out data
            http.setDoOutput(true);

            // Write out a header
            http.addRequestProperty("Content-Type", "application/json");

            // Write out the body
            var body = Map.of("bud", "joe", "sue", "tom");
            try (var outputStream = http.getOutputStream()) {
                var jsonBody = new Gson().toJson(body);
                outputStream.write(jsonBody.getBytes());
            }

            // Make the request
            http.connect();

            // Output the response body
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                System.out.println(new Gson().fromJson(inputStreamReader, Map.class));
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void login(LoginRequest request) {

    }

    public void createGame(CreateGameRequest request){

    }

    public void listGames(ListGamesRequest request) {

    }

    public void joinGame(JoinGameRequest request) {

    }

    public void observeGame(int gameID) {

    }

    public void logout(String authToken) {

    }
}


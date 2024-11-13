package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

public class HTTPCommunicator {

    private HttpURLConnection getURLConnection(String urlPath){
        try {
            URI uri = new URI("http://localhost:8080" + urlPath);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            return http;
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String authToken, String urlPath) {
        //create url
        HttpURLConnection http = getURLConnection(urlPath);
        try {
            http.setRequestMethod("GET");

            //create header
            http.addRequestProperty("authorization", authToken);

            //send
            http.connect();

            //get and return json response
            try (InputStream respBody = http.getInputStream()){
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                System.out.println(new Gson().fromJson(inputStreamReader, Map.class));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public String put(String authToken, String jsonBody, String urlPath) {
        //create url
        //create header
        //create body
        //send
        //get and return json response
        return "";
    }

    public String delete(String authToken, String urlPath) {
        //create url
        //create header
        //send
        //get and return json response\
        return "";
    }

    public String post(String authToken, String jsonBody, String urlPath) {
        //create url
        //create header
        //create body
        //send
        //get and return json response
        return "";
    }
}

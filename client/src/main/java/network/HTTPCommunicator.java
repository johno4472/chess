package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class HTTPCommunicator {

    private HttpURLConnection getURLConnection(String urlPath){
        try {
            URI uri = new URI("http://localhost:8080" + urlPath);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setReadTimeout(5000);
            http.setDoOutput(true);
            return http;
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStreamReader get(String authToken, String urlPath) {
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
                return new InputStreamReader(respBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStreamReader put(String authToken, String jsonBody, String urlPath) {
        //create url
        HttpURLConnection http = getURLConnection(urlPath);
        try {
            http.setRequestMethod("PUT");

            //create header
            http.addRequestProperty("authorization", authToken);

            //create body
            try (OutputStream requestBody = http.getOutputStream();) {
                requestBody.write(jsonBody.getBytes());
            }

            //send
            http.connect();

            //get and return json response
            try (InputStream respBody = http.getInputStream()){
                return new InputStreamReader(respBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStreamReader delete(String authToken, String urlPath) {
        //create url
        HttpURLConnection http = getURLConnection(urlPath);
        try {
            http.setRequestMethod("DELETE");

            //create header
            http.addRequestProperty("authorization", authToken);

            //send
            http.connect();

            //get and return json response
            try (InputStream respBody = http.getInputStream()){
                return new InputStreamReader(respBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStreamReader post(String authToken, String jsonBody, String urlPath) {
        //create url
        HttpURLConnection http = getURLConnection(urlPath);
        try {
            http.setRequestMethod("POST");

            //create header
            http.addRequestProperty("authorization", authToken);

            //create body
            try (OutputStream reqBody = http.getOutputStream();){
                reqBody.write(jsonBody.getBytes());
            }

            //send
            http.connect();

            //get and return json response
            try (InputStream respBody = http.getInputStream()){
                return new InputStreamReader(respBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
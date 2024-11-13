package model.requestresult;

public record LoginResponse(String username, String authToken, String message) {
}

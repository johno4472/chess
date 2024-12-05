package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData nullifyBlack() {
        return new GameData(gameID, whiteUsername, null, gameName, game);
    }

    public GameData nullifyWhite() {
        return new GameData(gameID, null, blackUsername, gameName, game);
    }
}

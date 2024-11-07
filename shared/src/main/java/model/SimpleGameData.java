package model;

import chess.ChessGame;

public record SimpleGameData(Integer gameID, String whiteUsername, String blackUsername, String gameName) {
}

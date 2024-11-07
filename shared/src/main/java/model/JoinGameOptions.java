package model;

import chess.ChessGame;

public record JoinGameOptions(ChessGame.TeamColor playerColor, int gameID) {
}

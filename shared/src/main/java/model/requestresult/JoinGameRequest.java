package service.requestresult;

import chess.ChessGame.TeamColor;

public record JoinGameRequest(TeamColor playerColor, int gameID, String authToken) {
}

package service.requestresult;

import chess.ChessGame.TeamColor;

public record JoinGameRequest(TeamColor color, int gameID) {
}

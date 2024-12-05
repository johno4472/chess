package network;

import chess.ChessGame;
import chess.ChessPosition;
import ui.HighlightBoardUI;
import websocket.messages.ServerMessage;

public class ServerMessageSender implements ServerMessageObserver {
    @Override
    public void notify(ServerMessage serverMessage) {
        String message = serverMessage.getMessageBody();
        switch (serverMessage.getServerMessageType()){
            case ERROR -> sendErrorMessage(serverMessage.getErrorMessage());
            case LOAD_GAME -> loadGame(message, serverMessage.getChessBoard(), serverMessage.getColor(),
                    serverMessage.getErrorMessage());
            case NOTIFICATION -> sendNotification(message, serverMessage);
        }
    }

    public void sendErrorMessage(String errorMessage){
        System.out.println(errorMessage);
    }



    public void loadGame(String message, ChessGame chessGame, ChessGame.TeamColor color, String piece) {
        if (color == null){
            HighlightBoardUI.main(chessGame, ChessGame.TeamColor.WHITE, null);
        }
        else {
            if (message != null && message.equals("highlight")){
                int row = Character.getNumericValue(piece.charAt(0));
                int col = Character.getNumericValue(piece.charAt(1));
                ChessPosition position = new ChessPosition(row, col);
                HighlightBoardUI.main(chessGame, color, position);

            }
            else{
                HighlightBoardUI.main(chessGame, color, null);
            }
        }
        if (message != null){
            System.out.println(message);
        }
    }

    public void sendNotification(String message, ServerMessage serverMessage){
        if (message != null) {
            System.out.println(message);
        }
    }
}

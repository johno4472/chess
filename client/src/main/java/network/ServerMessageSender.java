package network;

import chess.ChessBoard;
import chess.ChessGame;
import websocket.messages.ServerMessage;
import ui.BoardUI;

public class ServerMessageSender implements ServerMessageObserver {
    @Override
    public void notify(ServerMessage serverMessage) {
        String message = serverMessage.getMessageBody();
        switch (serverMessage.getServerMessageType()){
            case ERROR -> sendErrorMessage(serverMessage.getErrorMessage());
            case LOAD_GAME -> loadGame(message, serverMessage.getChessBoard(), serverMessage.getColor());
            case NOTIFICATION -> sendNotification(message, serverMessage);
        }
    }

    public void sendErrorMessage(String errorMessage){
        System.out.println(errorMessage);
    }

    public void loadGame(String message, ChessBoard chessBoard, ChessGame.TeamColor color) {
        if (color == null){
            BoardUI.main(chessBoard, ChessGame.TeamColor.WHITE);
        }
        else {
            BoardUI.main(chessBoard, color);
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

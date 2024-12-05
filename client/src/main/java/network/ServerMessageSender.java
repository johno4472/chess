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
            case ERROR -> sendErrorMessage(message);
            case LOAD_GAME -> loadGame(message, serverMessage.getChessBoard());
            case NOTIFICATION -> sendNotification(message);
        }
    }

    public void sendErrorMessage(String message){

    }

    public void loadGame(String message, ChessBoard chessBoard) {
        BoardUI.main(chessBoard, ChessGame.TeamColor.WHITE);
        System.out.println(message);
    }

    public void sendNotification(String message){
        System.out.println(message);
    }
}

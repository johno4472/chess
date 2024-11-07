package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard chessBoard;
    private TeamColor teamTurn;

    public ChessGame() {
        chessBoard = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        chessBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
        }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        teamTurn = team;

    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public ChessBoard copyBoard(ChessBoard board){
        ChessBoard newBoard = new ChessBoard();
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                newBoard.addPiece(new ChessPosition(i, j), board.getPiece(new ChessPosition(i, j)));
            }
        }
        return newBoard;
    }

    public Collection<ChessMove> getOutOfCheck(ChessPosition startPosition, Collection<ChessMove> moves) {
        Collection<ChessMove> newMoves = new ArrayList<>();
        TeamColor color = chessBoard.getPiece(startPosition).getTeamColor();
        for (ChessMove m: moves){
            ChessBoard tempChessBoard = copyBoard(chessBoard);
            doTheMove(tempChessBoard, m);
            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(tempChessBoard);
            tempGame.setTeamTurn(color);
            if (!tempGame.isInCheck(color)){
                newMoves.add(m);
            }
        }
        return newMoves;
    }
    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get piece type
        //return valid moves
        ChessPiece piece = chessBoard.getPiece(startPosition);
        if (piece == null){
            return null;
        }
        return getOutOfCheck(startPosition, piece.pieceMoves(chessBoard, startPosition));
    }

    public Boolean isIn (ChessMove move, Collection<ChessMove> moves){
        for (ChessMove m : moves){
            if (move.getEndPosition().equals(m.getEndPosition())){
                return true;
            }
        }
        return false;
    }

    public void doTheMove(ChessBoard board, ChessMove move){
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);
        board.addPiece(start, null);
        if (move.getPromotionPiece() == null){
            board.addPiece(move.getEndPosition(), piece);
        }
        else {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //chess board at start position is now empty
        //chess board at end position is now that piece
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = chessBoard.getPiece(start);
        if (piece == null){
            throw new InvalidMoveException("No piece here!");
        }
        if (piece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("It's not your turn!");
        }
        if (isIn(move, validMoves(start))){
            doTheMove(chessBoard, move);
        }
        else{
            throw new InvalidMoveException("This move is Invalid");
        }
        if (teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        }
        else{
            teamTurn = TeamColor.WHITE;
        }
    }

    public Boolean kingInDanger(ChessPosition kingPosition, Collection<ChessMove> moves){
        for (ChessMove m : moves){
           if (kingPosition.equals(m.getEndPosition())){
               return true;
           }
        }
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = chessBoard.findKing(teamColor);
        if (kingPosition == null){
            return false;
        }
        //if king of team color is in valid moves of other color, return true
        //find king on chess board
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() != teamColor){
                    if (kingInDanger(kingPosition, piece.pieceMoves(chessBoard, new ChessPosition(i, j)))){
                        return true;
                    }
                }
            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        //if king of teamcolor has no valid moves, return true
        if (isInCheck(teamColor)){
            for (int i = 1; i < 9; i++){
                for (int j = 1; j < 9; j++){
                    ChessPiece piece = chessBoard.getPiece(new ChessPosition(i, j));
                    if (piece != null && piece.getTeamColor() == teamColor &&
                            !validMoves(new ChessPosition(i, j)).isEmpty()){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //if valid move length is 0, return true
        if (isInCheckmate(teamColor)){
            return false;
        }
        //this one was tricky
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                if (chessBoard.getPiece(new ChessPosition(i,j)) != null
                        && chessBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor){
                    if (!validMoves(new ChessPosition(i, j)).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;

    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }
}

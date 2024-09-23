package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Arrays;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        //throw new RuntimeException("Not implemented");
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        //throw new RuntimeException("Not implemented");
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //throw new RuntimeException("Not implemented");

        return switch (this.type) {
            case KING -> kingMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        //create array
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        //create variables for chess piece movement
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //make cycling for loops to check availability for all moves
        for (int newRow = row - 1; newRow <= row + 1; newRow++){
            for (int newCol = col - 1; newCol <= col + 1; newCol++){
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPiece otherPiece = board.getPiece(new ChessPosition(newRow, newCol));
                    if (otherPiece==null || otherPiece.getTeamColor() != this.getTeamColor()) {
                        addPossibleMove(possibleMoves, row, col, newRow, newCol);
                    }
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece otherPiece;

        int colorVar = 1;
        if (this.getTeamColor() == ChessGame.TeamColor.BLACK) {
            colorVar = -1;
        }

        if (row+(1*colorVar) > 8 || row+(1*colorVar) < 1){
            return possibleMoves;
        }
        //can move two forward if still in original place
        if ((row == 2 && this.getTeamColor() == ChessGame.TeamColor.WHITE) ||
                (row == 7 && this.getTeamColor() == ChessGame.TeamColor.BLACK)){
            otherPiece = board.getPiece(new ChessPosition(row + (2*colorVar), col));
            ChessPiece onePiece = board.getPiece(new ChessPosition(row + (1*colorVar), col));
            if (otherPiece==null && onePiece == null){
                addPossibleMove(possibleMoves, row, col, row+(2*colorVar), col);
            }
        }
        otherPiece = board.getPiece(new ChessPosition(row + (1*colorVar), col));
        if (otherPiece==null){
            if (row+(1*colorVar) == 1 || row+(1*colorVar) == 8) {
                addMovePromotion(possibleMoves, row, col, row+(1*colorVar), col);
            }
            else {
                addPossibleMove(possibleMoves, row, col, row+(1*colorVar), col);
            }
        }
        //pawn can move one forward if no one is there


        //can move diagonally forward if taking the place of an enemy
        if (col+1 <= 8){
            otherPiece = board.getPiece(new ChessPosition(row + (1*colorVar), col + 1));
            if (otherPiece!=null){
                if (otherPiece.getTeamColor() != this.getTeamColor()) {
                    if (row + (1 * colorVar) == 1 || row + (1 * colorVar) == 8) {
                        addMovePromotion(possibleMoves, row, col, row + (1 * colorVar), col + 1);
                    } else {
                        addPossibleMove(possibleMoves, row, col, row + (1 * colorVar), col + 1);
                    }
                }
            }
        }

        if (col-1 >= 1){
            otherPiece = board.getPiece(new ChessPosition(row + (1*colorVar), col-1));
            if (otherPiece!=null){
                if (otherPiece.getTeamColor() != this.getTeamColor()){
                    if (row+(1*colorVar) == 1 || row+(1*colorVar) == 8) {
                        addMovePromotion(possibleMoves, row, col, row+(1*colorVar), col-1);
                    }
                    else {
                        addPossibleMove(possibleMoves, row, col, row+(1*colorVar), col-1);
                    }

                }
            }
        }


        return possibleMoves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //rooks can go up, down, right, and left
        checkExtended(board, row, col, 1, 0, possibleMoves); //up
        checkExtended(board, row, col, -1, 0, possibleMoves); //down
        checkExtended(board, row, col, 0, 1, possibleMoves); //right
        checkExtended(board, row, col, 0, -1, possibleMoves); //left

        return possibleMoves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //queens can do all rook moves
        checkExtended(board, row, col, 1, 0, possibleMoves); //up
        checkExtended(board, row, col, -1, 0, possibleMoves); //down
        checkExtended(board, row, col, 0, 1, possibleMoves); //right
        checkExtended(board, row, col, 0, -1, possibleMoves); //left

        //and all bishop moves
        checkExtended(board, row, col, 1, 1, possibleMoves); //up and right
        checkExtended(board, row, col, 1, -1, possibleMoves); //up and left
        checkExtended(board, row, col, -1, 1, possibleMoves); //down and right
        checkExtended(board, row, col, -1, -1, possibleMoves); //down and left

        return possibleMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //Bishops can do all diagonal moves
        checkExtended(board, row, col, 1, 1, possibleMoves); //up and right
        checkExtended(board, row, col, -1, 1, possibleMoves); //up and left
        checkExtended(board, row, col, -1, -1, possibleMoves); //down and right
        checkExtended(board, row, col, 1, -1, possibleMoves); //down and left

        return possibleMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //rooks can move two in one direction and one in the other
        checkOne(board, row, col, 2, -1, possibleMoves);
        checkOne(board, row, col, 2, 1, possibleMoves);
        checkOne(board, row, col, -2, 1, possibleMoves);
        checkOne(board, row, col, -2, -1, possibleMoves);
        checkOne(board, row, col, 1, 2, possibleMoves);
        checkOne(board, row, col, -1, 2, possibleMoves);
        checkOne(board, row, col, 1, -2, possibleMoves);
        checkOne(board, row, col, -1, -2, possibleMoves);

        return possibleMoves;
    }

    public void checkExtended(ChessBoard board, int row, int col, int rowAdd, int colAdd,
                              Collection<ChessMove> possibleMoves){

        int newCol = col;
        for (int newRow = row+rowAdd; newRow <= 8 && newRow >= 1; newRow+=rowAdd){
            newCol+=colAdd;
            if (newCol < 1 || newCol > 8) {
                return;
            }
            ChessPiece otherPiece = board.getPiece(new ChessPosition(newRow, newCol));
            if (otherPiece==null){
                addPossibleMove(possibleMoves, row, col, newRow, newCol);
            }
            else if (otherPiece.getTeamColor() != this.getTeamColor()) {
                addPossibleMove(possibleMoves, row, col, newRow, newCol);
                return;
            }
            else {
                return;
            }
        }
    }

    public void checkOne(ChessBoard board, int row, int col, int rowAdd, int colAdd,
                         Collection<ChessMove> possibleMoves){

        int newRow = row+rowAdd;
        int newCol = col+colAdd;

        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8){
            ChessPiece otherPiece = board.getPiece(new ChessPosition(newRow, newCol));
            if (otherPiece==null){
                addPossibleMove(possibleMoves, row, col, newRow, newCol);
            }
            else if (otherPiece.getTeamColor() != this.getTeamColor()) {
                addPossibleMove(possibleMoves, row, col, newRow, newCol);
            }
        }
    }

    public void addPossibleMove(Collection<ChessMove> possibleMoves, int row, int col, int newRow, int newCol) {
        possibleMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol),
                null));
    }

    public void addMovePromotion(Collection<ChessMove> possibleMoves, int row, int col, int newRow,
                                 int newCol) {
        possibleMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol),
                PieceType.QUEEN));
        possibleMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol),
                PieceType.BISHOP));
        possibleMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol),
                PieceType.ROOK));
        possibleMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newRow, newCol),
                PieceType.KNIGHT));
    }

}

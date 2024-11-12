package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class BoardUI {
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = "   ";

    private static int row = 0;
    private static int whiteView = 1;
    private static int colorCount = 0;
    private static String squareColor = "White";
    private static ChessBoard board = new ChessGame().getBoard();

    private static final String [] COLUMNS_IN_ORDER = {"a", "b", "c", "d", "e", "f", "g", "h"};


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawTopOrBottom(out);

        drawChessBoard(out);

        drawTopOrBottom(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawTopOrBottom(PrintStream out) {

        setMagenta(out);

        out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));

        for (int boardCol = 0; boardCol < COLUMNS_IN_ORDER.length; ++boardCol) {
            printHeaderText(out, " " + COLUMNS_IN_ORDER[boardCol] + " ");
        }

        out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));

        setDarkGrey(out);

        out.println();
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_YELLOW);

        out.print(player);

        setMagenta(out);
    }

    private static void drawChessBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES - 2; ++boardRow) {

            drawRowOfSquares(out, boardRow);
            colorCount += 1;
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {

        printRowHeader(out, boardRow + 1);

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES - 2; ++boardCol) {
                setBoxColor(out);
                printPlayer(out, board.getPiece(new ChessPosition(boardRow + 1, boardCol + 1)));

                setDarkGrey(out);
            }

            printRowHeader(out, boardRow + 1);
            row += 1;
            setDarkGrey(out);

            out.println();
        }
    }

    private static void setBoxColor(PrintStream out) {
        colorCount %= 2;
        if (colorCount == 0) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        }
        else {
            out.print(SET_BG_COLOR_BLUE);
        }
        colorCount += 1;
    }

    private static void printRowHeader(PrintStream out, int boardRow) {
        String rowString = " " + boardRow + " ";
        printHeaderText(out, rowString);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setMagenta(PrintStream out) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private static void setDarkGrey(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void printPlayer(PrintStream out, ChessPiece piece) {
        if (piece == null) {
            out.print(EMPTY);
            return;
        }
        else if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)){
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(pieceToString(piece.getPieceType()));
        }
        else {
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(pieceToString(piece.getPieceType()));
        }

        setWhite(out);
    }

    private static String pieceToString(ChessPiece.PieceType type){
        return switch (type) {
            case KNIGHT -> " N ";
            case BISHOP -> " B ";
            case QUEEN -> " Q ";
            case ROOK -> " R ";
            case PAWN -> " P ";
            case KING -> " K ";
        };
    }
}

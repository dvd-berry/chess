package chess;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];

    }
    public ChessBoard(ChessBoard other) {
        board = new ChessPiece[8][8];
        for(int i = 0; i < 8; i++) {
            System.arraycopy(other.board[i], 0, board[i], 0, 8);
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board[0][0] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        board[0][1] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        board[0][2] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        board[0][3] = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
        board[0][4] = new ChessPiece(TeamColor.WHITE, PieceType.KING);
        board[0][5] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        board[0][6] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        board[0][7] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);
        }

        board[7][0] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        board[7][1] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        board[7][2] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        board[7][3] = new ChessPiece(TeamColor.BLACK, PieceType.QUEEN);
        board[7][4] = new ChessPiece(TeamColor.BLACK, PieceType.KING);
        board[7][5] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        board[7][6] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        board[7][7] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
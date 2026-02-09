package chess;

import java.util.*;
import chess.ChessPiece.PieceType;

import static chess.ChessBoard.CASTLING_MOVES;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;

    




    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }


    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }
    public void switchTurn() {
        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return board.validMoves(startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece == null || piece.getTeamColor() != turn)
            throw new InvalidMoveException();
        if(board.validMoves(move.getStartPosition()).contains(move)) {
            board.maintainCastlingPermissions(move);
            board.makeMove(move);
            board.existsEnPassant = false;
            if(piece.getPieceType() == PieceType.PAWN && Math.abs(move.startPosition().getRow() - move.endPosition().getRow()) == 2) {
                int direction = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;
                ChessPiece adjacentLeft = piece.isValidIndex(move.endPosition().getColumn()-1) ? board.getPiece(new ChessPosition(move.endPosition().getRow(), move.endPosition().getColumn()-1)) : null;
                ChessPiece adjacentRight = piece.isValidIndex(move.endPosition().getColumn()+1) ? board.getPiece(new ChessPosition(move.endPosition().getRow(), move.endPosition().getColumn()+1)) : null;
                if(adjacentLeft != null && adjacentLeft.getPieceType() == PieceType.PAWN && adjacentLeft.getTeamColor() != piece.getTeamColor()){
                    board.existsEnPassant = true;
                    board.enPassantSquare = new ChessPosition(move.endPosition().getRow() - direction, move.endPosition().getColumn());
                }
                if(adjacentRight != null && adjacentRight.getPieceType() == PieceType.PAWN && adjacentRight.getTeamColor() != piece.getTeamColor()){
                    board.existsEnPassant = true;
                    board.enPassantSquare = new ChessPosition(move.endPosition().getRow() - direction, move.endPosition().getColumn());
                }
            }
            switchTurn();
        }
        else throw new InvalidMoveException();
    }
    private void castle(ChessMove move) {
        board.castle(move);
        switchTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return board.isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return board.isInCheckmate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return board.isInStalemate(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard();
        this.board.copy(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }
}
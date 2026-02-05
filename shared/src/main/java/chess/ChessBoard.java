package chess;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;
    private final ChessPosition[] kingPositions;

    public ChessBoard() {
        board = new ChessPiece[8][8];
        kingPositions = new ChessPosition[2];
        resetBoard();
    }
    public ChessBoard(ChessBoard other) {
        board = new ChessPiece[8][8];
        kingPositions = new ChessPosition[2];
        for(int i = 0; i < 8; i++)
            System.arraycopy(other.board[i], 0, this.board[i], 0, 8);
        System.arraycopy(other.kingPositions, 0, this.kingPositions, 0,2);
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
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
        addPiece(new ChessPosition(1, 1), new ChessPiece(TeamColor.WHITE, PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(TeamColor.WHITE, PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(TeamColor.WHITE, PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(TeamColor.WHITE, PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(TeamColor.WHITE, PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(TeamColor.WHITE, PieceType.ROOK));
        for (int i = 1; i <= 8; i++)
            addPiece(new ChessPosition(2, i), new ChessPiece(TeamColor.WHITE, PieceType.PAWN));

        addPiece(new ChessPosition(8, 1), new ChessPiece(TeamColor.BLACK, PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(TeamColor.BLACK, PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(TeamColor.BLACK, PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(TeamColor.BLACK, PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(TeamColor.BLACK, PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(TeamColor.BLACK, PieceType.ROOK));
        for (int i = 1; i <= 8; i++)
            addPiece(new ChessPosition(7, i), new ChessPiece(TeamColor.BLACK, PieceType.PAWN));

        kingPositions[0] = new ChessPosition(1, 5);
        kingPositions[1] = new ChessPosition(8, 5);
    }

    public ChessPosition[] getKingPositions(){
        return kingPositions;
    }
    private ChessPosition getKingPosition(TeamColor color) {
        return kingPositions[color.ordinal()];
    }
    public void setKingPositions(ChessPosition[] kingPositions){
        System.arraycopy(kingPositions, 0, this.kingPositions, 0, 2);
    }
    public Collection<ChessMove> validMoves(ChessPosition position) {
        ChessPiece piece = this.getPiece(position);
        if(piece == null)
            return Collections.emptyList();
        Collection<ChessMove> potentialMoves = piece.pieceMoves(this, position);
        potentialMoves.removeIf(move -> !isValidMove(move)); // removes all invalid moves

        return potentialMoves;
    }
    public boolean isValidMove(ChessMove move) {
        ChessPiece piece = this.getPiece(move.startPosition());
        ChessBoard duplicate = new ChessBoard(this);
        duplicate.makeMove(move);
        return !duplicate.isInCheck(piece.getTeamColor()); // returns false if team that made move finishes in check
    }
    public void makeMove(ChessMove move) {
        ChessPiece piece = this.getPiece((move.startPosition()));
        this.addPiece(move.endPosition(), piece);
        this.addPiece(move.startPosition(), null);
        if(piece.getPieceType() == PieceType.KING)
            updateKingPos(move, piece.getTeamColor());
    }
    private void updateKingPos(ChessMove move, TeamColor color) {
        kingPositions[color.ordinal()] = move.endPosition();
    }
    public boolean isInCheck(TeamColor team) {
        for (int i = 1; i <= 8; i++)
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = getPiece(position);
                if(piece == null || piece.getTeamColor() == team)
                    continue;
                for(ChessMove move : piece.pieceMoves(this, position))
                    if(move.endPosition().equals(getKingPosition(team)))
                        return true;
            }
        return false;
    }
    public boolean isInCheckmate(TeamColor team) {
        return isInCheck(team) && noLegalMoves(team);
    }
    public boolean isInStalemate(TeamColor team) {
        return !isInCheck(team) && noLegalMoves(team);
    }
    private boolean noLegalMoves(TeamColor team) {
        for(int i = 1; i <=8; i++)
            for(int j = 1; j <=8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = this.getPiece(position);
                if(piece == null || piece.getTeamColor() != team)
                    continue;
                if(!validMoves(position).isEmpty()) {
                    return false;
                }
            }
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board) && Objects.deepEquals(kingPositions, that.kingPositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(board), Arrays.hashCode(kingPositions));
    }
}

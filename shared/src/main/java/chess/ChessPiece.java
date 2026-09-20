package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import chess.ChessGame.TeamColor;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final TeamColor teamColor;
    private final PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        this.type = type;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return teamColor == piece.teamColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
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
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    private boolean isValidIndex(int val) {
        return (val >= 1 && val <= 8);
    }

    private boolean isCapture(ChessBoard board, ChessPosition position) {
        return !board.isEmptySquare(position) && board.getPiece(position).getTeamColor() != teamColor;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return board.getPiece(myPosition).getPieceType() == PieceType.PAWN ? pawnMoves(board, myPosition) : majorPieceMoves(board, myPosition);
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        boolean isStartSquare = teamColor == TeamColor.WHITE ? row == 2 : row == 7;
        boolean isPromotion = teamColor == TeamColor.WHITE ? row == 7 : row == 2;

        int direction = teamColor == TeamColor.WHITE ? 1 : -1;
        ChessPosition oneForward = new ChessPosition(row + direction, col);
        ChessPosition twoForward = new ChessPosition(row + direction*2, col);
        ChessPosition captureLeft = isValidIndex(myPosition.getColumn()-1) ? new ChessPosition(row+direction, col-1) : null;
        ChessPosition captureRight = isValidIndex(myPosition.getColumn()+1) ? new ChessPosition(row+direction, col+1) : null;

        PieceType[] promotionPieces = isPromotion ? new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT} : new PieceType[]{null};

        if(isStartSquare && board.isEmptySquare(oneForward) && board.isEmptySquare(twoForward))
            moves.add(new ChessMove(myPosition, twoForward, null));
        if(board.isEmptySquare(oneForward))
            for (PieceType promotionPiece : promotionPieces)
                moves.add(new ChessMove(myPosition, oneForward, promotionPiece));
        if(!Objects.isNull(captureLeft) && isCapture(board, captureLeft))
            for (PieceType promotionPiece : promotionPieces)
                moves.add(new ChessMove(myPosition, captureLeft, promotionPiece));
        if(!Objects.isNull(captureRight) && isCapture(board, captureRight))
            for (PieceType promotionPiece : promotionPieces)
                moves.add(new ChessMove(myPosition, captureRight, promotionPiece));

        return moves;
    }
    private Collection<ChessMove> majorPieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int[][] dirVec = getVector();

        for (int[] d : dirVec) {
            int r = row + d[0];
            int c = col + d[1];
            while(isValidIndex(r) && isValidIndex(c)) {
                ChessPosition target = new ChessPosition(r, c);
                if (board.isEmptySquare(target))
                    moves.add(new ChessMove(myPosition, target, null));
                else {
                    if (isCapture(board, target)) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    break; // ends direction if runs into piece
                }
                if (type == PieceType.KING || type == PieceType.KNIGHT)
                    break; // can only move one unit
                r += d[0];
                c += d[1];
            }
        }

        return moves;
    }

    private int[][] getVector() {
        int[][] cardinalVec = {{-1, 0}, {1,0}, {0,1}, {0,-1}};
        int[][] diagonalVec = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
        int[][] compassVec = {{-1, 0}, {1,0}, {0,1}, {0,-1}, {-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
        int[][] knightVec = {{-2, -1}, {-2, 1}, {-1, 2}, {1, 2}, {2,1}, {2, -1}, {1, -2}, {-1, -2}};

        return switch (type) {
            case ROOK -> cardinalVec;
            case BISHOP -> diagonalVec;
            case KING,QUEEN -> compassVec;
            case KNIGHT -> knightVec;
            default -> throw new IllegalStateException("Unwated Piece Type: " + type);
        };
    }
}

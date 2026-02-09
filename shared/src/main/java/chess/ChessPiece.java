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
    private final TeamColor pieceColor;
    private final PieceType type;


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
        return pieceColor;
    }
    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }


    public boolean isCapture(ChessBoard board, ChessPosition position) {
        return position != null && !board.isEmptySquare(position) && board.getPiece(position).getTeamColor() != pieceColor;
    }
    private boolean isValidIndex(int val) {
        return val >= 1 && val <= 8;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        boolean isStartSquare = (pieceColor == TeamColor.WHITE && row == 2) || (pieceColor == TeamColor.BLACK && row == 7);
        boolean isPromotion = (pieceColor == TeamColor.WHITE && row == 7) || (pieceColor == TeamColor.BLACK && row == 2);
        int direction = pieceColor == TeamColor.WHITE ? 1 : -1;
        int forwardRow = row + direction;
        PieceType[] promotionPieces = isPromotion ? new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP} : new PieceType[]{ null };

        ChessPosition oneForward = new ChessPosition(forwardRow, column);
        ChessPosition twoForward = isStartSquare ? new ChessPosition(forwardRow + direction, column) : null;
        ChessPosition captureLeft = isValidIndex(column-1) ? new ChessPosition(forwardRow, column-1) : null;
        ChessPosition captureRight = isValidIndex(column+1) ? new ChessPosition(forwardRow, column+1) : null;

        if(isStartSquare && board.isEmptySquare(oneForward) && board.isEmptySquare(twoForward))
            moves.add(new ChessMove(myPosition, twoForward, null)); // Starting move
        if(board.isEmptySquare(oneForward))
            for(PieceType promotion : promotionPieces) // promotionPieces contains either all promotionPieces or null if not a promotion
                moves.add(new ChessMove(myPosition, oneForward, promotion)); // Normal forward move
        if(isCapture(board, captureLeft))
            for(PieceType promotion : promotionPieces)
                moves.add(new ChessMove(myPosition, captureLeft, promotion)); // Left captures
        if(isCapture(board, captureRight))
            for(PieceType promotion : promotionPieces)
                moves.add(new ChessMove(myPosition, captureRight, promotion)); // Right captures

        return moves;
    }
    private Collection<ChessMove> majorPieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        int[][] dirVec = getVector(); // Vectors for all possible directions for any given piece

        for (int[] d : dirVec) {
            int r = row + d[0];
            int c = column + d[1];

            while (isValidIndex(r) && isValidIndex(c)) {
                ChessPosition target = new ChessPosition(r, c);
                if (board.isEmptySquare(target))
                    moves.add(new ChessMove(myPosition, target, null));
                else { // Captures or friendly pieces blocking
                    if (isCapture(board, target))
                        moves.add(new ChessMove(myPosition, target, null));
                    break; // Ends that direction
                }
                if (type == PieceType.KING || type == PieceType.KNIGHT) // Can only move one unit of their direction
                    break;
                r += d[0]; // Continues to iterate on direction vector
                c += d[1];
            }
        }
        return moves;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return (type == PieceType.PAWN) ? pawnMoves(board, myPosition) : majorPieceMoves(board, myPosition);
    }

    private int[][] getVector() {
        int[][] cardinalVec = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int[][] intercardinalVec = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int[][] compassVec = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int[][] knightVec = {{-2,1}, {-1,2}, {1, 2}, {2, 1},{2, -1}, {1, -2},{-1, -2},{-2, -1}};

        return switch (type) {
            case ROOK -> cardinalVec;
            case BISHOP -> intercardinalVec;
            case QUEEN, KING -> compassVec;
            case KNIGHT -> knightVec;
            default -> throw new IllegalStateException("Unwanted Piece Type: " + type);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}

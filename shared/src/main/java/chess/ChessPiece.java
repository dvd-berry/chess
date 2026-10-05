package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return type == PieceType.PAWN ? pawnMoves(board, myPosition) : majorPieceMoves(board, myPosition);
    }

    private boolean isValidPosition(ChessBoard board, ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >=1 && position.getColumn() <= 8;
    }
    private boolean isValidIndex(int val) {
        return val >= 1 && val <= 8;
    }
    private boolean isCapture(ChessBoard board, ChessPosition position) {
        return !isEmptySquare(board, position) && board.getPiece(position).getTeamColor() != pieceColor;
    }
    private boolean isEmptySquare(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) == null;
    }
    private int[][] getVector() {
        int[][] cardinalVec = new int[][]{{1,0},{-1,0},{0,1},{0,-1}};
        int[][] diagonalVec = new int[][]{{1,1},{-1,-1},{-1,1},{1,-1}};
        int[][] everythingVec = new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{-1,1},{1,-1}};
        int[][] knightVec = new int[][]{{-2,1},{-1,2},{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1}};

        return switch(type) {
            case PieceType.KING, PieceType.QUEEN -> everythingVec;
            case PieceType.BISHOP -> diagonalVec;
            case PieceType.KNIGHT -> knightVec;
            case PieceType.ROOK -> cardinalVec;

            default -> throw new IllegalArgumentException("Unwanted Piece Type: " + type);
        };
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moveList = new ArrayList<ChessMove>();
        int row = position.getRow();
        int col = position.getColumn();
        boolean isStartSquare = pieceColor == TeamColor.WHITE ? row == 2 : row == 7;
        boolean isPromotion = pieceColor == TeamColor.WHITE ? row == 7 : row == 2;
        int direction = pieceColor == TeamColor.WHITE ? 1 : -1;
        PieceType[] promotionPieces = isPromotion ? new PieceType[]{PieceType.QUEEN, PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP} : new PieceType[]{null};

        ChessPosition oneForward = new ChessPosition(row + direction, col);
        ChessPosition twoForward = new ChessPosition(row + direction*2, col);
        ChessPosition captureLeft = new ChessPosition(row + direction, col-1);
        ChessPosition captureRight = new ChessPosition(row + direction, col+1);

        if(isEmptySquare(board, oneForward)) {
            for (PieceType type : promotionPieces) {
                moveList.add(new ChessMove(position, oneForward, type));
            }
            if(isStartSquare && isEmptySquare(board, twoForward))
                moveList.add(new ChessMove(position, twoForward, null));
        }
        if(isValidPosition(board, captureLeft) && isCapture(board, captureLeft)) {
            for (PieceType type : promotionPieces) {
                moveList.add(new ChessMove(position, captureLeft, type));
            }
        }
        if(isValidPosition(board, captureRight) && isCapture(board, captureRight)) {
            for (PieceType type : promotionPieces) {
                moveList.add(new ChessMove(position, captureRight, type));
            }
        }

        return moveList;
    }
    private Collection<ChessMove> majorPieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moveList = new ArrayList<ChessMove>();
        int row = position.getRow();
        int col = position.getColumn();
        int[][] directionVec = getVector();

        for (int[] d : directionVec) {
            int r = row + d[0];
            int c = col + d[1];
            while (isValidIndex(r) && isValidIndex(c)) {
                ChessPosition target = new ChessPosition(r, c);
                if(isEmptySquare(board, target)) {
                    moveList.add(new ChessMove(position, target, null));
                }
                else {
                    if(isCapture(board, target)) {
                        moveList.add(new ChessMove(position, target, null));
                    }
                    break;
                }
                if(type == PieceType.KING || type == PieceType.KNIGHT) {
                    break;
                }
                r += d[0];
                c += d[1];
            }
        }

        return moveList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
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
    private TeamColor pieceColor;
    private PieceType type;


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

    private boolean isNull(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) == null;
    }
    private boolean isCapture(ChessBoard board, ChessPosition position) {
        return !isNull(board, position) && board.getPiece(position).getTeamColor() != pieceColor;
    }
    private boolean isValidMove(ChessBoard board, ChessPosition position) {
        return isNull(board, position) || isCapture(board, position);
    }
    private boolean inbounds(int val) {
        return val >= 1 && val <= 8;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        PieceType promotionPieces[] = {PieceType.QUEEN, PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP};

        if(type == PieceType.KING) {
            for(int i = row-1; i <= row+1; i++)
                for(int j = column-1; j <= column+1; j++) {
                    if (!inbounds(i) || !inbounds(j) || (i == row && j == column))
                        continue;
                    if(isValidMove(board, new ChessPosition(i, j)))
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                }

        }
        if(type == PieceType.ROOK || type == PieceType.QUEEN) {
            for(int i = row-1; i >= 1; i--) {
                if(!inbounds(i))
                    continue;
                if(isNull(board, new ChessPosition(i, column)))
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, column), null));
                else if(isCapture(board, new ChessPosition(i, column))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, column), null));
                    break;
                }
                else break;
            }
            for(int i = row+1; i <=8; i++) {
                if(!inbounds(i))
                    continue;
                if(isNull(board, new ChessPosition(i, column)))
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, column), null));
                else if(isCapture(board, new ChessPosition(i, column))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, column), null));
                    break;
                }
                else break;
            }
            for(int i = column-1; i >= 1; i--) {
                if(!inbounds(i))
                    continue;
                if(isNull(board, new ChessPosition(row, i)))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, i), null));
                else if(isCapture(board, new ChessPosition(row, i))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, i), null));
                    break;
                }
                else break;
            }
            for(int i = column+1; i <= 8; i++) {
                if(!inbounds(i))
                    continue;
                if(isNull(board, new ChessPosition(row, i)))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, i), null));
                else if(isCapture(board, new ChessPosition(row, i))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, i), null));
                    break;
                }
                else break;
            }
        }
        if(type == PieceType.BISHOP || type == PieceType.QUEEN) {
            for(int i = row-1, j = column-1; i >= 1 && j >= 1; i--, j--) {
                if(!inbounds(i) || !inbounds(j))
                    continue;
                if(isNull(board, new ChessPosition(i, j)))
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                else if(isCapture(board, new ChessPosition(i, j))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;
                }
                else break;
            }
            for(int i = row+1, j = column-1; i <= 8 && j >= 1; i++, j--) {
                if(!inbounds(i) || !inbounds(j))
                    continue;
                if(isNull(board, new ChessPosition(i, j)))
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                else if(isCapture(board, new ChessPosition(i, j))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;
                }
                else break;
            }
            for(int i = row-1, j = column+1; i >= 1 && j <= 8; i--, j++) {
                if(!inbounds(i) || !inbounds(j))
                    continue;
                if(isNull(board, new ChessPosition(i, j)))
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                else if(isCapture(board, new ChessPosition(i, j))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;
                }
                else break;
            }
            for(int i = row+1, j = column+1; i <= 8 && j <= 8; i++, j++) {
                if(!inbounds(i) || !inbounds(j))
                    continue;
                if(isNull(board, new ChessPosition(i, j)))
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                else if(isCapture(board, new ChessPosition(i, j))) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    break;
                }
                else break;
            }
        }
        if(type == PieceType.KNIGHT) {
            int[] moveY = {-2, -1, 1, 2, 2, 1, -1, -2};
            int[] moveX = {1, 2, 2, 1, -1, -2, -2, -1};

            for(int i = 0; i < 8; i++) {
                if(!inbounds(row+moveX[i]) || !inbounds(column+moveY[i]))
                    continue;
                if(isValidMove(board, new ChessPosition(row+moveX[i], column+moveY[i])))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+moveX[i], column+moveY[i]),null));
            }
        }
        if(type == PieceType.PAWN) {
            if(pieceColor == TeamColor.WHITE) {
                if(row == 2) {
                    if(isNull(board, new ChessPosition(3, column)) && isNull(board, new ChessPosition(4, column)))
                        moves.add(new ChessMove(myPosition, new ChessPosition(4, column), null));
                }
                if(row <= 6) {
                    if(isNull(board, new ChessPosition(row+1, column)))
                        moves.add(new ChessMove(myPosition, new ChessPosition(row+1, column), null));
                    for(int i = column-1; i <= column+1; i++) {
                        if(!inbounds(i) || i == column)
                            continue;
                        if(isCapture(board, new ChessPosition(row+1, i))) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row+1, i), null));
                        }
                    }
                }
                if(row == 7) {
                    if(isNull(board, new ChessPosition(row+1, column))) {
                        for(PieceType promotion : promotionPieces)
                            moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, column), promotion));
                    }
                    for(int i = column-1; i <= column+1; i++) {
                        if(!inbounds(i) || i == column)
                            continue;
                        if(isCapture(board, new ChessPosition(row+1, i))) {
                            for(PieceType promotion : promotionPieces)
                                moves.add(new ChessMove(myPosition, new ChessPosition(row+1, i), promotion));
                        }
                    }
                }
            }
            if(pieceColor == TeamColor.BLACK) {
                if(row == 7) {
                    if(isNull(board, new ChessPosition(6, column)) && isNull(board, new ChessPosition(5, column)))
                        moves.add(new ChessMove(myPosition, new ChessPosition(5, column), null));
                }
                if(row >= 3) {
                    if(isNull(board, new ChessPosition(row-1, column)))
                        moves.add(new ChessMove(myPosition, new ChessPosition(row-1, column), null));
                    for(int i = column-1; i <= column+1; i++) {
                        if(!inbounds(i) || i == column)
                            continue;
                        if(isCapture(board, new ChessPosition(row-1, i))) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(row-1, i), null));
                        }
                    }
                }
                if(row == 2) {
                    if(isNull(board, new ChessPosition(row-1, column))) {
                        for(PieceType promotion : promotionPieces)
                            moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, column), promotion));
                    }
                    for(int i = column-1; i <= column+1; i++) {
                        if(!inbounds(i) || i == column)
                            continue;
                        if(isCapture(board, new ChessPosition(row-1, i))) {
                            for(PieceType promotion : promotionPieces)
                                moves.add(new ChessMove(myPosition, new ChessPosition(row-1, i), promotion));
                        }
                    }
                }
            }
        }


        return moves;
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

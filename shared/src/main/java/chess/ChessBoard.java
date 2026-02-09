package chess;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;

    public boolean longCastlingPrivilegesWhite = true;
    public boolean longCastlingPrivilegesBlack = true;
    public boolean shortCastlingPrivilegesWhite = true;
    public boolean shortCastlingPrivilegesBlack = true;
    public boolean existsEnPassant = false;
    public ChessPosition enPassantSquare;

    public static final Set<ChessMove> CASTLING_MOVES= Set.of(
            new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null),
            new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null),
            new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null),
            new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null));

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }
    public void copy(ChessBoard other) {
        for(int i = 0; i < 8; i++)
            System.arraycopy(other.board[i], 0, this.board[i], 0, 8);
    }
    public ChessPosition getKingPosition(TeamColor color) {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j<= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = getPiece(position);
                if(piece != null && piece.getPieceType() == PieceType.KING && piece.getTeamColor() == color)
                    return position;
            }
        }
        throw new IllegalStateException("There is no king on the board");
    }
    public Collection<ChessMove> validMoves(ChessPosition position) {
        ChessPiece piece = this.getPiece(position);
        if(piece == null)
            return Collections.emptyList();
        Collection<ChessMove> potentialMoves = piece.pieceMoves(this, position);
        potentialMoves.removeIf(move -> !isValidMove(move)); // removes all invalid moves
        if(piece.getPieceType() == PieceType.KING) {
            if(canCastle(piece.getTeamColor(), CastleType.SHORT)) {
                potentialMoves.add(new ChessMove(position, new ChessPosition(position.row(), 7), null));
            }
            if(canCastle(piece.getTeamColor(), CastleType.LONG)) {
                potentialMoves.add(new ChessMove(position, new ChessPosition(position.row(), 3), null));
            }
        }
        if(piece.getPieceType() == PieceType.PAWN && existsEnPassant && Math.abs(position.getColumn() - enPassantSquare.getColumn()) == 1) {
            potentialMoves.add(new ChessMove(position, enPassantSquare, null));
        }
        return potentialMoves;
    }
    private boolean isValidMove(ChessMove move) {
        ChessPiece piece = this.getPiece(move.startPosition());
        if(piece == null)
            return false;
        Collection<ChessMove> moveList = piece.pieceMoves(this, move.getStartPosition());
        if(!moveList.contains(move))
            return false;
        ChessBoard duplicate = new ChessBoard();
        duplicate.copy(this);
        duplicate.makeMove(move);
        return !duplicate.isInCheck(piece.getTeamColor());
    }
    public void makeMove(ChessMove move) {
        ChessPiece piece = this.getPiece((move.getStartPosition()));
        if(piece.getPieceType() == PieceType.KING && CASTLING_MOVES.contains(move)) {
            CastleType castleType = (move.getEndPosition().getColumn() == 7) ? CastleType.SHORT : CastleType.LONG;
            castle(move);
            revokeCastling(piece.getTeamColor(), castleType);
            return;
        }
        
        if(move.promotionPiece() != null)
            piece = new ChessPiece(piece.getTeamColor(), move.promotionPiece());
        this.addPiece(move.getEndPosition(), piece);
        this.addPiece(move.getStartPosition(), null);
        if(piece.getPieceType() == PieceType.PAWN && move.endPosition().equals(enPassantSquare)) {
            int direction = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;
            this.addPiece(new ChessPosition(move.getEndPosition().getRow() - direction, move.getEndPosition().getColumn()), null);
        }
    }
    public void castle(ChessMove move) {
        int row = move.getStartPosition().getRow();
        int endCol = move.getEndPosition().getColumn();

        // Makes move without function call to avoid infinite recursion
        this.addPiece(move.getEndPosition(), this.getPiece((move.getStartPosition())));
        this.addPiece(move.getStartPosition(), null);

        int rookEndCol = (endCol == 7) ? 6 : 4; // final rook position
        int rookStartCol = (rookEndCol == 6) ? 8 : 1; // left or right rook
        ChessMove rookMove = new ChessMove(new ChessPosition(row, rookStartCol), new ChessPosition(row, rookEndCol), null);
        makeMove(rookMove);

    }
    public void maintainCastlingPermissions(ChessMove move) {
        Collection<ChessPosition> rightRookPositions = new ArrayList<>(List.of(new ChessPosition(1,8), new ChessPosition(8,8)));
        Collection<ChessPosition> leftRookPositions = new ArrayList<>(List.of(new ChessPosition(1,1), new ChessPosition(8,1)));
        ChessPiece piece = getPiece(move.getStartPosition());
        if(move.getStartPosition().equals(getKingPosition(piece.getTeamColor())))
            revokeCastling(piece.getTeamColor(), CastleType.ALL);
        else if(rightRookPositions.contains(move.getStartPosition()))
            revokeCastling(piece.getTeamColor(), CastleType.SHORT);
        else if(leftRookPositions.contains(move.getStartPosition()))
            revokeCastling(piece.getTeamColor(), CastleType.LONG);
    }
    public enum CastleType {
        LONG,
        SHORT,
        ALL
    }
    public void revokeCastling(TeamColor color, CastleType type) {
        if(type == CastleType.ALL) {
            revokeCastling(color, CastleType.SHORT);
            revokeCastling(color, CastleType.LONG);
        }
        switch(color) {
            case WHITE -> {
                if(type == CastleType.SHORT) shortCastlingPrivilegesWhite = false;
                else longCastlingPrivilegesWhite = false;
            }
            case BLACK -> {
                if(type == CastleType.SHORT) shortCastlingPrivilegesBlack = false;
                else longCastlingPrivilegesBlack = false;
            }
        }
    }
    public boolean hasCastlingPrivileges(TeamColor color, CastleType type) {
        if(type == CastleType.ALL)
            return hasCastlingPrivileges(color, CastleType.SHORT) && hasCastlingPrivileges(color, CastleType.LONG);
        try {
            switch (color) {
                case WHITE -> {
                    if (type == CastleType.SHORT) return shortCastlingPrivilegesWhite && getPiece(new ChessPosition(1, 8)).getPieceType() == PieceType.ROOK && getPiece(new ChessPosition(1, 8)).getTeamColor() == TeamColor.WHITE;
                    else return longCastlingPrivilegesWhite && getPiece(new ChessPosition(1, 1)).getPieceType() == PieceType.ROOK && getPiece(new ChessPosition(1, 1)).getTeamColor() == TeamColor.WHITE;
                }
                case BLACK -> {
                    if (type == CastleType.SHORT) return shortCastlingPrivilegesBlack && getPiece(new ChessPosition(8, 8)).getPieceType() == PieceType.ROOK && getPiece(new ChessPosition(8, 8)).getTeamColor() == TeamColor.BLACK;
                    else return longCastlingPrivilegesBlack && getPiece(new ChessPosition(8, 1)).getPieceType() == PieceType.ROOK && getPiece(new ChessPosition(8, 1)).getTeamColor() == TeamColor.BLACK;
                }
            }
        }
        catch (NullPointerException except) {
            return false;
        }
        return false;
    }
    private boolean castlingPathClear(TeamColor color, CastleType type) {
        Collection<ChessPosition> whiteLongSquares = new ArrayList<>(List.of(new ChessPosition(1,2), new ChessPosition(1,3), new ChessPosition(1,4)));
        Collection<ChessPosition> whiteShortSquares = new ArrayList<>(List.of(new ChessPosition(1,6), new ChessPosition(1,7)));
        Collection<ChessPosition> blackLongSquares = new ArrayList<>(List.of(new ChessPosition(8,2), new ChessPosition(8,3), new ChessPosition(8,4)));
        Collection<ChessPosition> blackShortSquares = new ArrayList<>(List.of(new ChessPosition(8,6), new ChessPosition(8,7)));
        switch(type) {
            case SHORT -> {
                return (color == TeamColor.WHITE) ? !isInCheck(color) && whiteShortSquares.stream().allMatch(position -> isEmptySquare(position) && untargeted(color, position)) : !isInCheck(color) && blackShortSquares.stream().allMatch(position -> isEmptySquare(position) && untargeted(color, position));
            }
            case LONG -> {
                return (color == TeamColor.WHITE) ? !isInCheck(color) && whiteLongSquares.stream().allMatch(position -> isEmptySquare(position) && untargeted(color, position)) : !isInCheck(color) && blackLongSquares.stream().allMatch(position -> isEmptySquare(position) && untargeted(color, position));
            }
            default -> throw new IllegalArgumentException("ALL is not a valid parameter for this function");
        }
    }
    private boolean canCastle(TeamColor color, CastleType type){
        return hasCastlingPrivileges(color, type) && castlingPathClear(color, type);
    }






    public boolean isInCheck(TeamColor team) {
        ChessPosition kingPosition = getKingPosition(team);
        for (int i = 1; i <= 8; i++)
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = getPiece(position);
                if(piece == null || piece.getTeamColor() == team)
                    continue;
                for(ChessMove move : piece.pieceMoves(this, position))
                    if(move.endPosition().equals(kingPosition))
                        return true;
            }
        return false;
    }
    public boolean untargeted(TeamColor team, ChessPosition position) {
        for (int i = 1; i <= 8; i++)
            for (int j = 1; j <= 8; j++) {
                ChessPosition current = new ChessPosition(i, j);
                ChessPiece piece = getPiece(current);
                if(piece == null || piece.getTeamColor() == team)
                    continue;
                for(ChessMove move : piece.pieceMoves(this, current))
                    if(move.endPosition().equals(position))
                        return false;
            }
        return true;
    }
    public boolean isInCheckmate(TeamColor team) {
        return isInCheck(team) && noLegalMoves(team);
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
    public boolean isEmptySquare(ChessPosition position) {
        return getPiece(position) == null;
    }
    public boolean isInStalemate(TeamColor team) {
        return !isInCheck(team) && noLegalMoves(team);
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

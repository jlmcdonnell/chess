package dev.mcd.chess.ui.game.board.piece

import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import dev.mcd.chess.common.game.DirectionalMove
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout

object UpdateChessPieceState {

    context(BoardLayout)
    operator fun invoke(
        moveCount: Int,
        directionalMove: DirectionalMove,
        state: ChessPieceState,
    ): ChessPieceState {
        val (moveBackup, undo) = directionalMove
        val newState = state.copy()

        if (undo) {
            undoMove(moveBackup, state, newState, onNoChanges = { return state })
        } else {
            move(moveBackup, moveCount, state, newState, onNoChanges = { return state })
        }

        newState.squareOffset = newState.square.topLeft()
        newState.moveCount = moveCount

        return newState
    }
}

context(BoardLayout)
private inline fun undoMove(
    moveBackup: MoveBackup,
    state: ChessPieceState,
    newState: ChessPieceState,
    onNoChanges: () -> Unit,
) {
    if (state.moves.empty() || state.moves.peek() != moveBackup.move.toString()) {
        onNoChanges()
    }

    if (moveBackup.move.to == state.square) {
        if (moveBackup.move.promotion != Piece.NONE && state.piece == moveBackup.move.promotion) {
            newState.piece = moveBackup.movingPiece
            newState.square = moveBackup.move.from
        }
        if (moveBackup.capturedPiece == state.piece) {
            newState.captured = false
        } else if (moveBackup.movingPiece == state.piece) {
            newState.square = moveBackup.move.from
        }
    } else if (moveBackup.rookCastleMove?.to == state.square) {
        newState.square = moveBackup.rookCastleMove.from
    } else if (moveBackup.enPassantTarget == state.square) {
        newState.captured = false
    }
    if (state != newState) {
        newState.moves.pop()
    }
}

context(BoardLayout)
private inline fun move(
    moveBackup: MoveBackup,
    moveCount: Int,
    state: ChessPieceState,
    newState: ChessPieceState,
    onNoChanges: () -> Unit,
) {
    val move = moveBackup.move
    if (moveCount == state.moveCount) {
        onNoChanges()
    }
    if (!state.captured) {
        if (move.from == state.square) {
            newState.square = move.to
            if (move.promotion != Piece.NONE) {
                newState.piece = move.promotion
            }
        } else if (state.piece == moveBackup.capturedPiece && state.square == moveBackup.capturedSquare) {
            newState.captured = true
        } else if (moveBackup.rookCastleMove?.from == state.square) {
            newState.square = moveBackup.rookCastleMove.to
        } else if (moveBackup.enPassantTarget == state.square) {
            newState.square = moveBackup.enPassantTarget
        }
    }
    if (newState != state) {
        newState.moves.push(moveBackup.move.toString())
    }
}

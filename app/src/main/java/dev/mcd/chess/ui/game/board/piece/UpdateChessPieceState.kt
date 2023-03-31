package dev.mcd.chess.ui.game.board.piece

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.DirectionalMove
import dev.mcd.chess.ui.extension.topLeft

/**
 *      _                         _
 *     |_|                       |_|
 *     | |         /^^^\         | |
 *    _| |_      (| "o" |)      _| |_
 *  _| | | | _    (_---_)    _ | | | |_
 * | | | | |' |    _| |_    | `| | | | |
 * \          /   /     \   \          /
 *  \        /  / /(. .)\ \  \        /
 *    \    /  / /  | . |  \ \  \    /
 *      \  \/ /    ||-||    \ \/  /
 *        \_/      || ||      \_/
 *                 () ()
 *                 || ||
 *                ooO Ooo
 */

object UpdateChessPieceState {
    operator fun invoke(
        perspective: Side,
        size: Float,
        directionalMove: DirectionalMove,
        state: ChessPieceState,
    ): ChessPieceState {
        val (moveBackup, undo) = directionalMove
        val move = moveBackup.move
        val newState = state.copy()

        if (undo) {
            if (state.moves.empty() || state.moves.peek() != move) {
                return state
            }

            if (move.to == state.square) {
                if (move.promotion != Piece.NONE && state.piece == move.promotion) {
                    newState.piece = moveBackup.movingPiece
                    newState.square = move.from
                }
                if (moveBackup.capturedPiece == state.piece) {
                    newState.captured = false
                } else if (moveBackup.movingPiece == state.piece) {
                    newState.square = move.from
                }
            } else if (moveBackup.rookCastleMove?.to == state.square) {
                newState.square = moveBackup.rookCastleMove.from
            } else if (moveBackup.enPassantTarget == state.square) {
                newState.captured = false
            }
            if (newState != state) {
                newState.moves.pop()
            }
        } else {
            if (!state.moves.empty() && state.moves.peek() == move) {
                return state
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
                newState.moves.push(moveBackup.move)
            }
        }

        newState.squareOffset = newState.square.topLeft(perspective, size)

        return newState
    }
}

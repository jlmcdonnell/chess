package dev.mcd.chess.domain

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.BoardEventType
import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class GameSession(
    val id: String,
    val board: Board,
    val self: Player,
    val opponent: Player,
    val selfSide: Side,
) {
    private val _pieceUpdates = MutableStateFlow(board.boardToArray().toList())
    private val _moves = MutableStateFlow<Move?>(null)

    val pieceUpdates = _pieceUpdates
    val moves = _moves

    init {
        val updatePieces = suspend { pieceUpdates.emit(board.boardToArray().toList()) }

        board.addEventListener(BoardEventType.ON_LOAD) {
            runBlocking {
                updatePieces()
            }
        }
        board.addEventListener(BoardEventType.ON_MOVE) {
            runBlocking {
                updatePieces()
                _moves.emit(it as Move)
            }
        }
        board.addEventListener(BoardEventType.ON_UNDO_MOVE) {
            runBlocking {
                updatePieces()
                _moves.emit((it as MoveBackup).move)
            }
        }
    }
}

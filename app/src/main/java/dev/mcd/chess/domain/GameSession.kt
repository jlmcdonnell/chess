package dev.mcd.chess.domain

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.BoardEventType
import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow

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
        board.addEventListener(BoardEventType.ON_LOAD) {
            _pieceUpdates.value = board.boardToArray().toList()
        }
        board.addEventListener(BoardEventType.ON_MOVE) {
            _pieceUpdates.value = board.boardToArray().toList()
            _moves.value = it as Move
        }
        board.addEventListener(BoardEventType.ON_UNDO_MOVE) {
            _pieceUpdates.value = board.boardToArray().toList()
            _moves.value = (it as MoveBackup).move
        }
    }
}

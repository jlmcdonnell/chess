package dev.mcd.chess.ui.puzzle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.R
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.compose.StableHolder
import dev.mcd.chess.ui.game.GameView
import dev.mcd.chess.ui.game.board.interaction.GameSettings
import dev.mcd.chess.ui.game.board.sounds.BoardSounds
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PuzzleScreen(
    puzzleViewModel: PuzzleViewModel = hiltViewModel(),
) {
    Scaffold {
        Column(
            modifier = Modifier.padding(it),
        ) {
            val sessionManager = LocalGameSession.current

            val state by puzzleViewModel.collectAsState()

            LaunchedEffect(state.session) {
                if (state.session != null) {
                    sessionManager.updateSession(state.session!!)
                }
            }

            state.session?.let { session ->
                GameView(
                    gameHolder = StableHolder(session),
                    settings = GameSettings(allowResign = false, showCapturedPieces = false),
                    onMove = { move ->
                        puzzleViewModel.onMove(move)
                    },
                    sounds = { BoardSounds(enableNotify = false) },
                )
            }
            if (state.loading && !state.completed && !state.failed) {
                Loading(stringResource(id = R.string.finding_puzzle))
            }
            if (state.completed) {
                PuzzleCompleted(
                    rating = state.puzzleRating,
                    loading = state.loading,
                ) {
                    puzzleViewModel.onNextPuzzle()
                }
            } else if (state.failed) {
                PuzzleFailed(
                    onSkip = { puzzleViewModel.onSkip() },
                    onRetry = { puzzleViewModel.onRetry() },
                    loading = state.loading,
                )
            }
        }
    }
}

package dev.mcd.chess.ui.puzzle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.R
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.compose.StableHolder
import dev.mcd.chess.ui.game.GameView
import dev.mcd.chess.ui.game.board.interaction.GameSettings
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PuzzleScreen(
    viewModel: PuzzleViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    var showingPuzzleOptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            state.session?.let {
                TopAppBar(
                    title = {},
                    actions = {
                        IconButton(onClick = { showingPuzzleOptions = !showingPuzzleOptions }) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.Tune),
                                contentDescription = stringResource(id = R.string.puzzle_options_desc),
                            )
                        }
                    },
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            val sessionManager = LocalGameSession.current

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
                        viewModel.onMove(move)
                    },
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
                    viewModel.onNextPuzzle()
                }
            } else if (state.failed) {
                PuzzleFailed(
                    onSkip = { viewModel.onSkip() },
                    onRetry = { viewModel.onRetry() },
                    loading = state.loading,
                )
            }
        }
    }

    if (showingPuzzleOptions) {
        PuzzleOptionsDialog(
            modifier = Modifier.padding(24.dp),
            ratingRange = state.ratingRange,
            maxRatingRange = state.maxRatingRange,
            onRatingRangeChanged = { range -> viewModel.onRatingRangeChanged(range) },
            onDismissRequest = { showingPuzzleOptions = false },
        )
    }
}

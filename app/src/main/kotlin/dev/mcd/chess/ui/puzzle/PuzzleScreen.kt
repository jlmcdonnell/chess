package dev.mcd.chess.ui.puzzle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
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
    onNavigateBack: () -> Unit,
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
                    onResign = {
                    },
                    sounds = { BoardSounds(enableNotify = false) },
                )
            }
            if (state.loading) {
                Loading(stringResource(id = R.string.finding_puzzle))
            }
            if (state.completed) {
                PuzzleCompleted(rating = state.puzzleRating) {
                    puzzleViewModel.onNextPuzzle()
                }
            } else if (state.failed) {
                PuzzleFailed {
                    puzzleViewModel.onRetry()
                }
            }
        }
    }
}

@Composable
fun PuzzleFailed(onRetry: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
    ) {
        Column(
            Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
        ) {
            Text(text = stringResource(id = R.string.incorrect))
            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = onRetry,
            ) {
                Text(stringResource(id = R.string.retry))
            }
        }
    }
}

@Composable
fun PuzzleCompleted(
    rating: Int,
    onNext: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
    ) {
        Column {
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Rounded.Checklist),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = stringResource(id = R.string.correct),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Text(
                modifier = Modifier.padding(start = 64.dp),
                style = MaterialTheme.typography.bodySmall,
                text = buildAnnotatedString {
                    append(stringResource(R.string.puzzle_rating, rating))
                },
            )
            TextButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 16.dp),
                onClick = onNext,
            ) {
                Text(stringResource(id = R.string.next))
            }
        }
    }
}

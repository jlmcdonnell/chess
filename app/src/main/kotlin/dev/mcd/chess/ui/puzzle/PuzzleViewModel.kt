package dev.mcd.chess.ui.puzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleInput
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleOutput
import dev.mcd.chess.feature.puzzle.domain.usecase.GetPuzzleOptions
import dev.mcd.chess.feature.puzzle.maxPuzzleRatingRange
import dev.mcd.chess.feature.sound.domain.GameSessionSoundWrapper
import dev.mcd.chess.feature.sound.domain.SoundSettings
import dev.mcd.chess.online.domain.entity.Puzzle
import dev.mcd.chess.online.domain.usecase.GetRandomPuzzle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val getRandomPuzzle: GetRandomPuzzle,
    private val createPuzzleSession: CreatePuzzleSession,
    private val soundWrapper: GameSessionSoundWrapper,
    private val appPreferences: AppPreferences,
    private val getPuzzleOptions: GetPuzzleOptions,
) : ViewModel(), ContainerHost<PuzzleViewModel.State, PuzzleViewModel.SideEffect> {

    private var puzzleInput: PuzzleInput? = null

    override val container = container<State, SideEffect>(State()) {
        intent {
            reduce {
                state.copy(loading = true)
            }

            runCatching {
                val options = getPuzzleOptions()
                val puzzle = getRandomPuzzle(ratingRange = options.ratingRange)
                reduce {
                    state.copy(
                        loading = false,
                        puzzleRating = puzzle.rating,
                        ratingRange = options.ratingRange,
                    )
                }
                startPuzzle(puzzle)
            }.onFailure {
                Timber.e(it, "Retrieving puzzle")
                reduce { state.copy(loading = false) }
            }
        }
    }

    private fun startPuzzle(puzzle: Puzzle) = intent {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.Default) {
                    val (input, puzzleOutput) = createPuzzleSession(puzzle)
                    puzzleInput = input
                    puzzleOutput.collectLatest {
                        handlePuzzleOutput(it)
                    }
                }
            }.onFailure {
                Timber.e(it, "Retrieving puzzle")
                reduce { state.copy(loading = false) }
            }.getOrNull() ?: return@launch
        }
    }

    fun onSkip() {
        intent {
            puzzleInput?.close()
        }
        onNextPuzzle()
    }

    fun onRetry() {
        intent {
            puzzleInput?.retry()
            reduce { state.copy(failed = false) }
        }
    }

    fun onMove(move: Move) {
        intent {
            runCatching {
                puzzleInput?.move(move.toString())
            }.onFailure {
                Timber.e(it, "Sending move to puzzle channel")
            }
        }
    }

    fun onNextPuzzle() {
        intent {
            puzzleInput?.close()
            reduce {
                state.copy(
                    loading = true,
                )
            }
            runCatching {
                val puzzle = getRandomPuzzle(appPreferences.puzzleRatingRange())
                reduce {
                    state.copy(
                        loading = false,
                        completed = false,
                        failed = false,
                        puzzleRating = puzzle.rating,
                    )
                }
                startPuzzle(puzzle)
            }.onFailure {
                Timber.e(it, "Retrieving puzzle")
                reduce { state.copy(loading = false) }
            }
        }
    }

    fun onRatingRangeChanged(range: IntRange) {
        intent {
            appPreferences.setPuzzleRatingRange(range)
            reduce {
                state.copy(ratingRange = range)
            }
        }
    }

    private fun handlePuzzleOutput(output: PuzzleOutput) {
        intent {
            when (output) {
                is PuzzleOutput.Session -> handleNewSession(output.session)
                is PuzzleOutput.Completed -> reduce { state.copy(completed = true) }
                is PuzzleOutput.Failed -> reduce { state.copy(failed = true, completed = false) }
                is PuzzleOutput.NoMovesLeft -> handlePuzzleOutputError(output)
                is PuzzleOutput.NotUserTurn -> handlePuzzleOutputError(output)
                is PuzzleOutput.ErrorMoveInvalid -> handlePuzzleOutputError(output)
                is PuzzleOutput.PlayerToMove -> Unit
            }
        }
    }

    private fun handlePuzzleOutputError(error: PuzzleOutput) {
        Timber.e("Puzzle Error: $error")
    }

    private fun handleNewSession(session: GameSession) {
        intent {
            reduce { state.copy(session = session) }
            val soundSettings = SoundSettings(
                enabled = appPreferences.soundsEnabled(),
                enableNotify = false,
            )
            soundWrapper.attachSession(session, soundSettings)
        }
    }

    data class State(
        val session: GameSession? = null,
        val completed: Boolean = false,
        val puzzleRating: Int = 0,
        val failed: Boolean = false,
        val loading: Boolean = false,
        val ratingRange: IntRange = maxPuzzleRatingRange,
        val maxRatingRange: IntRange = maxPuzzleRatingRange,
    )

    object SideEffect
}

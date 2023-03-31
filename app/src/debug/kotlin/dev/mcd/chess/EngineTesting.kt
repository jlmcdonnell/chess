package dev.mcd.chess

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.github.bhlangonijr.chesslib.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.common.engine.ChessEngine
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.compose.collectSideEffect
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class EngineTestingViewModel @Inject constructor(
    val engine: ChessEngine,
) : ViewModel(), ContainerHost<Unit, String> {

    override val container: Container<Unit, String> = container(Unit) {
        intent {
            repeatOnSubscription {
                engine.startAndWait()
            }
        }
    }

    fun getMove() = intent {
        val move = engine.getMove(Constants.startStandardFENPosition, 0, 1)
        postSideEffect(move)
    }
}

@Composable
fun EngineTesting(
    vm: EngineTestingViewModel = hiltViewModel(),
    onRestart: () -> Unit,
) {
    var ready by remember { mutableStateOf(false) }
    var move by remember { mutableStateOf("") }

    vm.collectSideEffect { move = it }

    LaunchedEffect(Unit) {
        vm.engine.awaitReady()
        ready = true
    }

    val readyText = if (ready) "Engine ready" else "Engine not ready"

    Column {
        Text(readyText)
        if (move.isNotBlank()) {
            Text(move)
        } else {
            Text("No move")
        }
        Row {
            OutlinedButton(onClick = { vm.getMove() }) {
                Text(text = "Move")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(onClick = { onRestart() }) {
                Text(text = "Restart")
            }
        }
    }
}

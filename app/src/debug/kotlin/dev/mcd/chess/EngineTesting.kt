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
import dev.mcd.chess.engine.lc0.FenParam
import dev.mcd.chess.engine.lc0.MaiaWeights
import dev.mcd.chess.feature.engine.BotEngineProxy
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
    val engine: BotEngineProxy,
) : ViewModel(), ContainerHost<Unit, String> {

    override val container: Container<Unit, String> = container(Unit) {
        intent {
            repeatOnSubscription {
                engine.start(MaiaWeights.ELO_1100)
            }
        }
    }

    fun getMove() = intent {
        val move = engine.getMove(FenParam(Constants.startStandardFENPosition))
        postSideEffect(move)
    }
}

@Composable
fun EngineTesting() {
    var restart by remember { mutableStateOf(false) }

    LaunchedEffect(restart) {
        restart = false
    }

    if (!restart) {
        EngineTest {
            restart = true
        }
    }
}

@Composable
fun EngineTest(
    vm: EngineTestingViewModel = hiltViewModel(),
    onRestart: () -> Unit,
) {
    var move by remember { mutableStateOf("") }

    vm.collectSideEffect { move = it }

    Column {
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

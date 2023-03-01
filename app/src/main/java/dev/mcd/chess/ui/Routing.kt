package dev.mcd.chess.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.mcd.chess.ui.screen.botgame.BotGameScreen
import dev.mcd.chess.ui.screen.botselection.BotSelectionScreen
import dev.mcd.chess.ui.screen.choosemode.ChooseModeScreen
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameScreen
import dev.mcd.chess.ui.screen.settings.SettingsScreen

@Composable
fun Routing() {
    val navController = rememberNavController()
    NavHost(navController, "/choosemode") {
        composable("/choosemode") {
            ChooseModeScreen(
                onPlayOnline = { navController.navigate("/game/online") },
                onPlayBot = { navController.navigate("/selectbot") },
                onNavigateSettings = { navController.navigate("/settings") },
                onNavigateExistingGame = { navController.navigate("/game/online?gameId=$it") }
            )
        }
        composable("/selectbot") {
            BotSelectionScreen(
                onBotSelected = { bot, side -> navController.navigate("/game/bot/$bot/$side") },
                onDismiss = { navController.popBackStack() },
            )
        }
        composable(
            "/game/bot/{bot}/{side}",
            listOf(
                navArgument("bot") { type = NavType.StringType },
                navArgument("side") { type = NavType.StringType }
            )

        ) {
            BotGameScreen {
                navController.popBackStack()
            }
        }
        composable(
            "/game/online?gameId={gameId}",
            listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            OnlineGameScreen {
                navController.popBackStack()
            }
        }
        composable("/settings") {
            SettingsScreen {
                navController.popBackStack()
            }
        }
    }
}

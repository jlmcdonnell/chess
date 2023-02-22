package dev.mcd.chess.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.mcd.chess.ui.screen.botgame.BotGameScreen
import dev.mcd.chess.ui.screen.botselection.SelectBotScreen
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameScreen

@Composable
fun Routing() {
    val navController = rememberNavController()
    NavHost(navController, "/game/online") {
        composable("/selectbot") {
            SelectBotScreen { bot, side -> navController.navigate("/game/bot/$bot/$side") }
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
        composable("/game/online") {
            OnlineGameScreen {
                navController.popBackStack()
            }
        }
    }
}

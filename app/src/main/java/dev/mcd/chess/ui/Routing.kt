package dev.mcd.chess.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.mcd.chess.ui.botselection.SelectBotScreen
import dev.mcd.chess.ui.screen.GameScreen

@Composable
fun Routing() {
    val navController = rememberNavController()
    NavHost(navController, "/selectbot") {
        composable("/selectbot") {
            SelectBotScreen { bot -> navController.navigate("/game/bot/$bot") }
        }
        composable("/game/bot/{bot}", listOf(navArgument("bot") { type = NavType.StringType })) {
            GameScreen()
        }
    }
}

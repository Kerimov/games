package com.vadim.retrophonegames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vadim.retrophonegames.games.bantumi.BantumiGameScreen
import com.vadim.retrophonegames.games.bounce.BounceGameScreen
import com.vadim.retrophonegames.games.pairs.PairsGameScreen
import com.vadim.retrophonegames.games.racing.RacingGameScreen
import com.vadim.retrophonegames.games.snake.SnakeGameScreen
import com.vadim.retrophonegames.games.spaceimpact.SpaceImpactGameScreen
import com.vadim.retrophonegames.ui.GameMenuScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetroGamesApp()
        }
    }
}

@Composable
fun RetroGamesApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "menu",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("menu") {
            GameMenuScreen(
                onGameSelected = { gameId ->
                    navController.navigate(gameId)
                }
            )
        }
        composable("snake") {
            SnakeGameScreen(onBack = { navController.popBackStack() })
        }
        composable("spaceimpact") {
            SpaceImpactGameScreen(onBack = { navController.popBackStack() })
        }
        composable("pairs") {
            PairsGameScreen(onBack = { navController.popBackStack() })
        }
        composable("bantumi") {
            BantumiGameScreen(onBack = { navController.popBackStack() })
        }
        composable("racing") {
            RacingGameScreen(onBack = { navController.popBackStack() })
        }
        composable("bounce") {
            BounceGameScreen(onBack = { navController.popBackStack() })
        }
    }
}

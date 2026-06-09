package com.vadim.classicmobilegames

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
import com.vadim.classicmobilegames.games.bantumi.BantumiGameScreen
import com.vadim.classicmobilegames.games.bounce.BounceGameScreen
import com.vadim.classicmobilegames.games.pairs.PairsGameScreen
import com.vadim.classicmobilegames.games.racing.RacingGameScreen
import com.vadim.classicmobilegames.games.snake.SnakeGameScreen
import com.vadim.classicmobilegames.games.spaceimpact.SpaceImpactGameScreen
import com.vadim.classicmobilegames.ui.GameMenuScreen

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

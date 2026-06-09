package com.vadim.retrophonegames.games.racing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.vadim.retrophonegames.ui.components.DpadControls
import com.vadim.retrophonegames.ui.components.PhoneFrame
import com.vadim.retrophonegames.ui.theme.RetroColors
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Obstacle(var lane: Int, var y: Float)

@Composable
fun RacingGameScreen(onBack: () -> Unit) {
    val lanes = 3
    val screenH = 160f

    var playerLane by remember { mutableIntStateOf(1) }
    val obstacles = remember { mutableStateListOf<Obstacle>() }
    var score by remember { mutableIntStateOf(0) }
    var speed by remember { mutableStateOf(2f) }
    var gameOver by remember { mutableStateOf(false) }
    var tick by remember { mutableIntStateOf(0) }

    fun reset() {
        playerLane = 1
        obstacles.clear()
        score = 0
        speed = 2f
        gameOver = false
        tick = 0
    }

    LaunchedEffect(gameOver) {
        while (!gameOver) {
            delay(33)
            tick++
            score++

            obstacles.forEach { it.y += speed }
            obstacles.removeAll { it.y > screenH }

            if (tick % 40 == 0) {
                val lane = Random.nextInt(lanes)
                obstacles.add(Obstacle(lane, -20f))
            }

            if (tick % 200 == 0 && speed < 6f) speed += 0.3f

            obstacles.forEach { obs ->
                if (obs.lane == playerLane && obs.y > screenH - 40 && obs.y < screenH - 10) {
                    gameOver = true
                }
            }
        }
    }

    PhoneFrame(
        title = "Racing",
        score = "Дист: $score"
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val laneW = size.width / lanes
            val sy = size.height / screenH

            drawRect(RetroColors.ScreenBg, size = size)

            for (l in 0 until lanes) {
                val x = l * laneW + laneW / 2
                for (i in 0..10) {
                    val dashY = ((tick * speed * sy + i * 30) % size.height)
                    drawRect(
                        RetroColors.PixelLight.copy(alpha = 0.3f),
                        topLeft = Offset(x - 2, dashY),
                        size = Size(4f, 12f)
                    )
                }
            }

            obstacles.forEach { obs ->
                drawRect(
                    RetroColors.Pixel,
                    topLeft = Offset(obs.lane * laneW + 4, obs.y * sy),
                    size = Size(laneW - 8, 18f * sy)
                )
            }

            drawRect(
                RetroColors.PixelLight,
                topLeft = Offset(playerLane * laneW + 6, size.height - 36),
                size = Size(laneW - 12, 28f)
            )

            if (gameOver) {
                drawRect(RetroColors.ScreenDark.copy(alpha = 0.7f), size = size)
            }
        }
    }

    DpadControls(
        onLeft = { if (!gameOver && playerLane > 0) playerLane-- },
        onRight = { if (!gameOver && playerLane < lanes - 1) playerLane++ },
        onCenter = { if (gameOver) reset() },
        onBack = onBack,
        centerLabel = if (gameOver) "↻" else "—",
        showDpad = true
    )
}

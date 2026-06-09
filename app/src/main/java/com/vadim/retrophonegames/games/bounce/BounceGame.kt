package com.vadim.retrophonegames.games.bounce

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

data class Platform(val x: Float, val y: Float, val w: Float)
@Composable
fun BounceGameScreen(onBack: () -> Unit) {
    val screenW = 120f
    val screenH = 160f
    val ballR = 5f

    val platforms = remember {
        listOf(
            Platform(0f, screenH - 8, screenW),
            Platform(0f, 130f, 40f),
            Platform(70f, 110f, 50f),
            Platform(10f, 90f, 35f),
            Platform(60f, 70f, 45f),
            Platform(5f, 50f, 30f),
            Platform(55f, 30f, 50f),
            Platform(20f, 12f, 30f),
        )
    }

    val ringPositions = remember {
        listOf(
            15f to 122f,
            85f to 102f,
            25f to 82f,
            75f to 62f,
            15f to 42f,
            70f to 22f,
        )
    }
    var collectedRings by remember { mutableStateOf(setOf<Int>()) }

    var ballX by remember { mutableStateOf(20f) }
    var ballY by remember { mutableStateOf(screenH - 20f) }
    var velX by remember { mutableStateOf(0f) }
    var velY by remember { mutableStateOf(0f) }
    var onGround by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var gameOver by remember { mutableStateOf(false) }
    var won by remember { mutableStateOf(false) }

    fun reset() {
        ballX = 20f
        ballY = screenH - 20f
        velX = 0f
        velY = 0f
        score = 0
        level = 1
        gameOver = false
        won = false
        collectedRings = setOf()
    }

    LaunchedEffect(gameOver, won) {
        while (!gameOver && !won) {
            delay(16)

            velY += 0.25f
            ballX += velX
            ballY += velY
            onGround = false

            if (ballX < ballR) { ballX = ballR; velX = -velX * 0.5f }
            if (ballX > screenW - ballR) { ballX = screenW - ballR; velX = -velX * 0.5f }

            if (ballY > screenH - ballR) {
                ballY = screenH - ballR
                velY = -velY * 0.6f
                velX *= 0.9f
                if (kotlin.math.abs(velY) < 1f) {
                    velY = 0f
                    onGround = true
                }
            }

            platforms.forEach { p ->
                if (ballX + ballR > p.x && ballX - ballR < p.x + p.w &&
                    ballY + ballR >= p.y && ballY + ballR <= p.y + 6 && velY > 0
                ) {
                    ballY = p.y - ballR
                    velY = 0f
                    onGround = true
                }
            }

            ringPositions.forEachIndexed { index, (rx, ry) ->
                if (index !in collectedRings) {
                    val dx = ballX - rx
                    val dy = ballY - ry
                    if (dx * dx + dy * dy < 100) {
                        collectedRings = collectedRings + index
                        score += 50
                    }
                }
            }

            if (ballY < -10) gameOver = true

            if (collectedRings.size == ringPositions.size) {
                won = true
            }
        }
    }

    PhoneFrame(
        title = "Bounce",
        score = "★$score"
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sx = size.width / screenW
            val sy = size.height / screenH

            drawRect(RetroColors.ScreenBg, size = size)

            platforms.forEach { p ->
                drawRect(
                    RetroColors.PixelLight,
                    topLeft = Offset(p.x * sx, p.y * sy),
                    size = Size(p.w * sx, 4f * sy)
                )
            }

            ringPositions.forEachIndexed { index, (rx, ry) ->
                if (index !in collectedRings) {
                    drawCircle(
                        color = RetroColors.Pixel,
                        radius = 6f * sx,
                        center = Offset(rx * sx, ry * sy),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }
            }

            drawCircle(
                color = RetroColors.Pixel,
                radius = ballR * sx,
                center = Offset(ballX * sx, ballY * sy)
            )

            if (gameOver || won) {
                drawRect(RetroColors.ScreenDark.copy(alpha = 0.7f), size = size)
            }
        }
    }

    DpadControls(
        onLeft = { if (!gameOver && !won) velX = -2.5f },
        onRight = { if (!gameOver && !won) velX = 2.5f },
        onCenter = {
            when {
                gameOver || won -> reset()
                onGround -> velY = -5.5f
            }
        },
        onBack = onBack,
        centerLabel = when {
            gameOver || won -> "↻"
            else -> "↑"
        }
    )
}

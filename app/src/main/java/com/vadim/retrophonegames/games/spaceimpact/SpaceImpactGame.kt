package com.vadim.retrophonegames.games.spaceimpact

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

data class Entity(var x: Float, var y: Float, val w: Float, val h: Float)

@Composable
fun SpaceImpactGameScreen(onBack: () -> Unit) {
    val screenW = 160f
    val screenH = 120f

    var playerY by remember { mutableStateOf(screenH / 2) }
    val bullets = remember { mutableStateListOf<Entity>() }
    val enemies = remember { mutableStateListOf<Entity>() }
    val enemyBullets = remember { mutableStateListOf<Entity>() }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var gameOver by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var tick by remember { mutableIntStateOf(0) }

    fun reset() {
        playerY = screenH / 2
        bullets.clear()
        enemies.clear()
        enemyBullets.clear()
        score = 0
        lives = 3
        gameOver = false
        paused = false
        tick = 0
    }

    LaunchedEffect(gameOver, paused) {
        while (!gameOver && !paused) {
            delay(33)
            tick++

            bullets.forEach { it.x += 6f }
            bullets.removeAll { it.x > screenW }

            enemies.forEach { it.x -= 1.5f }
            if (tick % 60 == 0 && enemies.size < 6) {
                enemies.add(Entity(screenW, Random.nextFloat() * (screenH - 16), 12f, 12f))
            }
            enemies.removeAll { it.x < -20 }

            if (tick % 45 == 0) {
                enemies.randomOrNull()?.let { e ->
                    enemyBullets.add(Entity(e.x, e.y + 4, 4f, 2f))
                }
            }
            enemyBullets.forEach { it.x -= 4f }
            enemyBullets.removeAll { it.x < -10 }

            val playerX = 12f
            val playerH = 10f
            bullets.forEach { b ->
                enemies.removeAll { e ->
                    val hit = b.x < e.x + e.w && b.x + b.w > e.x &&
                        b.y < e.y + e.h && b.y + b.h > e.y
                    if (hit) score += 10
                    hit
                }
            }

            enemyBullets.removeAll { eb ->
                val hit = eb.x < playerX + 14 && eb.x + eb.w > playerX &&
                    eb.y < playerY + playerH && eb.y + eb.h > playerY
                if (hit) {
                    lives--
                    if (lives <= 0) gameOver = true
                }
                hit
            }

            enemies.removeAll { e ->
                val hit = e.x < playerX + 14 && e.x + e.w > playerX &&
                    e.y < playerY + playerH && e.y + e.h > playerY
                if (hit) {
                    lives--
                    if (lives <= 0) gameOver = true
                }
                hit
            }
        }
    }

    PhoneFrame(
        title = "Space Impact",
        score = "★$score ♥$lives"
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sx = size.width / screenW
            val sy = size.height / screenH

            drawRect(RetroColors.ScreenBg, size = size)

            for (i in 0..8) {
                val starX = ((tick * 2 + i * 37) % screenW.toInt()).toFloat()
                val starY = (i * 14f) % screenH
                drawRect(
                    RetroColors.PixelLight.copy(alpha = 0.4f),
                    topLeft = Offset(starX * sx, starY * sy),
                    size = Size(2f * sx, 2f * sy)
                )
            }

            drawRect(
                RetroColors.Pixel,
                topLeft = Offset(12f * sx, playerY * sy),
                size = Size(14f * sx, 10f * sy)
            )

            bullets.forEach { b ->
                drawRect(
                    RetroColors.Pixel,
                    topLeft = Offset(b.x * sx, b.y * sy),
                    size = Size(b.w * sx, b.h * sy)
                )
            }

            enemies.forEach { e ->
                drawRect(
                    RetroColors.PixelLight,
                    topLeft = Offset(e.x * sx, e.y * sy),
                    size = Size(e.w * sx, e.h * sy)
                )
            }

            enemyBullets.forEach { eb ->
                drawRect(
                    RetroColors.Pixel,
                    topLeft = Offset(eb.x * sx, eb.y * sy),
                    size = Size(eb.w * sx, eb.h * sy)
                )
            }

            if (gameOver) {
                drawRect(RetroColors.ScreenDark.copy(alpha = 0.7f), size = size)
            }
        }
    }

    DpadControls(
        onUp = { if (!gameOver) playerY = (playerY - 8f).coerceAtLeast(0f) },
        onDown = { if (!gameOver) playerY = (playerY + 8f).coerceAtMost(screenH - 10) },
        onCenter = {
            when {
                gameOver -> reset()
                paused -> paused = false
                else -> {
                    bullets.add(Entity(26f, playerY + 3, 6f, 3f))
                }
            }
        },
        onBack = onBack,
        centerLabel = if (gameOver) "↻" else "🔫",
        showDpad = true
    )
}

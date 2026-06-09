package com.vadim.retrophonegames.games.snake

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
import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }

data class Point(val x: Int, val y: Int)

@Composable
fun SnakeGameScreen(onBack: () -> Unit) {
    val gridW = 14
    val gridH = 16
    var snake by remember { mutableStateOf(listOf(Point(7, 8), Point(6, 8), Point(5, 8))) }
    var direction by remember { mutableStateOf(Direction.RIGHT) }
    var nextDirection by remember { mutableStateOf(Direction.RIGHT) }
    var food by remember { mutableStateOf(Point(10, 8)) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }

    fun spawnFood(body: List<Point>): Point {
        val free = mutableListOf<Point>()
        for (x in 0 until gridW) {
            for (y in 0 until gridH) {
                val p = Point(x, y)
                if (p !in body) free.add(p)
            }
        }
        return free.random()
    }

    fun reset() {
        snake = listOf(Point(7, 8), Point(6, 8), Point(5, 8))
        direction = Direction.RIGHT
        nextDirection = Direction.RIGHT
        food = Point(10, 8)
        score = 0
        gameOver = false
        paused = false
    }

    LaunchedEffect(gameOver, paused) {
        while (!gameOver && !paused) {
            delay(180)
            direction = nextDirection
            val head = snake.first()
            val newHead = when (direction) {
                Direction.UP -> Point(head.x, head.y - 1)
                Direction.DOWN -> Point(head.x, head.y + 1)
                Direction.LEFT -> Point(head.x - 1, head.y)
                Direction.RIGHT -> Point(head.x + 1, head.y)
            }
            if (newHead.x < 0 || newHead.x >= gridW || newHead.y < 0 || newHead.y >= gridH || newHead in snake) {
                gameOver = true
            } else {
                val ate = newHead == food
                snake = listOf(newHead) + snake.take(if (ate) snake.size else snake.size - 1)
                if (ate) {
                    score++
                    food = spawnFood(snake)
                }
            }
        }
    }

    PhoneFrame(
        title = "Snake II",
        score = "Очки: $score"
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / gridW
            val cellH = size.height / gridH
            val cell = minOf(cellW, cellH)

            drawRect(RetroColors.ScreenBg, size = size)

            snake.forEachIndexed { i, p ->
                drawRect(
                    color = if (i == 0) RetroColors.Pixel else RetroColors.PixelLight,
                    topLeft = Offset(p.x * cell + 1, p.y * cell + 1),
                    size = Size(cell - 2, cell - 2)
                )
            }

            drawRect(
                color = RetroColors.Pixel,
                topLeft = Offset(food.x * cell + cell * 0.2f, food.y * cell + cell * 0.2f),
                size = Size(cell * 0.6f, cell * 0.6f)
            )

            if (gameOver) {
                drawRect(
                    color = RetroColors.ScreenDark.copy(alpha = 0.7f),
                    size = size
                )
            }
        }
    }

    DpadControls(
        onUp = {
            if (!gameOver && direction != Direction.DOWN) nextDirection = Direction.UP
        },
        onDown = {
            if (!gameOver && direction != Direction.UP) nextDirection = Direction.DOWN
        },
        onLeft = {
            if (!gameOver && direction != Direction.RIGHT) nextDirection = Direction.LEFT
        },
        onRight = {
            if (!gameOver && direction != Direction.LEFT) nextDirection = Direction.RIGHT
        },
        onCenter = {
            if (gameOver) reset() else paused = !paused
        },
        onBack = onBack,
        centerLabel = if (gameOver) "↻" else if (paused) "▶" else "❚❚"
    )
}
